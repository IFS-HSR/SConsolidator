package ch.hsr.ifs.sconsolidator.core;

import static ch.hsr.ifs.sconsolidator.core.preferences.PreferenceConstants.ADDITIONAL_COMMANDLINE_OPTIONS;
import static ch.hsr.ifs.sconsolidator.core.preferences.PreferenceConstants.BUILD_SETTINGS_PAGE_ID;
import static ch.hsr.ifs.sconsolidator.core.preferences.PreferenceConstants.SCONSTRUCT_NAME;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.preference.IPreferenceStore;

import ch.hsr.ifs.sconsolidator.core.base.utils.StringUtil;


public class SConsTwoStepBuild {

    // -f file: Use file as the initial SConscript file. Multiple -f options may
    // be specified, in which case SCons will read all of the specified files in
    // the given order.
    private static final String    SCONS_FILE_OPTION_PATTERN      = "-f %s";
    private static final String    SCONS_DIRECTORY_OPTION_PATTERN = "--directory=%s";
    private final IProject         project;
    private final String           scriptName;
    private final List<String>     twoStepArgs;
    private final IPreferenceStore activePrefs;

    public SConsTwoStepBuild(IProject project, String scriptName) {
        this.project = project;
        this.scriptName = scriptName;
        twoStepArgs = new ArrayList<String>();
        activePrefs = getActivePreferences();
        init();
    }

    public Collection<String> getCommandLine() {
        return Collections.unmodifiableCollection(twoStepArgs);
    }

    private void init() {
        initAdditionalSConsOptions();
        initSConstructName();
        initSConsFilePattern();
        initStartingDir();
    }

    private void initStartingDir() {
        String startingDirectory = SConsHelper.determineStartingDirectory(project);
        twoStepArgs.add(String.format(SCONS_DIRECTORY_OPTION_PATTERN, startingDirectory));
    }

    private void initSConsFilePattern() {
        twoStepArgs.addAll(StringUtil.split(String.format(SCONS_FILE_OPTION_PATTERN, scriptName)));
    }

    private void initAdditionalSConsOptions() {
        String options = PlatformSpecifics.expandEnvVariables(activePrefs.getString(ADDITIONAL_COMMANDLINE_OPTIONS));
        twoStepArgs.addAll(StringUtil.split(options));
    }

    private void initSConstructName() {
        String sconstructName = activePrefs.getString(SCONSTRUCT_NAME);
        twoStepArgs.addAll(StringUtil.split(String.format(SCONS_FILE_OPTION_PATTERN, sconstructName)));
    }

    private IPreferenceStore getActivePreferences() {
        return SConsPlugin.getActivePreferences(project, BUILD_SETTINGS_PAGE_ID);
    }
}
