package ch.hsr.ifs.sconsolidator.core.managed;

import static ch.hsr.ifs.sconsolidator.core.base.utils.CollectionUtil.orderPreservingSet;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.cdt.core.CCProjectNature;
import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.settings.model.ICConfigurationDescription;
import org.eclipse.cdt.core.settings.model.ICFolderDescription;
import org.eclipse.cdt.core.settings.model.ICLanguageSetting;
import org.eclipse.cdt.core.settings.model.ICLanguageSettingEntry;
import org.eclipse.cdt.core.settings.model.ICProjectDescription;
import org.eclipse.cdt.core.settings.model.ICSettingEntry;
import org.eclipse.cdt.core.settings.model.ICSourceEntry;
import org.eclipse.cdt.core.settings.model.util.CDataUtil;
import org.eclipse.cdt.managedbuilder.core.BuildException;
import org.eclipse.cdt.managedbuilder.core.IConfiguration;
import org.eclipse.cdt.managedbuilder.core.IOption;
import org.eclipse.cdt.managedbuilder.core.ITool;
import org.eclipse.cdt.managedbuilder.core.ManagedBuildManager;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import ch.hsr.ifs.sconsolidator.core.base.utils.PythonUtil;
import ch.hsr.ifs.sconsolidator.core.base.utils.StringUtil;

public class ProjectSettingsReader {
  private static String CPP_LINKER_ID = "cdt.managedbuild.tool.gnu.cpp.linker";
  private static String C_LINKER_ID = "cdt.managedbuild.tool.gnu.c.linker";
  private static String CPP_COMPILER_ID = "cdt.managedbuild.tool.gnu.cpp.compiler";
  private static String C_COMPILER_ID = "cdt.managedbuild.tool.gnu.c.compiler";
  private static String EMPTY_STRING = "";
  private static String PROJECT_TYPE_STATIC_LIBRARY = "a";
  private static String PROJECT_TYPE_SHARED_LIBRARY = "so";
  private static String PROJECT_TYPE_EXECUTABLE = "exe";
  private final List<ICLanguageSetting> languageSettings;
  private final IProject project;
  private final IWorkspaceRoot root;
  private final IConfiguration config;
  private boolean hasCCNature;

  public ProjectSettingsReader(IProject project) throws CoreException {
    this.project = project;
    languageSettings = new LinkedList<ICLanguageSetting>();
    root = ResourcesPlugin.getWorkspace().getRoot();
    config = getDefaultConfig();
    initLanguageSettings(project);
  }

  private IConfiguration getDefaultConfig() {
    return ManagedBuildManager.getBuildInfo(project).getDefaultConfiguration();
  }

  private void initLanguageSettings(IProject project) throws CoreException {
    ICProjectDescription prjDesc = CoreModel.getDefault().getProjectDescription(project);

    if (prjDesc == null)
      throw new IllegalArgumentException("No valid CDT project given!");

    ICConfigurationDescription activeConfig = prjDesc.getActiveConfiguration();

    if (activeConfig == null)
      throw new IllegalArgumentException("No valid active configuration found!");

    String[] extensionsToInclude = getExtensionsToInclude(project);
    ICFolderDescription folderDesc = activeConfig.getRootFolderDescription();

    for (ICLanguageSetting ls : folderDesc.getLanguageSettings()) {
      String[] extensions = ls.getSourceExtensions();
      Arrays.sort(extensions);

      if (Arrays.equals(extensionsToInclude, extensions)) {
        languageSettings.add(ls);
      }
    }
  }

  private String[] getExtensionsToInclude(IProject project) throws CoreException {
    String contentType = determineContentType(project);
    String[] extensionsToInclude =
        CDataUtil.getExtensionsFromContentTypes(project, new String[] {contentType});
    Arrays.sort(extensionsToInclude);
    return extensionsToInclude;
  }

  private String determineContentType(IProject project) throws CoreException {
    String contentType;

    if (project.hasNature(CCProjectNature.CC_NATURE_ID)) {
      contentType = CCorePlugin.CONTENT_TYPE_CXXSOURCE;
      hasCCNature = true;
    } else {
      contentType = CCorePlugin.CONTENT_TYPE_CSOURCE;
      hasCCNature = false;
    }

    return contentType;
  }

  public String getArtifactName() {
    return config.getArtifactName().equalsIgnoreCase("${ProjName}") ? project.getName() : config
        .getArtifactName();
  }

  public String getProjectType() {
    String buildArtefactTypeId = config.getBuildArtefactType().getId();

    if (buildArtefactTypeId.equals(ManagedBuildManager.BUILD_ARTEFACT_TYPE_PROPERTY_EXE))
      return PROJECT_TYPE_EXECUTABLE;
    else if (buildArtefactTypeId.equals(ManagedBuildManager.BUILD_ARTEFACT_TYPE_PROPERTY_SHAREDLIB))
      return PROJECT_TYPE_SHARED_LIBRARY;
    else if (buildArtefactTypeId.equals(ManagedBuildManager.BUILD_ARTEFACT_TYPE_PROPERTY_STATICLIB))
      return PROJECT_TYPE_STATIC_LIBRARY;
    else
      throw new IllegalArgumentException("Project type not supported!");
  }

