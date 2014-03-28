package ch.hsr.ifs.sconsolidator.core.commands;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ch.hsr.ifs.sconsolidator.core.PlatformSpecifics;
import ch.hsr.ifs.sconsolidator.core.SConsHelper;
import ch.hsr.ifs.sconsolidator.core.base.utils.FileUtil;
import ch.hsr.ifs.sconsolidator.core.console.NullConsole;
import ch.hsr.ifs.sconsolidator.core.helper.CppManagedTestProject;

public class BuildInfoCollectorCommandTest {
  private static CppManagedTestProject testProject;
  private BuildInfoCollectorCommand existingCodeCommand;

  @BeforeClass
  public static void beforeClass() throws Exception {
    testProject = new CppManagedTestProject(true);
  }

  @AfterClass
  public static void afterClass() throws Exception {
    testProject.dispose();
  }

  @Before
  public void setUp() throws Exception {
    existingCodeCommand =
        new BuildInfoCollectorCommand(getSConsPath(), new NullConsole(), testProject.getProject());
  }

  private String getSConsPath() {
    return PlatformSpecifics.findSConsExecOnSystemPath().getAbsolutePath();
  }

  @Test
  public void testCreateYieldsExpectedProgramArguments() throws Exception {
    String[] args = existingCodeCommand.getArguments().toArray(new String[0]);
    assertEquals("-f", args[0]);
    assertEquals("SConstruct", args[1]);
    assertEquals("-f", args[2]);
    assertTrue(args[3].contains(SConsHelper.BUILD_INFO_COLLECTOR));
    assertTrue(args[4].contains("--directory"));
  }

  @Test
  public void testRunYieldsBuildInfo() throws Exception {
    File projectPath = testProject.getProject().getLocation().toFile();
    copyExtractorScript(projectPath);
    String output = existingCodeCommand.run(projectPath, new NullProgressMonitor());
    File executable = new File(projectPath + File.separator + "hello");
    File objectFile = new File(projectPath + File.separator + "src/main.o");
    assertFalse(executable.exists());
    assertFalse(objectFile.exists());
    assertTrue(output.contains("USER_INCLUDES"));
    assertTrue(output.contains("SYS_C_INCLUDES"));
    assertTrue(output.contains("MACROS"));
    assertTrue(output.contains("SYS_C_MACROS"));
    assertTrue(output.contains("SYS_CPP_MACROS"));
  }

  private void copyExtractorScript(File projectPath) throws CoreException {
    FileUtil.copyBundleFile(new Path("scons_files" + File.separator
        + SConsHelper.BUILD_INFO_COLLECTOR), projectPath.getAbsolutePath() + File.separator
        + SConsHelper.BUILD_INFO_COLLECTOR);
  }
}
