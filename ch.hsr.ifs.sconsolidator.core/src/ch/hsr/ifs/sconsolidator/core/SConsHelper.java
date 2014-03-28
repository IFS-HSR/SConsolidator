package ch.hsr.ifs.sconsolidator.core;

import static ch.hsr.ifs.sconsolidator.core.base.utils.CollectionUtil.list;
import static ch.hsr.ifs.sconsolidator.core.preferences.PreferenceConstants.BUILD_SETTINGS_PAGE_ID;
import static ch.hsr.ifs.sconsolidator.core.preferences.PreferenceConstants.SCONSTRUCT_NAME;
import static ch.hsr.ifs.sconsolidator.core.preferences.PreferenceConstants.STARTING_DIRECTORY;

import java.io.File;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.preference.IPreferenceStore;

public final class SConsHelper {
  public static final String SCONSTRUCT = "SConstruct";
  public static final String SCONSCRIPT = "SConscript";
  public static final String SCONFIG = "SConfig";
  public static final String SCONFIG_PY = "SConfig.py";
  public static final String SCONFIG_PYC = SCONFIG_PY + "c";
  public static final String BUILD_INFO_COLLECTOR = "BuildInfoCollector.py";
  public static final String DEPENDENCY_ANALYZER = "DependencyAnalyzer.py";
  public static final String ASCII_TREE = "tree.txt";
  public static final String SCONS_FILES_DIR = "scons_files";

  // Number of jobs n should be about 1.5 to 2 times the number of
  // available CPU's. This keeps the CPU's busy while some of the jobs
  // are waiting for disk I/O to complete.
  private static final int NUM_OF_JOBS = 2 * PlatformSpecifics.getNumberOfAvalaibleProcessors();

  private SConsHelper() {}

  public static String findFileAbovePath(File startDir, String fileName) {
    if (!startDir.isDirectory())
      throw new IllegalArgumentException("Not a directory given");

    List<File> filesAndDirs = list(startDir.listFiles());
    filesAndDirs.add(startDir);

    for (File file : filesAndDirs) {
      if (file.isFile() && file.getName().equals(fileName))
        return file.getParentFile().getAbsolutePath();
    }

    if (startDir.getParentFile() != null)
      return findFileAbovePath(startDir.getParentFile(), fileName);

    return null;
  }

  public static String determineStartingDirectory(IProject project) {
    IPreferenceStore prefs = getActivePreferences(project);
    String sconstructName = prefs.getString(SCONSTRUCT_NAME);
    String startingDir = prefs.getString(STARTING_DIRECTORY);

    if (startingDir.trim().isEmpty()) {
      File projectPath = project.getLocation().toFile();
      startingDir = findFileAbovePath(projectPath, sconstructName);

      if (startingDir == null) {
        startingDir = projectPath.getAbsolutePath();
      }
    }
    return startingDir;
  }

  private static IPreferenceStore getActivePreferences(IProject project) {
    return SConsPlugin.getActivePreferences(project, BUILD_SETTINGS_PAGE_ID);
  }

  public static boolean isSConsFile(IFile file) {
    String fileName = file.getName();
    return fileName.equals(SCONSCRIPT) || fileName.equals(SCONFIG_PY)
        || fileName.equals(SCONFIG_PYC) || fileName.equals(getSConstructFileName());
  }

  public static String getSConstructFileName() {
    return SConsPlugin.getWorkspacePreferenceStore().getString(SCONSTRUCT_NAME);
  }

  public static int getNumOfPreferredJobs() {
    return NUM_OF_JOBS;
  }
}
