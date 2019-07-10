package ch.hsr.ifs.sconsolidator.core.preferences;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.Collection;

import org.eclipse.core.resources.ProjectScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import ch.hsr.ifs.sconsolidator.core.SConsHelper;
import ch.hsr.ifs.sconsolidator.core.SConsPlugin;
import ch.hsr.ifs.sconsolidator.core.base.utils.StringUtil;
import ch.hsr.ifs.sconsolidator.core.helper.CppManagedTestProject;


public class SConsOptionHandlerTest {

    private static CppManagedTestProject testProject;
    private PreferenceStore              defaultSettings;
    private PreferenceStore              specializedSettings;

    @BeforeClass
    public static void beforeClass() throws Exception {
        testProject = new CppManagedTestProject(true);
        initNumberOfJobs();
    }

    @AfterClass
    public static void afterClass() throws Exception {
        assertNotNull("TestProject is null", testProject);
        testProject.dispose();
    }

    @Before
    public void setUp() throws Exception {
        prepareDefaultSettings();
        prepareSpecializedSettings();
    }

    private void prepareDefaultSettings() {
        defaultSettings = new PreferenceStore();
        defaultSettings.setValue(PreferenceConstants.NUMBER_OF_JOBS, 4);
        defaultSettings.setValue(PreferenceConstants.KEEP_GOING, false);
        defaultSettings.setValue(PreferenceConstants.IGNORE_ERROS, false);
        defaultSettings.setValue(PreferenceConstants.RANDOM, false);
        defaultSettings.setValue(PreferenceConstants.SILENT, false);
        defaultSettings.setValue(PreferenceConstants.USE_CACHE, false);
        defaultSettings.setValue(PreferenceConstants.IMPLICIT_DEPS_CHANGED, false);
        defaultSettings.setValue(PreferenceConstants.IMPLICIT_DEPS_UNCHANGED, false);
        defaultSettings.setValue(PreferenceConstants.STACK_SIZE, 256);
        defaultSettings.setValue(PreferenceConstants.MD5_CHUNK_SIZE, 64);
        defaultSettings.setValue(PreferenceConstants.MAX_DRIFT, 2 * 24 * 60 * 60);
        defaultSettings.setValue(PreferenceConstants.SUPPRESS_READING_BUILDING_MSG, false);
        defaultSettings.setValue(PreferenceConstants.CACHE_WRITE_ERROR_WARNING, Boolean.FALSE);
        defaultSettings.setValue(PreferenceConstants.CORRUPT_SCONSIGN_WARNING, Boolean.TRUE);
        defaultSettings.setValue(PreferenceConstants.DEPENDENCIES_WARNINGS, Boolean.FALSE);
        defaultSettings.setValue(PreferenceConstants.DEPRECATED_COPY_WARNING, Boolean.TRUE);
        defaultSettings.setValue(PreferenceConstants.DEPRECATED_SOURCE_SIGNATURES, Boolean.TRUE);
        defaultSettings.setValue(PreferenceConstants.DUPLICATE_ENV_WARNING, Boolean.TRUE);
        defaultSettings.setValue(PreferenceConstants.FORTRAN_CPP_MIX_WARNING, Boolean.TRUE);
        defaultSettings.setValue(PreferenceConstants.FUTURE_DEPRECATED_WARNING, Boolean.FALSE);
        defaultSettings.setValue(PreferenceConstants.LINK_WARNING, Boolean.TRUE);
        defaultSettings.setValue(PreferenceConstants.MISLEADING_KEYWORD_WARNING, Boolean.TRUE);
        defaultSettings.setValue(PreferenceConstants.MISSING_SCONSCRIPT_WARNING, Boolean.TRUE);
        defaultSettings.setValue(PreferenceConstants.NO_MD5_MODULE, Boolean.FALSE);
        defaultSettings.setValue(PreferenceConstants.NO_META_CLASS_WARNINGS, Boolean.FALSE);
        defaultSettings.setValue(PreferenceConstants.NO_OBJECT_COUNT_WARNINGS, Boolean.FALSE);
        defaultSettings.setValue(PreferenceConstants.NO_PARALLEL_SUPPORT_WARNINGS, Boolean.FALSE);
        defaultSettings.setValue(PreferenceConstants.NO_PYTHON_VERSION_WARNINGS, Boolean.TRUE);
        defaultSettings.setValue(PreferenceConstants.RESERVED_VARIABLE_WARNINGS, Boolean.FALSE);
        defaultSettings.setValue(PreferenceConstants.STACK_SIZE_WARNINGS, Boolean.TRUE);
    }

