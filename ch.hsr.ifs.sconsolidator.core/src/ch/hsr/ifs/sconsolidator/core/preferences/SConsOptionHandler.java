package ch.hsr.ifs.sconsolidator.core.preferences;

import static ch.hsr.ifs.sconsolidator.core.base.tuple.Tuple._1;
import static ch.hsr.ifs.sconsolidator.core.base.tuple.Tuple._2;
import static ch.hsr.ifs.sconsolidator.core.base.tuple.Tuple.from;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.preference.IPreferenceStore;

import ch.hsr.ifs.sconsolidator.core.PlatformSpecifics;
import ch.hsr.ifs.sconsolidator.core.SConsPlugin;
import ch.hsr.ifs.sconsolidator.core.base.tuple.Pair;
import ch.hsr.ifs.sconsolidator.core.base.utils.PythonUtil;
import ch.hsr.ifs.sconsolidator.core.preferences.profiles.DefaultProfile;

public class SConsOptionHandler {
  private static String OPTION_ACTIVE_VALUE = "1";
  private static String WARN_ALL_OPTION = "all";
  private static String WARN_INACTIVE_OPTION_PREFIX = "warn_no_";
  private static String WARN_ACTIVE_OPTION_PREFIX = "warn_";
  private static String WARN_ACTIVE_CMD_LINE_PREFIX = "--warn=";
  private static String WARN_INACTIVE_CMD_LINE_PREFIX = WARN_ACTIVE_CMD_LINE_PREFIX + "no-";
  private static String EMPTY_STRING = "";
  private static Map<String, Boolean> warningOptions;
  private static Map<String, Pair<String, String>> cmdOptions;
  private final IProject project;

  static {
    warningOptions = new LinkedHashMap<String, Boolean>();
    warningOptions.put(PreferenceConstants.CACHE_WRITE_ERROR_WARNING, FALSE);
    warningOptions.put(PreferenceConstants.CORRUPT_SCONSIGN_WARNING, TRUE);
    warningOptions.put(PreferenceConstants.DEPENDENCIES_WARNINGS, FALSE);
    warningOptions.put(PreferenceConstants.DEPRECATED_COPY_WARNING, TRUE);
    warningOptions.put(PreferenceConstants.DEPRECATED_SOURCE_SIGNATURES, TRUE);
    warningOptions.put(PreferenceConstants.DUPLICATE_ENV_WARNING, TRUE);
    warningOptions.put(PreferenceConstants.FORTRAN_CPP_MIX_WARNING, TRUE);
    warningOptions.put(PreferenceConstants.FUTURE_DEPRECATED_WARNING, FALSE);
    warningOptions.put(PreferenceConstants.LINK_WARNING, TRUE);
    warningOptions.put(PreferenceConstants.MISLEADING_KEYWORD_WARNING, TRUE);
    warningOptions.put(PreferenceConstants.MISSING_SCONSCRIPT_WARNING, TRUE);
    warningOptions.put(PreferenceConstants.NO_MD5_MODULE, FALSE);
    warningOptions.put(PreferenceConstants.NO_META_CLASS_WARNINGS, FALSE);
    warningOptions.put(PreferenceConstants.NO_OBJECT_COUNT_WARNINGS, FALSE);
    warningOptions.put(PreferenceConstants.NO_PARALLEL_SUPPORT_WARNINGS, FALSE);
    warningOptions.put(PreferenceConstants.NO_PYTHON_VERSION_WARNINGS, TRUE);
    warningOptions.put(PreferenceConstants.RESERVED_VARIABLE_WARNINGS, FALSE);
    warningOptions.put(PreferenceConstants.STACK_SIZE_WARNINGS, TRUE);
  }

