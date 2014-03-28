package ch.hsr.ifs.sconsolidator.core.managed;

import static ch.hsr.ifs.sconsolidator.core.PlatformSpecifics.NEW_LINE;
import static ch.hsr.ifs.sconsolidator.core.SConsHelper.SCONFIG_PY;
import static ch.hsr.ifs.sconsolidator.core.SConsHelper.SCONSCRIPT;
import static ch.hsr.ifs.sconsolidator.core.SConsHelper.SCONSTRUCT;
import static ch.hsr.ifs.sconsolidator.core.SConsHelper.SCONS_FILES_DIR;
import static ch.hsr.ifs.sconsolidator.core.base.utils.PythonUtil.toPythonBoolean;
import static ch.hsr.ifs.sconsolidator.core.base.utils.PythonUtil.toPythonDict;
import static ch.hsr.ifs.sconsolidator.core.base.utils.PythonUtil.toPythonList;
import static ch.hsr.ifs.sconsolidator.core.base.utils.PythonUtil.toPythonStringLiteral;
import static java.lang.String.format;

import java.io.File;
import java.io.InputStream;

import org.eclipse.cdt.core.settings.model.ICSourceEntry;
import org.eclipse.cdt.managedbuilder.core.BuildException;
import org.eclipse.cdt.managedbuilder.core.IConfiguration;
import org.eclipse.cdt.managedbuilder.core.IManagedBuildInfo;
import org.eclipse.cdt.managedbuilder.core.ManagedBuildManager;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.IPreferenceStore;

import ch.hsr.ifs.sconsolidator.core.SConsI18N;
import ch.hsr.ifs.sconsolidator.core.SConsPlugin;
import ch.hsr.ifs.sconsolidator.core.base.utils.FileUtil;
import ch.hsr.ifs.sconsolidator.core.base.utils.IOUtil;
import ch.hsr.ifs.sconsolidator.core.preferences.PreferenceConstants;
import ch.hsr.ifs.sconsolidator.core.preferences.SConsOptionHandler;

public class SConsFileWriter {
  private final IProject project;

  public SConsFileWriter(IProject project) {
    this.project = project;
  }

  public void writeSConstruct() throws CoreException {
    String sconstruct = SCONS_FILES_DIR + File.separator + SCONSTRUCT;
    IPath srcPath = new Path(sconstruct);
    IPath destPath = new Path(SCONSTRUCT);
    FileUtil.copyAndReplaceDerivedFile(srcPath, project.getFile(destPath));
  }

  public void writeSConfig() throws CoreException {
    try {
      ProjectSettingsReader pSettings = new ProjectSettingsReader(project);
      IPath destPath = new Path(SCONFIG_PY);
      InputStream is = IOUtil.stringToStream(toString(pSettings));
      FileUtil.copyAndReplaceDerivedFile(is, project.getFile(destPath));
    } catch (BuildException e) {
      SConsPlugin.log(e);
      IStatus status =
          new Status(IStatus.ERROR, SConsPlugin.PLUGIN_ID, 0, "Error getting project details.", e);
      throw new CoreException(status);
    }
  }

  private String toString(ProjectSettingsReader settings) throws BuildException {
    StringBuilder sb = new StringBuilder();
    addDoNotChangeWarning(sb);
    addSConsOptions(sb);
    addDeciderOptions(sb);
    addCompIncludesOptions(sb);
    addBuildArtifactName(settings, sb);
    addProjectType(settings, sb);
    addBuildConfiguration(settings, sb);
    addToolChainName(settings, sb);
    addCFlags(settings, sb);
    addCxxFlags(settings, sb);
    addCompilerName(settings, sb);
    addProjectName(sb);
    addIncludes(settings, sb);
    addLibraries(settings, sb);
    addLibraryPaths(settings, sb);
    addSourcePaths(settings, sb);
    addPreBuildCommand(settings, sb);
    addPreBuildDesc(settings, sb);
    addPostBuildCommand(settings, sb);
    addPostBuildDesc(settings, sb);
    addCompilerDefines(settings, sb);
    addLinkerFlags(settings, sb);
    return sb.toString();
  }

  private void addLinkerFlags(ProjectSettingsReader settings, StringBuilder sb) {
    sb.append(format("LINKER_FLAGS = %s%s", toPythonStringLiteral(settings.getLinkerFlags()),
        NEW_LINE));
  }

  private void addCompilerDefines(ProjectSettingsReader settings, StringBuilder sb) {
    sb.append(format("COMPILER_DEFINES = %s%s", toPythonDict(settings.getMacros()), NEW_LINE));
  }

  private void addPostBuildDesc(ProjectSettingsReader settings, StringBuilder sb) {
    sb.append(format("POST_BUILD_DESC = %s%s",
        toPythonStringLiteral(settings.getPostannouncebuildStep()), NEW_LINE));
  }

  private void addPostBuildCommand(ProjectSettingsReader settings, StringBuilder sb) {
    sb.append(format("POST_BUILD_COMMAND = %s%s",
        toPythonStringLiteral(settings.getPostbuildStep()), NEW_LINE));
  }

