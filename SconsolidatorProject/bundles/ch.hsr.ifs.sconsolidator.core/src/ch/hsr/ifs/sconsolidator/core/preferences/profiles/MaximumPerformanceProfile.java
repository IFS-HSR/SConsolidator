package ch.hsr.ifs.sconsolidator.core.preferences.profiles;

import ch.hsr.ifs.sconsolidator.core.preferences.PreferenceConstants;


public class MaximumPerformanceProfile extends PerformanceAccuracyProfile {

    public MaximumPerformanceProfile() {
        getSettings().put(PreferenceConstants.DECIDERS, SConsDecider.MD5_TIMESTAMP.toString());
        getSettings().put(PreferenceConstants.MAX_DRIFT, String.valueOf(1));
        getSettings().put(PreferenceConstants.USE_CACHE, String.valueOf(Boolean.FALSE));
        getSettings().put(PreferenceConstants.SYSTEM_HEADER_CCFLAGS_TRICK, String.valueOf(Boolean.TRUE));
        getSettings().put(PreferenceConstants.IMPLICIT_DEPS_UNCHANGED, String.valueOf(Boolean.TRUE));
    }
}