  static {
    cmdOptions = new LinkedHashMap<String, Pair<String, String>>();
    cmdOptions.put(PreferenceConstants.KEEP_GOING, from("--keep-going", (String) null));
    cmdOptions.put(PreferenceConstants.IGNORE_ERROS, from("--ignore-errors", (String) null));
    cmdOptions.put(PreferenceConstants.RANDOM, from("--random", "random"));
    cmdOptions.put(PreferenceConstants.SILENT, from("--silent", (String) null));
    cmdOptions.put(PreferenceConstants.SUPPRESS_READING_BUILDING_MSG, from("-Q", (String) null));
    cmdOptions.put(PreferenceConstants.NUMBER_OF_JOBS, from("--jobs=%d", "num_jobs"));
    cmdOptions.put(PreferenceConstants.USE_CACHE, from("--implicit-cache", "implicit_cache"));
    cmdOptions.put(PreferenceConstants.IMPLICIT_DEPS_CHANGED,
        from("--implicit-deps-changed", (String) null));
    cmdOptions.put(PreferenceConstants.IMPLICIT_DEPS_UNCHANGED,
        from("--implicit-deps-unchanged", (String) null));
    cmdOptions.put(PreferenceConstants.STACK_SIZE, from("--stack-size=%d", "stack_size"));
    cmdOptions.put(PreferenceConstants.MD5_CHUNK_SIZE, from("--md5-chunksize=%d", (String) null));
    cmdOptions.put(PreferenceConstants.MAX_DRIFT, from("--max-drift=%d", "max_drift"));
  }

  public static Map<String, Boolean> getWarnings() {
    return Collections.unmodifiableMap(warningOptions);
  }

  public SConsOptionHandler(IProject project) {
    this.project = project;
  }

  private void addBuildFileOptions(Map<String, String> dict, boolean isCmdLine) {
    IPreferenceStore preferences = getSconsBuildSettings();
    addKeepGoingOption(dict, isCmdLine, preferences);
    addIgnoreErrorsOption(dict, isCmdLine, preferences);
    addRandomOption(dict, isCmdLine, preferences);
    addSilentOption(dict, isCmdLine, preferences);
    addSuppressBuildingMsgOption(dict, isCmdLine, preferences);
    addNumOfJobsOption(dict, isCmdLine, preferences);
  }

  private IPreferenceStore getSconsBuildSettings() {
    return SConsPlugin.getActivePreferences(project, PreferenceConstants.BUILD_SETTINGS_PAGE_ID);
  }

  private void addNumOfJobsOption(Map<String, String> dict, boolean isCmdLine,
      IPreferenceStore preferences) {
    int numOfJobs = preferences.getInt(PreferenceConstants.NUMBER_OF_JOBS);

    if (numOfJobs != 0) {
      String option = getOption(PreferenceConstants.NUMBER_OF_JOBS, isCmdLine, numOfJobs);
      putValueIfExisting(dict, option, String.valueOf(numOfJobs));
    }
  }

  private void addSuppressBuildingMsgOption(Map<String, String> dict, boolean isCmdLine,
      IPreferenceStore preferences) {
    if (preferences.getBoolean(PreferenceConstants.SUPPRESS_READING_BUILDING_MSG)) {
      String option = getOption(PreferenceConstants.SUPPRESS_READING_BUILDING_MSG, isCmdLine);
      putValueIfExisting(dict, option, EMPTY_STRING);
    }
  }

  private void addSilentOption(Map<String, String> dict, boolean isCmdLine,
      IPreferenceStore preferences) {
    if (preferences.getBoolean(PreferenceConstants.SILENT)) {
      String option = getOption(PreferenceConstants.SILENT, isCmdLine);
      putValueIfExisting(dict, option, EMPTY_STRING);
    }
  }

  private void addRandomOption(Map<String, String> dict, boolean isCmdLine,
      IPreferenceStore preferences) {
    if (preferences.getBoolean(PreferenceConstants.RANDOM)) {
      String option = getOption(PreferenceConstants.RANDOM, isCmdLine);
      putValueIfExisting(dict, option, OPTION_ACTIVE_VALUE);
    }
  }

