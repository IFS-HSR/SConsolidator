package ch.hsr.ifs.sconsolidator.core.helper;

import static ch.hsr.ifs.sconsolidator.core.base.utils.CollectionUtil.list;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.eclipse.cdt.core.CCProjectNature;
import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.CProjectNature;
import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.settings.model.CMacroEntry;
import org.eclipse.cdt.core.settings.model.CSourceEntry;
import org.eclipse.cdt.core.settings.model.ICConfigExtensionReference;
import org.eclipse.cdt.core.settings.model.ICConfigurationDescription;
import org.eclipse.cdt.core.settings.model.ICFolderDescription;
import org.eclipse.cdt.core.settings.model.ICLanguageSetting;
import org.eclipse.cdt.core.settings.model.ICLanguageSettingEntry;
import org.eclipse.cdt.core.settings.model.ICMacroEntry;
import org.eclipse.cdt.core.settings.model.ICProjectDescription;
import org.eclipse.cdt.core.settings.model.ICProjectDescriptionManager;
import org.eclipse.cdt.core.settings.model.ICSettingEntry;
import org.eclipse.cdt.core.settings.model.ICSourceEntry;
import org.eclipse.cdt.managedbuilder.core.BuildException;
import org.eclipse.cdt.managedbuilder.core.IBuilder;
import org.eclipse.cdt.managedbuilder.core.IConfiguration;
import org.eclipse.cdt.managedbuilder.core.IManagedBuildInfo;
import org.eclipse.cdt.managedbuilder.core.IOption;
import org.eclipse.cdt.managedbuilder.core.IProjectType;
import org.eclipse.cdt.managedbuilder.core.ITool;
import org.eclipse.cdt.managedbuilder.core.IToolChain;
import org.eclipse.cdt.managedbuilder.core.ManagedBuildManager;
import org.eclipse.cdt.managedbuilder.internal.core.Configuration;
import org.eclipse.cdt.managedbuilder.internal.core.ManagedProject;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;

import ch.hsr.ifs.sconsolidator.core.SConsNatureTypes;
import ch.hsr.ifs.sconsolidator.core.base.utils.IOUtil;

@SuppressWarnings("restriction")
public class CppManagedTestProject {
  public static String SRC_FOLDER_NAME = "src";
  public static String TEST_PROJECT_NAME = "TestProject";
  public static String MAIN_FILE_NAME = "main.cpp";
  private final boolean withSConsSupport;
  private final boolean withBuildErrors;
  private IProject project;
  private IConfiguration config;

  public CppManagedTestProject(boolean withSConsSupport) throws Exception {
    this(withSConsSupport, false);
  }

  public CppManagedTestProject(boolean withSConsSupport, boolean withBuildErrors) throws Exception {
    this.withSConsSupport = withSConsSupport;
    this.withBuildErrors = withBuildErrors;
    createProject();
    addNatures();
    activateManagedBuild();
    addSysIncludesOfGCC();
    createSrcFolder();
    createMainFile();

    if (withSConsSupport) {
      createSConstruct();
    }
  }

  private void createSConstruct() throws CoreException {
    createFile(project, "SConstruct", String.format(
        "env = Environment()\nenv.Program(target = \"hello\", source = [\"src/%s\"])",
        MAIN_FILE_NAME));
  }

  private void createProject() throws CoreException {
    IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
    project = root.getProject(TEST_PROJECT_NAME);
    project.create(null);
    project.open(null);
  }

  private void createMainFile() throws CoreException {
    if (withBuildErrors) {
      createFile(
          project,
          SRC_FOLDER_NAME + File.separator + MAIN_FILE_NAME,
          "#include <iostream>\n int main() { \ni i;\nstd::cout << \"Hello World!\" << std::endl;\nreturn 0; }");
    } else {
      createFile(project, SRC_FOLDER_NAME + File.separator + MAIN_FILE_NAME,
          "#include <iostream>\n int main() { \nstd::cout << \"Hello World!\" << std::endl;\nreturn 0; }");
    }
  }

  private void addNatures() throws CoreException {
    // A CDT C++ project has both C_NATURE and CC_NATURE!
    addNatureToProject(project, CProjectNature.C_NATURE_ID, null);
    CCorePlugin.getDefault().mapCProjectOwner(project, TEST_PROJECT_NAME, false);
    addDefaultBinaryParser(project);
    CCorePlugin.getDefault().getCoreModel().create(project);
    addNatureToProject(project, CCProjectNature.CC_NATURE_ID, null);

    if (withSConsSupport) {
      addNatureToProject(project, SConsNatureTypes.EXISTING_CODE_PROJECT_NATURE.getId(), null);
    }
  }

