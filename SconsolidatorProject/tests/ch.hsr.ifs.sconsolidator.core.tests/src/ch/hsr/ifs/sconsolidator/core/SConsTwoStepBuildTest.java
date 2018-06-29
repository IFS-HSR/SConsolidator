package ch.hsr.ifs.sconsolidator.core;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import ch.hsr.ifs.sconsolidator.core.base.utils.IOUtil;
import ch.hsr.ifs.sconsolidator.core.base.utils.StringUtil;

public class SConsTwoStepBuildTest {
  private static IProject project;
  private static IFolder folder;

  @BeforeClass
  public static void setUp() throws IOException, CoreException {
    IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
    project = root.getProject("Test");
    project.create(null);
    project.open(null);
    folder = project.getFolder("src");
    folder.create(true, true, new NullProgressMonitor());
    IFile file = project.getFile("SConstruct");
    file.create(IOUtil.stringToStream(""), false, new NullProgressMonitor());
  }

  @AfterClass
  public static void tearDown() throws CoreException {
    project.delete(true, null);
  }

  @Test
  public void testGetCommandLine() throws IOException {
    SConsTwoStepBuild twoStepBuild =
        new SConsTwoStepBuild(project, SConsHelper.BUILD_INFO_COLLECTOR);
    String projectPath = project.getLocation().toFile().getAbsolutePath();
    String expected =
        String.format("-f SConstruct -f %s --directory=%s", SConsHelper.BUILD_INFO_COLLECTOR,
            projectPath);
    assertEquals(expected, StringUtil.join(twoStepBuild.getCommandLine(), " "));
  }
}
