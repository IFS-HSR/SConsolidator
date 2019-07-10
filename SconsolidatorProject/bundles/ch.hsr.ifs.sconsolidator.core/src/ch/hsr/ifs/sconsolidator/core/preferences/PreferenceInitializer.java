package ch.hsr.ifs.sconsolidator.core.preferences;

import static ch.hsr.ifs.sconsolidator.core.preferences.PreferenceConstants.BUILD_SETTINGS_PAGE_ID;
import static ch.hsr.ifs.sconsolidator.core.preferences.PreferenceConstants.PERF_VS_ACCURACY_PAGE_ID;
import static ch.hsr.ifs.sconsolidator.core.preferences.PreferenceConstants.USE_PARENT_SUFFIX;
import static ch.hsr.ifs.sconsolidator.core.preferences.PreferenceConstants.WARNINGS_PAGE_ID;

import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import ch.hsr.ifs.sconsolidator.core.SConsHelper;
import ch.hsr.ifs.sconsolidator.core.SConsPlugin;
import ch.hsr.ifs.sconsolidator.core.preferences.profiles.PerformanceAccuracyProfiles;


public class PreferenceInitializer extends AbstractPreferenceInitializer {

    @Override
    public void initializeDefaultPreferences() {
        IPreferenceStore store = SConsPlugin.getWorkspacePreferenceStore();
        initializeGeneralSettingsDefault(store);
        initializeBuildSettingsDefault(store);
        initializePerformanceAccuracyDefault(store);
        initializeWarningsDefault(store);
    }

    private static void initializeBuildSettingsDefault(IPreferenceStore store) {
        store.setDefault(PreferenceConstants.SUPPRESS_READING_BUILDING_MSG, false);
        store.setDefault(PreferenceConstants.SILENT, false);
        store.setDefault(PreferenceConstants.KEEP_GOING, false);
        store.setDefault(PreferenceConstants.RANDOM, false);
        store.setDefault(PreferenceConstants.IGNORE_ERROS, false);
        store.setDefault(PreferenceConstants.NUMBER_OF_JOBS, SConsHelper.getNumOfPreferredJobs());
        store.setDefault(PreferenceConstants.SCONSTRUCT_NAME, SConsHelper.SCONSTRUCT);
        store.setDefault(PreferenceConstants.STARTING_DIRECTORY, "");
        // A lot of SCons projects are relying on the TERM env variable being set
        store.setDefault(PreferenceConstants.ENVIRONMENT_VARIABLES, "TERM=dumb");
    }

    private static void initializePerformanceAccuracyDefault(IPreferenceStore store) {
        for (Entry<String, String> setting : getDefaultAccuracySettings().entrySet()) {
            store.setDefault(setting.getKey(), setting.getValue());
        }
    }

    private static Map<String, String> getDefaultAccuracySettings() {
        return PerformanceAccuracyProfiles.DEFAULT.getProfile().getSettings();
    }

    private static void initializeWarningsDefault(IPreferenceStore store) {
        store.setDefault(PreferenceConstants.ALL_WARNINGS_ENABLED, false);
        for (Entry<String, Boolean> warning : SConsOptionHandler.getWarnings().entrySet()) {
            store.setDefault(warning.getKey(), warning.getValue());
        }
    }

    private static void initializeGeneralSettingsDefault(IPreferenceStore store) {
        store.setDefault(PreferenceConstants.CLEAR_CONSOLE_BEFORE_BUILD, true);
        store.setDefault(PreferenceConstants.OPEN_CONSOLE_WHEN_BUILDING, true);
    }

    public static void initializePropertiesDefault(IPreferenceStore store) {
        store.setDefault(BUILD_SETTINGS_PAGE_ID + USE_PARENT_SUFFIX, true);
        store.setDefault(WARNINGS_PAGE_ID + USE_PARENT_SUFFIX, true);
        store.setDefault(PERF_VS_ACCURACY_PAGE_ID + USE_PARENT_SUFFIX, true);
        initializeGeneralSettingsDefault(store);
        initializeBuildSettingsDefault(store);
        initializePerformanceAccuracyDefault(store);
        initializeWarningsDefault(store);
    }
}