  private void activateManagedBuild() throws CoreException, BuildException {
    ICProjectDescriptionManager mgr = CoreModel.getDefault().getProjectDescriptionManager();
    ICProjectDescription des = mgr.getProjectDescription(project, true);

    IManagedBuildInfo info = ManagedBuildManager.createBuildInfo(project);
    IProjectType projType =
        ManagedBuildManager.getExtensionProjectType("cdt.managedbuild.target.gnu.exe");
    IToolChain toolChain =
        ManagedBuildManager.getExtensionToolChain("cdt.managedbuild.toolchain.gnu.exe.debug");

    ManagedProject mProj = new ManagedProject(project, projType);
    info.setManagedProject(mProj);

    IConfiguration[] configs = ManagedBuildManager.getExtensionConfigurations(toolChain, projType);

    for (IConfiguration icf : configs) {
      if (!(icf instanceof Configuration)) {
        continue;
      }

      Configuration cf = (Configuration) icf;

      String id = ManagedBuildManager.calculateChildId(cf.getId(), null);
      Configuration config = new Configuration(mProj, cf, id, false, true);

      ICConfigurationDescription cfgDes =
          des.createConfiguration(ManagedBuildManager.CFG_DATA_PROVIDER_ID,
              config.getConfigurationData());
      config.setConfigurationDescription(cfgDes);
      config.exportArtifactInfo();

      IBuilder bld = config.getEditableBuilder();

      if (bld != null) {
        bld.setManagedBuildOn(true);
      }

      config.setName("Debug");
      config.setArtifactName(project.getName());
    }

    mgr.setProjectDescription(project, des);
    config = info.getDefaultConfiguration();
  }

  private void addSysIncludesOfGCC() throws BuildException {
    IToolChain toolChain = config.getToolChain();
    ITool cppCompiler =
        toolChain.getToolsBySuperClassId("cdt.managedbuild.tool.gnu.cpp.compiler")[0];
    IOption option = cppCompiler.getOptionById("gnu.cpp.compiler.option.include.paths");

    String[] includePaths = option.getIncludePaths();

    ManagedBuildManager.setOption(config, cppCompiler, option, includePaths);
    ManagedBuildManager.saveBuildInfo(project, true);
  }

  private void createSrcFolder() throws CoreException {
    IFolder srcFolder = project.getFolder(SRC_FOLDER_NAME);
    srcFolder.create(false, true, null);

    ICSourceEntry projectSourceEntry =
        new CSourceEntry(TEST_PROJECT_NAME, new IPath[] {srcFolder.getProjectRelativePath()}, 0);
    ICSourceEntry srcFolderEntry = new CSourceEntry(SRC_FOLDER_NAME, null, 0);
    config.setSourceEntries(new ICSourceEntry[] {projectSourceEntry, srcFolderEntry});
  }

  private IFile createFile(IProject project, String name, String contents) throws CoreException {
    IFile file = project.getFile(name);
    if (!file.exists()) {
      IPath dirPath = file.getFullPath().removeLastSegments(1).removeFirstSegments(1);
      if (dirPath.segmentCount() > 0) {
        IFolder rc = project.getFolder(dirPath);
        if (!rc.exists()) {
          rc.create(true, true, null);
        }
      }

      file.create(IOUtil.stringToStream(contents), false, null);
    }
    return file;
  }

  public void addNewSourceExclusionEntry(String folder, IPath[] exclusionFolders) {
    ICSourceEntry[] sourceEntries = config.getSourceEntries();
    int len = sourceEntries.length;
    ICSourceEntry newSourceEntries[] = new ICSourceEntry[len + 1];
    System.arraycopy(sourceEntries, 0, newSourceEntries, 0, len);
    newSourceEntries[len] = new CSourceEntry(folder, exclusionFolders, 0);
    config.setSourceEntries(newSourceEntries);
  }

  public boolean isManagedBuildActivated() {
    return config.isManagedBuildOn();
  }

  public void dispose() throws CoreException {
    project.delete(true, true, null);
  }

  public IProject getProject() {
    return project;
  }

  public IConfiguration getConfig() {
    return config;
  }

  public String[] getIncludePaths() throws BuildException {
    ITool cppCompiler =
        config.getToolChain().getToolsBySuperClassId("cdt.managedbuild.tool.gnu.cpp.compiler")[0];
    IOption[] options = cppCompiler.getOptions();

    for (IOption opt : options) {
      if (opt.getValueType() == IOption.INCLUDE_PATH)
        return opt.getIncludePaths();
    }

    return new String[0];
  }

  public void addMacro(String key, String value) {
    ICProjectDescription projectDescription = CoreModel.getDefault().getProjectDescription(project);
    ICConfigurationDescription activeConfiguration = projectDescription.getActiveConfiguration();
    ICFolderDescription folderDescription = activeConfiguration.getRootFolderDescription();
    ICLanguageSetting[] allLanguageSettings = folderDescription.getLanguageSettings();
    ICLanguageSettingEntry[] macros =
        allLanguageSettings[0].getSettingEntries(ICSettingEntry.MACRO);
    ICMacroEntry entry = new CMacroEntry(key, value, 0);

    int len = macros.length;
    ICLanguageSettingEntry newMacros[] = new ICLanguageSettingEntry[len + 1];
    System.arraycopy(macros, 0, newMacros, 0, len);
    newMacros[len] = entry;

    List<ICLanguageSettingEntry> list = list();
    list.addAll(Arrays.asList(newMacros));

    allLanguageSettings[0].setSettingEntries(ICSettingEntry.MACRO, list);
  }

