package ch.hsr.ifs.sconsolidator.core.managed;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import ch.hsr.ifs.sconsolidator.core.helper.CppManagedTestProject;

public class ProjectSettingsReaderTest {
  private CppManagedTestProject testProject;
  private ProjectSettingsReader reader;

  @Before
  public void setUp() throws Exception {
    testProject = new CppManagedTestProject(false);
    reader = new ProjectSettingsReader(testProject.getProject());
  }

  @After
  public void tearDown() throws Exception {
    testProject.dispose();
  }

  @Test
  public void testGetArtifactName() {
    assertEquals(CppManagedTestProject.TEST_PROJECT_NAME, reader.getArtifactName());
  }

  @Test
  public void testGetProjectType() {
    assertEquals("exe", reader.getProjectType());
  }

  @Test
  public void testGetDefaultConfigurationName() {
    assertEquals("Debug", reader.getDefaultConfigurationName());
  }

  @Test
  public void testGetIncludes() throws Exception {
    Collection<File> includes = reader.getIncludes();
    assertTrue(includes.isEmpty()); // sys includes should be ignored (SCons handles this)
    String boostIncludePath = "/usr/include/boost";
    testProject.addIncludePath(boostIncludePath);
    ProjectSettingsReader reader = new ProjectSettingsReader(testProject.getProject());
    assertEquals(1, reader.getIncludes().size());
    assertEquals(boostIncludePath, reader.getIncludes().iterator().next().getAbsolutePath());
  }

  @Test
  public void testGetSourceDirsAndExclusionPatterns() throws CoreException {
    Map<String, String> sourceDirsAndExclusionPatterns = reader.getSourceDirsAndExclusionPatterns();
    assertEquals("['src']",
        sourceDirsAndExclusionPatterns.get(CppManagedTestProject.TEST_PROJECT_NAME));
    assertEquals("[]", sourceDirsAndExclusionPatterns.get(CppManagedTestProject.SRC_FOLDER_NAME));
    testProject.addNewSourceExclusionEntry("src",
        new IPath[] {testProject.getProject().getFolder("test").getProjectRelativePath()});
    ProjectSettingsReader reader = new ProjectSettingsReader(testProject.getProject());
    assertEquals("['test']",
        reader.getSourceDirsAndExclusionPatterns().get(CppManagedTestProject.SRC_FOLDER_NAME));
  }

  @Ignore
  public void testGetLibraries() throws Exception {
    assertTrue(reader.getLibraries().isEmpty());
    testProject.addLibrary("boost_thread");
    ProjectSettingsReader reader = new ProjectSettingsReader(testProject.getProject());
    assertEquals("boost_thread", reader.getLibraries().iterator().next());
  }

  @Ignore
  public void testGetLibraryPaths() throws Exception {
    assertTrue(reader.getLibraryPaths().isEmpty());
    testProject.addLibraryPath("/usr/lib/boost");
    ProjectSettingsReader reader = new ProjectSettingsReader(testProject.getProject());
    assertEquals("/usr/lib/boost", reader.getLibraryPaths().iterator().next());
  }

  @Test
  public void testGetCompilerName() throws Exception {
    assertEquals("g++", reader.getCompilerName());
  }

  @Test
  public void testGetCompilerCFlags() throws Exception {
    assertEquals("-O0 -g3 -Wall -c -fmessage-length=0", reader.getCompilerCFlags());
  }

  @Test
  public void testGetCompilerCxxFlags() throws Exception {
    assertEquals("-O0 -g3 -Wall -c -fmessage-length=0", reader.getCompilerCxxFlags());
  }

  @Ignore
  public void testGetMacros() throws CoreException {
    assertTrue(reader.getMacros().isEmpty());
    testProject.addMacro("WIN", "1");
    ProjectSettingsReader reader = new ProjectSettingsReader(testProject.getProject());
    Map<String, String> macros = reader.getMacros();
    Entry<String, String> macro = macros.entrySet().iterator().next();
    assertEquals(macro.getKey(), "WIN");
    assertEquals(macro.getValue(), "1");
  }

  @Test
  public void testGetPrebuildStep() throws CoreException {
    assertEquals("", reader.getPrebuildStep());
    String prebuildStep = "cp x y";

    testProject.setPrebuildStep(prebuildStep);
    ProjectSettingsReader reader = new ProjectSettingsReader(testProject.getProject());
    assertEquals(prebuildStep, reader.getPrebuildStep());
  }

  @Test
  public void testGetPreannouncebuildStep() throws CoreException {
    assertEquals("", reader.getPreannouncebuildStep());
    String preannouncebuildStep = "Copy necessary files";

    testProject.setPreannouncebuildStep(preannouncebuildStep);
    ProjectSettingsReader reader = new ProjectSettingsReader(testProject.getProject());
    assertEquals(preannouncebuildStep, reader.getPreannouncebuildStep());
  }

  @Test
  public void testGetPostbuildStep() throws CoreException {
    assertEquals("", reader.getPostbuildStep());
    String postbuildStep = "rm y";

    testProject.setPostbuildStep(postbuildStep);
    ProjectSettingsReader reader = new ProjectSettingsReader(testProject.getProject());
    assertEquals(postbuildStep, reader.getPostbuildStep());
  }

  @Test
  public void testGetPostannouncebuildStep() throws CoreException {
    assertEquals("", reader.getPostannouncebuildStep());
    String postannouncebuildStep = "Remove temporary file";

    testProject.setPostannouncebuildStep(postannouncebuildStep);
    ProjectSettingsReader reader = new ProjectSettingsReader(testProject.getProject());
    assertEquals(postannouncebuildStep, reader.getPostannouncebuildStep());
  }
}