  private void addIgnoreErrorsOption(Map<String, String> dict, boolean isCmdLine,
      IPreferenceStore preferences) {
    if (preferences.getBoolean(PreferenceConstants.IGNORE_ERROS)) {
      String option = getOption(PreferenceConstants.IGNORE_ERROS, isCmdLine);
      putValueIfExisting(dict, option, EMPTY_STRING);
    }
  }

  private void addKeepGoingOption(Map<String, String> dict, boolean isCmdLine,
      IPreferenceStore preferences) {
    if (preferences.getBoolean(PreferenceConstants.KEEP_GOING)) {
      String option = getOption(PreferenceConstants.KEEP_GOING, isCmdLine);
      putValueIfExisting(dict, option, EMPTY_STRING);
    }
  }

  private void addPerfAccuracyRelatedOptions(Map<String, String> dict, boolean isCmdLine) {
    IPreferenceStore preferences =
        SConsPlugin.getActivePreferences(project, PreferenceConstants.PERF_VS_ACCURACY_PAGE_ID);
    Map<String, String> defaultSettings = new DefaultProfile().getSettings();
    addUseCacheOption(dict, isCmdLine, preferences);
    addImplictDepsChangedOption(dict, isCmdLine, preferences);
    addStackSizeOption(dict, isCmdLine, preferences, defaultSettings);
    addChunkSizeOption(dict, isCmdLine, preferences, defaultSettings);
    addMaxDriftOption(dict, isCmdLine, preferences, defaultSettings);
  }

  private void addMaxDriftOption(Map<String, String> dict, boolean isCmdLine,
      IPreferenceStore preferences, Map<String, String> defaultSettings) {
    int maxDrift = preferences.getInt(PreferenceConstants.MAX_DRIFT);
    if (maxDrift != Integer.valueOf(defaultSettings.get(PreferenceConstants.MAX_DRIFT))) {
      String option = getOption(PreferenceConstants.MAX_DRIFT, isCmdLine, maxDrift);
      putValueIfExisting(dict, option, String.valueOf(maxDrift));
    }
  }

  private void addChunkSizeOption(Map<String, String> dict, boolean isCmdLine,
      IPreferenceStore preferences, Map<String, String> defaultSettings) {
    int chunkSize = preferences.getInt(PreferenceConstants.MD5_CHUNK_SIZE);
    if (chunkSize != Integer.valueOf(defaultSettings.get(PreferenceConstants.MD5_CHUNK_SIZE))) {
      String option = getOption(PreferenceConstants.MD5_CHUNK_SIZE, isCmdLine, chunkSize);
      putValueIfExisting(dict, option, String.valueOf(chunkSize));
    }
  }

  private void addStackSizeOption(Map<String, String> dict, boolean isCmdLine,
      IPreferenceStore preferences, Map<String, String> defaultSettings) {
    int stackSize = preferences.getInt(PreferenceConstants.STACK_SIZE);
    if (stackSize != Integer.valueOf(defaultSettings.get(PreferenceConstants.STACK_SIZE))) {
      String option = getOption(PreferenceConstants.STACK_SIZE, isCmdLine, stackSize);
      putValueIfExisting(dict, option, String.valueOf(stackSize));
    }
  }

  private void addImplictDepsChangedOption(Map<String, String> dict, boolean isCmdLine,
      IPreferenceStore preferences) {
    if (preferences.getBoolean(PreferenceConstants.IMPLICIT_DEPS_CHANGED)) {
      String option = getOption(PreferenceConstants.IMPLICIT_DEPS_CHANGED, isCmdLine);
      putValueIfExisting(dict, option, EMPTY_STRING);
      preferences.setValue(PreferenceConstants.IMPLICIT_DEPS_CHANGED, false);
    } else if (preferences.getBoolean(PreferenceConstants.IMPLICIT_DEPS_UNCHANGED)) {
      String option = getOption(PreferenceConstants.IMPLICIT_DEPS_UNCHANGED, isCmdLine);
      putValueIfExisting(dict, option, EMPTY_STRING);
    }
  }

