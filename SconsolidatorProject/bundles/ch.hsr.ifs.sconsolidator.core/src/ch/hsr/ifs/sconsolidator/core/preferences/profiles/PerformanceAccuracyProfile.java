package ch.hsr.ifs.sconsolidator.core.preferences.profiles;

import java.util.HashMap;
import java.util.Map;

import ch.hsr.ifs.sconsolidator.core.preferences.PreferenceConstants;


public abstract class PerformanceAccuracyProfile {

    private final Map<String, String> settings;

    public PerformanceAccuracyProfile() {
        settings = new HashMap<String, String>();
        initDefaultSettings();
    }

    private void initDefaultSettings() {
        settings.put(PreferenceConstants.STACK_SIZE, "256");
        settings.put(PreferenceConstants.MD5_CHUNK_SIZE, "64");
        settings.put(PreferenceConstants.IMPLICIT_DEPS_CHANGED, String.valueOf(Boolean.FALSE));
    }

    public Map<String, String> getSettings() {
        return settings;
    }
}
