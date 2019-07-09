package ch.hsr.ifs.sconsolidator.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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


public class SConsHelperTest {

    private static IProject project;
    private static IFolder  folder;

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
    public void testFindSConstructAbovePath() throws IOException {
        String foundDir = SConsHelper.findFileAbovePath(folder.getLocation().toFile(), SConsHelper.SCONSTRUCT);
        String projectDir = project.getLocation().toFile().getAbsolutePath();
        assertEquals(projectDir, foundDir);

        foundDir = SConsHelper.findFileAbovePath(project.getLocation().toFile(), SConsHelper.SCONSTRUCT);
        assertEquals(projectDir, foundDir);
    }

    @Test
    public void testIsSConsFile() {
        assertTrue(SConsHelper.isSConsFile(project.getFile(SConsHelper.SCONSTRUCT)));
        assertFalse(SConsHelper.isSConsFile(project.getFile("test.txt")));
    }

    @Test
    public void testDetermineStartingDirectory() {
        assertEquals(project.getLocation().toFile().getAbsolutePath(), SConsHelper.determineStartingDirectory(project));
    }
}