  public String[] getMacros() throws BuildException {
    ITool cppCompiler =
        config.getToolChain().getToolsBySuperClassId("cdt.managedbuild.tool.gnu.cpp.compiler")[0];
    IOption[] options = cppCompiler.getOptions();

    for (IOption opt : options)
      if (opt.getValueType() == IOption.PREPROCESSOR_SYMBOLS)
        return opt.getDefinedSymbols();

    return new String[0];
  }

  public void addIncludePath(String newIncludePath) throws BuildException {
    IToolChain toolChain = config.getToolChain();
    ITool cppCompiler =
        toolChain.getToolsBySuperClassId("cdt.managedbuild.tool.gnu.cpp.compiler")[0];
    IOption option = cppCompiler.getOptionById("gnu.cpp.compiler.option.include.paths");

    String[] includePaths = option.getIncludePaths();
    int len = includePaths.length;
    String newIncludePaths[] = new String[len + 1];
    System.arraycopy(includePaths, 0, newIncludePaths, 0, len);
    newIncludePaths[len] = newIncludePath;

    ManagedBuildManager.setOption(config, cppCompiler, option, newIncludePaths);
    ManagedBuildManager.saveBuildInfo(project, true);
  }

  public String[] getLibraries() throws BuildException {
    ITool cppLinker =
        config.getToolChain().getToolsBySuperClassId("cdt.managedbuild.tool.gnu.cpp.linker")[0];
    IOption[] options = cppLinker.getOptions();

    for (IOption opt : options) {
      if (opt.getValueType() == IOption.LIBRARIES)
        return opt.getLibraries();
    }

    return new String[0];
  }

  public void addLibrary(String newLibrary) throws BuildException {
    IToolChain toolChain = config.getToolChain();
    ITool cppLinker = toolChain.getToolsBySuperClassId("cdt.managedbuild.tool.gnu.cpp.linker")[0];
    IOption option = cppLinker.getOptionById("gnu.cpp.link.option.libs");

    String[] libs = option.getLibraries();
    int len = libs.length;
    String newLibs[] = new String[len + 1];
    System.arraycopy(libs, 0, newLibs, 0, len);
    newLibs[len] = newLibrary;

    ManagedBuildManager.setOption(config, cppLinker, option, newLibs);
  }

  public String[] getLibraryPaths() throws BuildException {
    ITool cppLinker =
        config.getToolChain().getToolsBySuperClassId("cdt.managedbuild.tool.gnu.cpp.linker")[0];
    IOption[] options = cppLinker.getOptions();

    for (IOption opt : options) {
      if (opt.getValueType() == IOption.LIBRARY_PATHS)
        return opt.getBasicStringListValue();
    }

    return new String[0];
  }

  public void setPrebuildStep(String prebuildStep) {
    config.setPrebuildStep(prebuildStep);
  }

  public void setPreannouncebuildStep(String preAnnounceBuildStep) {
    config.setPreannouncebuildStep(preAnnounceBuildStep);
  }

  public void setPostbuildStep(String postbuildStep) {
    config.setPostbuildStep(postbuildStep);
  }

  public String getPostbuildStep() {
    return config.getPostbuildStep();
  }

  public void setPostannouncebuildStep(String postAnnounceBuildStep) {
    config.setPostannouncebuildStep(postAnnounceBuildStep);
  }

  public void addLibraryPath(String newLibraryPath) throws BuildException {
    IToolChain toolChain = config.getToolChain();
    ITool cppLinker = toolChain.getToolsBySuperClassId("cdt.managedbuild.tool.gnu.cpp.linker")[0];
    IOption option = cppLinker.getOptionById("gnu.c.link.option.paths");

    String[] libs = option.getLibraries();
    int len = libs.length;
    String newLibs[] = new String[len + 1];
    System.arraycopy(libs, 0, newLibs, 0, len);
    newLibs[len] = newLibraryPath;

    ManagedBuildManager.setOption(config, cppLinker, option, newLibs);
  }

  private boolean addDefaultBinaryParser(IProject project) throws CoreException {
    ICConfigExtensionReference[] binaryParsers =
        CCorePlugin.getDefault().getDefaultBinaryParserExtensions(project);
    if (binaryParsers == null || binaryParsers.length == 0) {
      ICProjectDescription desc = CCorePlugin.getDefault().getProjectDescription(project);
      if (desc == null)
        return false;

      desc.getDefaultSettingConfiguration().create(CCorePlugin.BINARY_PARSER_UNIQ_ID,
          CCorePlugin.DEFAULT_BINARY_PARSER_UNIQ_ID);
      CCorePlugin.getDefault().setProjectDescription(project, desc);
    }
    return true;
  }

  private void addNatureToProject(IProject proj, String natureId, IProgressMonitor pm)
      throws CoreException {
    IProjectDescription description = proj.getDescription();
    String[] prevNatures = description.getNatureIds();
    String[] newNatures = new String[prevNatures.length + 1];
    System.arraycopy(prevNatures, 0, newNatures, 0, prevNatures.length);
    newNatures[prevNatures.length] = natureId;
    description.setNatureIds(newNatures);
    proj.setDescription(description, pm);
  }
}
