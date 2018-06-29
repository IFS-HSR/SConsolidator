package ch.hsr.ifs.sconsolidator.core.targets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import ch.hsr.ifs.sconsolidator.core.PlatformSpecifics;
import ch.hsr.ifs.sconsolidator.core.SConsPlugin;
import ch.hsr.ifs.sconsolidator.core.helper.CppManagedTestProject;
import ch.hsr.ifs.sconsolidator.core.preferences.PreferenceConstants;
import ch.hsr.ifs.sconsolidator.core.targets.model.SConsBuildTarget;

public class SConsBuildTargetTest {
  private static CppManagedTestProject testProject;
  private SConsBuildTarget target;

  @BeforeClass
  public static void beforeClass() throws Exception {
    testProject = new CppManagedTestProject(true);
  }

  @AfterClass
  public static void afterClass() throws Exception {
    testProject.dispose();
  }

  @Before
  public void setUp() {
    target =
        new SConsBuildTarget("hello", testProject.getProject(), "ch.hsr.ifs.sconsolidator.Builder",
            "hello desc", "-m");
  }

  @Test
  public void testCreate() {
    assertEquals("hello", target.getTargetName());
    assertEquals("ch.hsr.ifs.sconsolidator.Builder", target.getTargetBuilderID());
    assertEquals("-m", target.getAdditionalCmdLineArgs());
    assertEquals(testProject.getProject(), target.getProject());
    assertEquals("hello desc", target.getDescription());
    assertFalse(target.isDefault());
  }

  @Test
  public void testToString() {
    assertEquals("hello desc [-m]", target.toString());
    target.setDescription("hello");
    assertEquals("hello [-m]", target.toString());
  }

  @Test
  public void testGetCommandLine() {
    assertEquals("-m hello", target.getCommandLine());
    target.setAdditionalCmdLineArgs("");
    assertEquals("hello", target.getCommandLine());
  }

  @Ignore
  public void testBuild() throws CoreException {
    SConsPlugin.getConfigPreferenceStore().setValue(PreferenceConstants.EXECUTABLE_PATH,
        getSConsPath());
    File projectPath = testProject.getProject().getLocation().toFile();
    target.build(new NullProgressMonitor());
    assertTrue(new File(projectPath + File.separator + "hello").exists());
    assertTrue(new File(projectPath + File.separator + "src/main.o").exists());
  }

  private String getSConsPath() {
    return PlatformSpecifics.findSConsExecOnSystemPath().getAbsolutePath();
  }
}
