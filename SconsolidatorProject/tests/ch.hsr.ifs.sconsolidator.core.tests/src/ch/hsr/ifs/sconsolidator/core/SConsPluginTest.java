package ch.hsr.ifs.sconsolidator.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.preference.IPreferenceStore;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ch.hsr.ifs.sconsolidator.core.preferences.PreferenceConstants;
import ch.hsr.ifs.sconsolidator.core.targets.model.SConsBuildTargetManager;


public class SConsPluginTest {

    private static IProject project;

    @Before
    public void setUp() throws IOException, CoreException {
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        project = root.getProject("Test");
        project.create(null);
        project.open(null);
    }

    @After
    public void tearDown() throws CoreException {
        project.delete(true, null);
    }

    @Test
    public void testGetDefault() {
        SConsPlugin activator = SConsPlugin.getDefault();
        assertNotNull(activator);
    }

    @Test
    public void testGetPluginId() throws Exception {
        String pluginId = SConsPlugin.getPluginId();
        assertEquals("ch.hsr.ifs.sconsolidator.core", pluginId);
    }

    @Test
    public void testGetActivePreferences() {
        IPreferenceStore p = SConsPlugin.getActivePreferences(project, PreferenceConstants.BUILD_SETTINGS_PAGE_ID);
        p.setValue(PreferenceConstants.BUILD_SETTINGS_PAGE_ID + PreferenceConstants.USE_PARENT_SUFFIX, false);
        p.setValue(PreferenceConstants.NUMBER_OF_JOBS, 2);
        p = SConsPlugin.getActivePreferences(project, PreferenceConstants.BUILD_SETTINGS_PAGE_ID);
        assertEquals(2, Integer.parseInt((p.getString(PreferenceConstants.NUMBER_OF_JOBS))));
    }

    @Test
    public void testGetProjectPreferenceStore() {
        IPreferenceStore preferences = SConsPlugin.getProjectPreferenceStore(project);
        assertNotNull(preferences);
        assertTrue(preferences.getBoolean(PreferenceConstants.CLEAR_CONSOLE_BEFORE_BUILD));
    }

    @Test
    public void testGetWorkspacePreferenceStore() {
        IPreferenceStore preferences = SConsPlugin.getWorkspacePreferenceStore();
        assertNotNull(preferences);
        assertTrue(preferences.getBoolean(PreferenceConstants.CLEAR_CONSOLE_BEFORE_BUILD));
    }

    @Test
    public void testGetConfigurationPreferenceStore() {
        IPreferenceStore preferences = SConsPlugin.getConfigPreferenceStore();
        assertNotNull(preferences);
        assertTrue(preferences.getBoolean(PreferenceConstants.CLEAR_CONSOLE_BEFORE_BUILD));
    }

    @Test
    public void testGetSConsTargetManager() {
        SConsPlugin activator = SConsPlugin.getDefault();
        SConsBuildTargetManager targetManager = activator.getSConsTargetManager();
        assertNotNull(targetManager);
        assertSame(targetManager, activator.getSConsTargetManager());
    }
}
