package ch.hsr.ifs.sconsolidator.core.preferences.profiles;

import ch.hsr.ifs.sconsolidator.core.preferences.PreferenceConstants;


public class DefaultProfile extends PerformanceAccuracyProfile {

    public DefaultProfile() {
        getSettings().put(PreferenceConstants.DECIDERS, SConsDecider.MD5.toString());
        getSettings().put(PreferenceConstants.MAX_DRIFT, String.valueOf(2 * 24 * 60 * 60));
        getSettings().put(PreferenceConstants.USE_CACHE, String.valueOf(Boolean.FALSE));
        getSettings().put(PreferenceConstants.SYSTEM_HEADER_CCFLAGS_TRICK, String.valueOf(Boolean.FALSE));
        getSettings().put(PreferenceConstants.IMPLICIT_DEPS_UNCHANGED, String.valueOf(Boolean.FALSE));
    }
}