  private void addPreBuildDesc(ProjectSettingsReader settings, StringBuilder sb) {
    sb.append(format("PRE_BUILD_DESC = %s%s",
        toPythonStringLiteral(settings.getPreannouncebuildStep()), NEW_LINE));
  }

  private void addPreBuildCommand(ProjectSettingsReader settings, StringBuilder sb) {
    sb.append(format("PRE_BUILD_COMMAND = %s%s", toPythonStringLiteral(settings.getPrebuildStep()),
        NEW_LINE));
  }

  private void addSourcePaths(ProjectSettingsReader settings, StringBuilder sb) {
    sb.append(format("SOURCE_PATHS = %s%s",
        toPythonDict(settings.getSourceDirsAndExclusionPatterns()), NEW_LINE));
  }

  private void addLibraryPaths(ProjectSettingsReader settings, StringBuilder sb) {
    sb.append(format("LIBRARY_PATHS = %s%s", toPythonList(settings.getLibraryPaths()), NEW_LINE));
  }

  private void addLibraries(ProjectSettingsReader settings, StringBuilder sb) {
    sb.append(format("LIBRARIES = %s%s", toPythonList(settings.getLibraries()), NEW_LINE));
  }

  private void addIncludes(ProjectSettingsReader settings, StringBuilder sb) {
    sb.append(format("INCLUDES = %s%s", toPythonList(settings.getIncludes()), NEW_LINE));
  }

  private void addProjectName(StringBuilder sb) {
    sb.append(format("PROJECT_NAME = %s%s", toPythonStringLiteral(project.getName()), NEW_LINE));
  }

  private void addCompilerName(ProjectSettingsReader settings, StringBuilder sb) {
    sb.append(format("COMPILER_NAME = %s%s", toPythonStringLiteral(settings.getCompilerName()),
        NEW_LINE));
  }

  private void addCxxFlags(ProjectSettingsReader settings, StringBuilder sb) throws BuildException {
    sb.append(format("CXX_FLAGS = %s%s", toPythonStringLiteral(settings.getCompilerCxxFlags()),
        NEW_LINE));
  }

  private void addCFlags(ProjectSettingsReader settings, StringBuilder sb) throws BuildException {
    sb.append(format("C_FLAGS = %s%s", toPythonStringLiteral(settings.getCompilerCFlags()),
        NEW_LINE));
  }

  private void addToolChainName(ProjectSettingsReader settings, StringBuilder sb) {
    sb.append(format("TOOLCHAIN_NAME = %s%s", toPythonStringLiteral(settings.getToolchainName()
        .toLowerCase()), NEW_LINE));
  }

  private void addBuildConfiguration(ProjectSettingsReader settings, StringBuilder sb) {
    sb.append(format("BUILD_CONFIGURATION = %s%s",
        toPythonStringLiteral(settings.getDefaultConfigurationName()), NEW_LINE));
  }

  private void addProjectType(ProjectSettingsReader settings, StringBuilder sb) {
    sb.append(format("PROJECT_TYPE = %s%s", toPythonStringLiteral(settings.getProjectType()),
        NEW_LINE));
  }

  private void addBuildArtifactName(ProjectSettingsReader settings, StringBuilder sb) {
    sb.append(format("BUILD_ARTIFACT_NAME = %s%s",
        toPythonStringLiteral(settings.getArtifactName()), NEW_LINE));
  }

  private void addCompIncludesOptions(StringBuilder sb) {
    IPreferenceStore p =
        SConsPlugin.getActivePreferences(project, PreferenceConstants.PERF_VS_ACCURACY_PAGE_ID);
    sb.append(format("COMP_INCLUDES_INTO_CCFLAGS = %s%s",
        toPythonBoolean(p.getBoolean(PreferenceConstants.SYSTEM_HEADER_CCFLAGS_TRICK)), NEW_LINE));
  }

  private void addDeciderOptions(StringBuilder sb) {
    IPreferenceStore p =
        SConsPlugin.getActivePreferences(project, PreferenceConstants.PERF_VS_ACCURACY_PAGE_ID);
    sb.append(format("DECIDER = %s%s",
        toPythonStringLiteral(p.getString(PreferenceConstants.DECIDERS)), NEW_LINE));
  }

  private void addSConsOptions(StringBuilder sb) {
    sb.append(format("SCONS_OPTIONS = %s%s", new SConsOptionHandler(project).getSConsFileOptions(),
        NEW_LINE));
  }

  private void addDoNotChangeWarning(StringBuilder sb) {
    sb.append(format("%s%s", SConsI18N.SConsFileWriter_DoNotChangeSConfigHeaderWarning, NEW_LINE));
  }

  public void writeSConscripts() throws CoreException {
    IManagedBuildInfo info = ManagedBuildManager.getBuildInfo(project);
    IConfiguration config = info.getDefaultConfiguration();
    for (ICSourceEntry source : config.getSourceEntries()) {
      writeSConscript(source.getFullPath());
    }
  }

  public void writeSConscript(IPath path) throws CoreException {
    String sconscript = SCONS_FILES_DIR + File.separator + SCONSCRIPT;
    IPath dstPath = new Path(path.toPortableString() + File.separator + SCONSCRIPT);
    FileUtil.copyAndReplaceDerivedFile(new Path(sconscript), project.getFile(dstPath));
  }
}