    private void prepareSpecializedSettings() {
        specializedSettings = new PreferenceStore();
        specializedSettings.setValue(PreferenceConstants.NUMBER_OF_JOBS, 8);
        specializedSettings.setValue(PreferenceConstants.KEEP_GOING, true);
        specializedSettings.setValue(PreferenceConstants.IGNORE_ERROS, true);
        specializedSettings.setValue(PreferenceConstants.RANDOM, true);
        specializedSettings.setValue(PreferenceConstants.SILENT, true);
        specializedSettings.setValue(PreferenceConstants.SUPPRESS_READING_BUILDING_MSG, true);
        specializedSettings.setValue(PreferenceConstants.USE_CACHE, true);
        specializedSettings.setValue(PreferenceConstants.IMPLICIT_DEPS_CHANGED, true);
        specializedSettings.setValue(PreferenceConstants.IMPLICIT_DEPS_UNCHANGED, false);
        specializedSettings.setValue(PreferenceConstants.STACK_SIZE, 512);
        specializedSettings.setValue(PreferenceConstants.MD5_CHUNK_SIZE, 1);
        specializedSettings.setValue(PreferenceConstants.MAX_DRIFT, 2 * 60 * 60);
        specializedSettings.setValue(PreferenceConstants.CACHE_WRITE_ERROR_WARNING, Boolean.TRUE);
        specializedSettings.setValue(PreferenceConstants.CORRUPT_SCONSIGN_WARNING, Boolean.FALSE);
        specializedSettings.setValue(PreferenceConstants.DEPENDENCIES_WARNINGS, Boolean.TRUE);
        specializedSettings.setValue(PreferenceConstants.DEPRECATED_COPY_WARNING, Boolean.FALSE);
        specializedSettings.setValue(PreferenceConstants.DEPRECATED_SOURCE_SIGNATURES, Boolean.FALSE);
        specializedSettings.setValue(PreferenceConstants.DUPLICATE_ENV_WARNING, Boolean.FALSE);
        specializedSettings.setValue(PreferenceConstants.FORTRAN_CPP_MIX_WARNING, Boolean.FALSE);
        specializedSettings.setValue(PreferenceConstants.FUTURE_DEPRECATED_WARNING, Boolean.TRUE);
        specializedSettings.setValue(PreferenceConstants.LINK_WARNING, Boolean.FALSE);
        specializedSettings.setValue(PreferenceConstants.MISLEADING_KEYWORD_WARNING, Boolean.FALSE);
        specializedSettings.setValue(PreferenceConstants.MISSING_SCONSCRIPT_WARNING, Boolean.FALSE);
        specializedSettings.setValue(PreferenceConstants.NO_MD5_MODULE, Boolean.TRUE);
        specializedSettings.setValue(PreferenceConstants.NO_META_CLASS_WARNINGS, Boolean.TRUE);
        specializedSettings.setValue(PreferenceConstants.NO_OBJECT_COUNT_WARNINGS, Boolean.TRUE);
        specializedSettings.setValue(PreferenceConstants.NO_PARALLEL_SUPPORT_WARNINGS, Boolean.TRUE);
        specializedSettings.setValue(PreferenceConstants.NO_PYTHON_VERSION_WARNINGS, Boolean.FALSE);
        specializedSettings.setValue(PreferenceConstants.RESERVED_VARIABLE_WARNINGS, Boolean.TRUE);
        specializedSettings.setValue(PreferenceConstants.STACK_SIZE_WARNINGS, Boolean.FALSE);
    }

    @Test
    public void testCmdLineOptionsWorkspaceDefault() {
        SConsOptionHandler handler = new SConsOptionHandler(testProject.getProject());
        Collection<String> commandLineOptions = handler.getCommandLineOptions();
        assertEquals(1, commandLineOptions.size());
        assertEquals(String.format("--jobs=%s", SConsHelper.getNumOfPreferredJobs()), commandLineOptions.iterator().next());
    }