  private void addUseCacheOption(Map<String, String> dict, boolean isCmdLine,
      IPreferenceStore preferences) {
    if (preferences.getBoolean(PreferenceConstants.USE_CACHE)) {
      String option = getOption(PreferenceConstants.USE_CACHE, isCmdLine);
      putValueIfExisting(dict, option, OPTION_ACTIVE_VALUE);
    }
  }

  private void addWarningFileOptions(Map<String, String> dict, boolean isCmdLine) {
    IPreferenceStore preferences =
        SConsPlugin.getActivePreferences(project, PreferenceConstants.WARNINGS_PAGE_ID);

    if (preferences.getBoolean(PreferenceConstants.ALL_WARNINGS_ENABLED)) {
      warnAllOptions(dict, isCmdLine);
    } else {
      warnSpecificOptions(dict, isCmdLine);
    }
  }

  public Collection<String> getCommandLineOptions() {
    Map<String, String> arguments = new LinkedHashMap<String, String>();
    addBuildFileOptions(arguments, true);
    addPerfAccuracyRelatedOptions(arguments, true);
    addWarningFileOptions(arguments, true);
    return Collections.unmodifiableCollection(arguments.keySet());
  }

  public String getSConsFileOptions() {
    Map<String, String> dict = new LinkedHashMap<String, String>();
    addBuildFileOptions(dict, false);
    addPerfAccuracyRelatedOptions(dict, false);
    addWarningFileOptions(dict, false);
    return PythonUtil.toPythonDict(dict);
  }

  private String getWarningPrefixActive(boolean isCmdLine) {
    return isCmdLine ? WARN_ACTIVE_CMD_LINE_PREFIX : WARN_ACTIVE_OPTION_PREFIX;
  }

  private String getWarningPrefixInActive(boolean isCmdLine) {
    return isCmdLine ? WARN_INACTIVE_CMD_LINE_PREFIX : WARN_INACTIVE_OPTION_PREFIX;
  }

  private String getOption(String optionName, boolean isCmdLine, int... value) {
    Pair<String, String> pair = cmdOptions.get(optionName);

    if (isCmdLine && value.length > 0)
      return String.format(_1(pair), value[0]);
    else if (isCmdLine)
      return _1(pair);

    return _2(pair);
  }

  private void putValueIfExisting(Map<String, String> dict, String key, String value) {
    if (key != null) {
      dict.put(key, value);
    }
  }

  private Map<String, String> warnAllOptions(Map<String, String> dict, boolean isCmdLine) {
    if (isCmdLine) {
      dict.put(WARN_ACTIVE_CMD_LINE_PREFIX + WARN_ALL_OPTION, EMPTY_STRING);
    } else {
      dict.put(WARN_ACTIVE_OPTION_PREFIX + WARN_ALL_OPTION, OPTION_ACTIVE_VALUE);
    }
    return dict;
  }

  private void warnSpecificOptions(Map<String, String> warnings, boolean isCmdLine) {
    IPreferenceStore preferences =
        SConsPlugin.getActivePreferences(project, PreferenceConstants.WARNINGS_PAGE_ID);

    for (Entry<String, Boolean> entry : warningOptions.entrySet()) {
      String warningName = entry.getKey();
      boolean isDefault = entry.getValue();
      boolean warningActive = preferences.getBoolean(warningName);

      if (warningActive && !isDefault) {
        warnings.put(getWarningPrefixActive(isCmdLine) + warningName, OPTION_ACTIVE_VALUE);
      } else if (!warningActive && isDefault) {
        warnings.put(getWarningPrefixInActive(isCmdLine) + warningName, OPTION_ACTIVE_VALUE);
      }
    }
  }

  public static String getNormalizedCommandLineOpts() {
    return PlatformSpecifics.expandEnvVariables(getAdditionalCmdLineArgs()).replaceAll(
        Pattern.quote(PlatformSpecifics.NEW_LINE), " ");
  }

  private static String getAdditionalCmdLineArgs() {
    return SConsPlugin.getWorkspacePreferenceStore().getString(
        PreferenceConstants.ADDITIONAL_COMMANDLINE_OPTIONS);
  }
}
