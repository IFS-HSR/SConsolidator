package ch.hsr.ifs.sconsolidator.core.base.utils;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.eclipse.cdt.core.CCProjectNature;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ch.hsr.ifs.sconsolidator.core.SConsNatureTypes;
import ch.hsr.ifs.sconsolidator.core.helper.CppManagedTestProject;


public class NatureUtilTest {

    private static final String   MANAGED_MAKE_BUILDER = "org.eclipse.cdt.managedbuilder.core.genmakebuilder";
    private static final String   GUGUS_BUILDER        = "GugusBuilder";
    private static final String   GUGUS_NATURE         = "GugusNature";
    private CppManagedTestProject testProject;

    @Before
    public void setUp() throws Exception {
        testProject = new CppManagedTestProject(false);
    }

    @After
    public void tearDown() throws Exception {
        assertNotNull("TestProject is null", testProject);
        testProject.dispose();
    }

    @Test
    public void testAddNature() throws CoreException {
        assertFalse(testProject.getProject().hasNature(SConsNatureTypes.EXISTING_CODE_PROJECT_NATURE.getId()));
        new NatureUtil(testProject.getProject()).addNature(SConsNatureTypes.EXISTING_CODE_PROJECT_NATURE.getId(), new NullProgressMonitor());
        assertTrue(testProject.getProject().hasNature(SConsNatureTypes.EXISTING_CODE_PROJECT_NATURE.getId()));
    }

    @Test
    public void testRemoveNature() throws CoreException {
        new NatureUtil(testProject.getProject()).removeNature(CCProjectNature.CC_NATURE_ID, new NullProgressMonitor());
        assertFalse(testProject.getProject().hasNature(CCProjectNature.CC_NATURE_ID));
        new NatureUtil(testProject.getProject()).removeNature(GUGUS_NATURE, new NullProgressMonitor());
        assertFalse(testProject.getProject().hasNature(CCProjectNature.CC_NATURE_ID));
    }

    @Test
    public void testHasBuilder() throws CoreException {
        assertFalse(new NatureUtil(testProject.getProject()).hasBuilder(GUGUS_BUILDER));
        assertTrue(new NatureUtil(testProject.getProject()).hasBuilder(MANAGED_MAKE_BUILDER));
    }

    @Test
    public void testConfigureBuilder() throws CoreException {
        assertFalse(new NatureUtil(testProject.getProject()).hasBuilder(GUGUS_BUILDER));
        new NatureUtil(testProject.getProject()).configureBuilder(GUGUS_BUILDER);
        assertTrue(new NatureUtil(testProject.getProject()).hasBuilder(GUGUS_BUILDER));
    }

    @Test
    public void testDeconfigureBuilder() throws CoreException {
        new NatureUtil(testProject.getProject()).deconfigureBuilder(MANAGED_MAKE_BUILDER);
        assertFalse(new NatureUtil(testProject.getProject()).hasBuilder(MANAGED_MAKE_BUILDER));
    }
}