  public String getDefaultConfigurationName() {
    return config.getName();
  }

  public String getToolchainName() {
    return config.getToolChain().getName();
  }

  public Collection<File> getIncludes() {
    Collection<File> paths = new LinkedList<File>();
    String workspacePath = getWorkspacePath();

    for (ICLanguageSetting languageSetting : languageSettings) {
      ICLanguageSettingEntry[] includePathSettings =
          languageSetting.getSettingEntries(ICSettingEntry.INCLUDE_PATH);

      for (ICLanguageSettingEntry e : includePathSettings) {
        if (!e.isBuiltIn()) {
          appendFile(paths, workspacePath, e);
        }
      }
    }
    return paths;
  }

  private String getWorkspacePath() {
    return project.getWorkspace().getRoot().getLocation().toOSString();
  }

  public Map<String, String> getSourceDirsAndExclusionPatterns() {
    ICSourceEntry[] sources = config.getSourceEntries();
    Map<String, String> dirsAndExclusions = new LinkedHashMap<String, String>();

    for (ICSourceEntry s : sources) {
      String path =
          s.getFullPath().segmentCount() == 0 ? project.getName() : s.getFullPath().toString();
      dirsAndExclusions.put(path, transformExclusionPatterns(s.getExclusionPatterns()));
    }

    return dirsAndExclusions;
  }

  private String transformExclusionPatterns(IPath[] paths) {
    return PythonUtil.toPythonList(Arrays.asList(paths));
  }

  private void appendFile(Collection<File> paths, String workspacePath,
      ICLanguageSettingEntry includePath) {
    File path = new File(includePath.getValue());

    if ((includePath.getFlags() & ICSettingEntry.VALUE_WORKSPACE_PATH) == ICSettingEntry.VALUE_WORKSPACE_PATH) {
      path = new File(workspacePath, path.toString());
    }

    IFile file = root.getFileForLocation(new Path(path.toString()));

    if (file != null) {
      path = file.getLocation().toFile();
    }

    paths.add(path);
  }

  public String getCompilerName() {
    for (ITool tool : config.getToolChain().getTools()) {
      if (isToolCompiler(tool))
        return tool.getToolCommand();
    }
    return EMPTY_STRING;
  }

  public String getCompilerCFlags() throws BuildException {
    return getCompilerFlags(C_COMPILER_ID);
  }

  public String getCompilerCxxFlags() throws BuildException {
    return getCompilerFlags(CPP_COMPILER_ID);
  }

  private String getCompilerFlags(String compiler) throws BuildException {
    for (ITool tool : config.getToolChain().getTools()) {
      if (tool.getId().startsWith(compiler))
        return tool.getToolCommandFlagsString(null, null);
    }
    return EMPTY_STRING;
  }

  private boolean isToolCompiler(ITool tool) {
    String nature = hasCCNature ? CPP_COMPILER_ID : C_COMPILER_ID;
    return tool.getSuperClass().getId().startsWith(nature);
  }

  private boolean isToolLinker(ITool tool) {
    String linkerId = hasCCNature ? CPP_LINKER_ID : C_LINKER_ID;
    return tool.getSuperClass().getId().startsWith(linkerId);
  }

  public Map<String, String> getMacros() {
    Map<String, String> macros = new LinkedHashMap<String, String>();

    for (ICLanguageSetting ls : languageSettings) {
      for (ICLanguageSettingEntry ms : ls.getSettingEntries(ICSettingEntry.MACRO)) {
        if (ms.isBuiltIn()) {
          continue;
        }
        macros.put(ms.getName(), ms.getValue());
      }
    }

    return macros;
  }

  public String getPrebuildStep() {
    return config.getPrebuildStep();
  }

  public String getPostbuildStep() {
    return config.getPostbuildStep();
  }

  public String getPreannouncebuildStep() {
    return config.getPreannouncebuildStep();
  }

  public String getPostannouncebuildStep() {
    return config.getPostannouncebuildStep();
  }

  public Collection<String> getLibraryPaths() {
    return getLinkerOptionValues(IOption.LIBRARY_PATHS);
  }

  public Collection<String> getLibraries() {
    return getLinkerOptionValues(IOption.LIBRARIES);
  }

  private Collection<String> getLinkerOptionValues(int linkerOption) {
    for (ITool tool : config.getToolChain().getTools()) {
      if (!isToolLinker(tool)) {
        continue;
      }

      for (IOption o : tool.getOptions()) {
        try {
          if (o.getValueType() != linkerOption) {
            continue;
          }
          return orderPreservingSet(linkerOption == IOption.LIBRARY_PATHS ? o.getLibraryPaths() : o
              .getLibraries());
        } catch (BuildException e) {
        }
      }
    }
    return Collections.emptySet();
  }

  public String getLinkerFlags() {
    for (ITool t : config.getToolChain().getTools()) {
      if (!isToolLinker(t)) {
        continue;
      }

      try {
        Set<String> flags =
            orderPreservingSet(t.getOptionBySuperClassId("gnu.cpp.link.option.other")
                .getStringListValue());
        if (!flags.isEmpty())
          return String.format("-Xlinker %s", StringUtil.join(flags, " -Xlinker "));
      } catch (BuildException e) {
      }
    }
    return EMPTY_STRING;
  }
}
