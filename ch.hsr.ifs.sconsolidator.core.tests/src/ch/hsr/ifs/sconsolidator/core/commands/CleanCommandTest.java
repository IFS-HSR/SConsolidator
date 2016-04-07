package ch.hsr.ifs.sconsolidator.core.commands;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.preference.PreferenceStore;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ch.hsr.ifs.sconsolidator.core.PlatformSpecifics;
import ch.hsr.ifs.sconsolidator.core.SConsHelper;
import ch.hsr.ifs.sconsolidator.core.console.NullConsole;
import ch.hsr.ifs.sconsolidator.core.helper.CppManagedTestProject;
import ch.hsr.ifs.sconsolidator.core.preferences.PreferenceConstants;

public class CleanCommandTest {
  private static final String[] EXPECTED_ARGS = new String[] {
      String.format("--jobs=%d", PlatformSpecifics.getNumberOfAvalaibleProcessors()), "hello"};
  private static CppManagedTestProject testProject;

  @AfterClass
  public static void afterClass() throws Exception {
    testProject.dispose();
  }

  @BeforeClass
  public static void beforeClass() throws Exception {
    testProject = new CppManagedTestProject(true);
  }

  private CleanCommand cleanCommand;
  private PreferenceStore projectSettingsStore;

  @Before
  public void setUp() throws Exception {
    projectSettingsStore = new PreferenceStore();
    projectSettingsStore.setValue(PreferenceConstants.DEFAULT_TARGET, "hello");
    cleanCommand =
        new CleanCommand(getSConsPath(), new NullConsole(), testProject.getProject(),
            projectSettingsStore);
  }

  private String getSConsPath() {
    return PlatformSpecifics.findSConsExecOnSystemPath().getAbsolutePath();
  }

  @Test
  public void testCreate() throws Exception {
    assertArrayEquals(EXPECTED_ARGS, cleanCommand.getArguments().toArray(new String[0]));
  }

  @Test
  public void testRun() throws Exception {
    File projectPath = testProject.getProject().getLocation().toFile();
    new BuildCommand(getSConsPath(), new NullConsole(), testProject.getProject(), null, null).run(
        projectPath, new NullProgressMonitor());
    File executable = new File(projectPath + File.separator + "hello");
    File objectFile = new File(projectPath + File.separator + "src/main.o");
    assertTrue(executable.exists());
    assertTrue(objectFile.exists());

    cleanCommand.run(projectPath, new NullProgressMonitor());
    assertFalse(executable.exists());
    assertFalse(objectFile.exists());

    String output = cleanCommand.getOutput();
    assertTrue(output.contains("Removed src/main.o\nRemoved hello\n"));
    String error = cleanCommand.getError();
    assertEquals("", error);
  }
}
