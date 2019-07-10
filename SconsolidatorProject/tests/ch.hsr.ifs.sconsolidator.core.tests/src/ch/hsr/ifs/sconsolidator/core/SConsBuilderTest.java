package ch.hsr.ifs.sconsolidator.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.HashMap;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import ch.hsr.ifs.sconsolidator.core.helper.CppManagedTestProject;
import ch.hsr.ifs.sconsolidator.core.preferences.PreferenceConstants;


public class SConsBuilderTest {

    private CppManagedTestProject testProject;

    @BeforeClass
    public static void beforeClass() {
        String sconsPath = PlatformSpecifics.findSConsExecOnSystemPath().getAbsolutePath();
        SConsPlugin.getConfigPreferenceStore().setValue(PreferenceConstants.EXECUTABLE_PATH, sconsPath);
    }

    @After
    public void after() throws Exception {
        assertNotNull("TestProject is null", testProject);
        testProject.dispose();
    }

    private IWorkspaceRunnable getBuilderFor(final IProject project) {
        IWorkspaceRunnable builderOperation = new IWorkspaceRunnable() {

            @Override
            public void run(final IProgressMonitor monitor) throws CoreException {
                project.build(IncrementalProjectBuilder.FULL_BUILD, SConsBuilder.BUILDER_ID, new HashMap<String, String>(), monitor);
            }
        };

        return builderOperation;
    }

    @Test
    public void testBuildWithErrors() throws Exception {
        testProject = new CppManagedTestProject(true, true);
        ResourcesPlugin.getWorkspace().run(getBuilderFor(testProject.getProject()), null, IResource.NONE, new NullProgressMonitor());
        assertEquals(org.eclipse.cdt.core.model.ICModelMarker.C_MODEL_PROBLEM_MARKER, findMarkersInProject()[0].getType());
    }

    private IMarker[] findMarkersInProject() throws CoreException {
        return testProject.getProject().findMarkers(org.eclipse.cdt.core.model.ICModelMarker.C_MODEL_PROBLEM_MARKER, true, IResource.DEPTH_INFINITE);
    }

    @Test
    public void testBuildWithoutErrors() throws Exception {
        testProject = new CppManagedTestProject(true, false);
        ResourcesPlugin.getWorkspace().run(getBuilderFor(testProject.getProject()), null, IResource.NONE, new NullProgressMonitor());
        File projectPath = testProject.getProject().getLocation().toFile();
        assertTrue(new File(projectPath + File.separator + "hello").exists());
        assertTrue(new File(projectPath + File.separator + "src/main.o").exists());
        assertEquals(0, findMarkersInProject().length);
    }

    @Test
    public void testClean() throws Exception {
        testProject = new CppManagedTestProject(true, false);
        final IProject project = testProject.getProject();
        ResourcesPlugin.getWorkspace().run(getBuilderFor(project), null, IResource.NONE, new NullProgressMonitor());

        IWorkspaceRunnable cleanOperation = new IWorkspaceRunnable() {

            @Override
            public void run(final IProgressMonitor monitor) throws CoreException {
                project.build(IncrementalProjectBuilder.CLEAN_BUILD, SConsBuilder.BUILDER_ID, new HashMap<String, String>(), monitor);
            }
        };
        ResourcesPlugin.getWorkspace().run(cleanOperation, null, IResource.NONE, new NullProgressMonitor());
        File projectPath = testProject.getProject().getLocation().toFile();
        assertFalse(new File(projectPath + File.separator + "hello").exists());
        assertFalse(new File(projectPath + File.separator + "src/main.o").exists());
    }
}
