package ch.hsr.ifs.sconsolidator.core.managed;

import static org.junit.Assert.assertArrayEquals;

import org.eclipse.core.runtime.CoreException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ch.hsr.ifs.sconsolidator.core.helper.CppManagedTestProject;

public class ProjectSettingsWriterTest {
  private CppManagedTestProject testProject;

  @Before
  public void setUp() throws Exception {
    testProject = new CppManagedTestProject(false);
  }

  @After
  public void tearDown() throws CoreException {
    testProject.dispose();
  }

  @Test
  public void testSetIncludePaths() throws Exception {
    String[] newIncludes = new String[] {"/usr/include/boost", "/usr/include/alsa"};
    ProjectSettingsWriter writer = new ProjectSettingsWriter(testProject.getProject());
    writer.setIncludePaths(newIncludes, null);
    assertArrayEquals(newIncludes, testProject.getIncludePaths());
  }

  @Test
  public void testSetMacros() throws Exception {
    String[] newMacros = new String[] {"-Dwindows", "-Dgaga"};
    ProjectSettingsWriter writer = new ProjectSettingsWriter(testProject.getProject());
    writer.setMacros(newMacros, null);
    assertArrayEquals(newMacros, testProject.getMacros());
  }
}