    @Test
    public void testSConsOptionsWorkspaceDefault() {
        SConsOptionHandler handler = new SConsOptionHandler(testProject.getProject());
        String sconsOptions = handler.getSConsFileOptions();
        assertEquals(String.format("{'num_jobs':'%s'}", SConsHelper.getNumOfPreferredJobs()), sconsOptions);
    }

    @Ignore
    public void testCmdLineOptionsProjectSpecific() throws IOException {
        ProjectScope ps = new ProjectScope(testProject.getProject());
        ScopedPreferenceStore scoped = new ScopedPreferenceStore(ps, SConsPlugin.PLUGIN_ID);
        scoped.setValue(PreferenceConstants.BUILD_SETTINGS_PAGE_ID, true);
        scoped.setValue(PreferenceConstants.KEEP_GOING, true);

        SConsOptionHandler handler = new SConsOptionHandler(testProject.getProject());
        Collection<String> commandLineOptions = handler.getCommandLineOptions();
        String expected = "--keep-going --ignore-errors --random --silent -Q --jobs=8 --implicit-cache " +
                          "--implicit-deps-changed --stack-size=512 --md5-chunksize=1 --max-drift=7200 --warn=cache-write-error " +
                          "--warn=no-corrupt-sconsign --warn=dependency --warn=no-deprecated-copy --warn=no-deprecated-source-signatures " +
                          "--warn=no-duplicate-environment --warn=no-fortran-cxx-mix --warn=future-deprecated --warn=no-link " +
                          "--warn=no-misleading-keywords --warn=no-missing-sconscript --warn=no-md5-module --warn=no-metaclass-support " +
                          "--warn=no-object-count --warn=no-parallel-support --warn=no-no-python-version --warn=reserved-variable --warn=no-stack-size";
        assertEquals(expected, StringUtil.join(commandLineOptions, " "));
    }

    @Ignore
    public void testSConsOptionsSpecialized() {
        SConsOptionHandler handler = new SConsOptionHandler(testProject.getProject());
        String sconsOptions = handler.getSConsFileOptions();
        String expected = "{'keep_going':'', 'ignore_errors':'', 'random':'', 'silent':'', 'num_jobs':'8', " +
                          "'implicit_cache':'', 'implicit_deps_changed':'', 'stack_size':'512', 'max_drift':'7200', " +
                          "'warn_cache-write-error':'', 'warn_no_corrupt-sconsign':'', 'warn_dependency':'', 'warn_no_deprecated-copy':'', " +
                          "'warn_no_deprecated-source-signatures':'', 'warn_no_duplicate-environment':'', 'warn_no_fortran-cxx-mix':'', " +
                          "'warn_future-deprecated':'', 'warn_no_link':'', 'warn_no_misleading-keywords':'', 'warn_no_missing-sconscript':'', " +
                          "'warn_no-md5-module':'', 'warn_no-metaclass-support':'', 'warn_no-object-count':'', 'warn_no-parallel-support':'', " +
                          "'warn_no_no-python-version':'', 'warn_reserved-variable':'', 'warn_no_stack-size':''}";
        assertEquals(expected, sconsOptions);
    }

    @Ignore
    public void testAllWarnings() {
        PreferenceStore store = new PreferenceStore();
        store.setValue(PreferenceConstants.ALL_WARNINGS_ENABLED, true);
        SConsOptionHandler handler = new SConsOptionHandler(testProject.getProject());
        Collection<String> commandLineOptions = handler.getCommandLineOptions();
        String expectedCmdLineOptions = "--stack-size=0 --md5-chunksize=0 --max-drift=0 --warn=all";
        assertEquals(expectedCmdLineOptions, StringUtil.join(commandLineOptions, " "));
        String expectedFileOptions = "{'stack_size':'0', 'max_drift':'0', 'warn_all':''}";
        assertEquals(expectedFileOptions, handler.getSConsFileOptions());
    }

    private static void initNumberOfJobs() {
        final IPreferenceStore preferences = SConsPlugin.getActivePreferences(testProject.getProject(), PreferenceConstants.BUILD_SETTINGS_PAGE_ID);
        preferences.putValue(PreferenceConstants.NUMBER_OF_JOBS, "" + SConsHelper.getNumOfPreferredJobs());
    }
}
