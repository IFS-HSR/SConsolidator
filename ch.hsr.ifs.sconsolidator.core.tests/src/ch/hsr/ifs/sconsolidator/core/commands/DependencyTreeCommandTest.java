package ch.hsr.ifs.sconsolidator.core.commands;

import static org.hamcrest.CoreMatchers.anyOf;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.matchers.JUnitMatchers.containsString;

import java.io.File;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ch.hsr.ifs.sconsolidator.core.PlatformSpecifics;
import ch.hsr.ifs.sconsolidator.core.console.NullConsole;
import ch.hsr.ifs.sconsolidator.core.helper.CppManagedTestProject;

public class DependencyTreeCommandTest {
  private static CppManagedTestProject testProject;

  @AfterClass
  public static void afterClass() throws Exception {
    testProject.dispose();
  }

  @BeforeClass
  public static void beforeClass() throws Exception {
    testProject = new CppManagedTestProject(true);
  }

  private DependencyTreeCommand dependencyTreeCommand;

  @Before
  public void setUp() throws Exception {
    dependencyTreeCommand =
        new DependencyTreeCommand(getSConsPath(), new NullConsole(), testProject.getProject(),
            "hello");
  }

  private String getSConsPath() {
    return PlatformSpecifics.findSConsExecOnSystemPath().getAbsolutePath();
  }

  @Test
  public void testCreate() throws Exception {
    String[] expectedArgs = new String[] {"hello"};
    String[] actualArguments = dependencyTreeCommand.getArguments().toArray(new String[0]);
    assertArrayEquals(expectedArgs, actualArguments);
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testRun() throws Exception {
    File projectPath = testProject.getProject().getLocation().toFile();
    dependencyTreeCommand.run(projectPath, new NullProgressMonitor());
    String output = dependencyTreeCommand.getOutput();
    String commonDeps = "+-hello\n  +-src/main.o\n  | +-src/main.cpp\n ";
    // FIXME better use a regex here
    assertThat(
        output,
        anyOf(containsString(commonDeps + " | +-/usr/bin/g++\n  +-/usr/bin/g++\n"),
            containsString(commonDeps + " | +-/usr/local/bin/g++\n  +-/usr/local/bin/g++\n"),
            containsString(commonDeps + " | +-/bin/g++\n  +-/bin/g++\n")));
    assertTrue(dependencyTreeCommand.getError().isEmpty());
  }
}
