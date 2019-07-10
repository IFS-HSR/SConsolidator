package ch.hsr.ifs.sconsolidator.core.preferences;

public final class PreferenceConstants {

    private PreferenceConstants() {}

    public static final String EXECUTABLE_PATH                = "executablePath";
    public static final String CLEAR_CONSOLE_BEFORE_BUILD     = "clearConsoleBeforeBuild";
    public static final String OPEN_CONSOLE_WHEN_BUILDING     = "openConsoleWhenBuilding";
    public static final String USE_PARENT_SUFFIX              = "_useParentScope";
    public static final String NUMBER_OF_JOBS                 = "numberOfJobs";
    public static final String USE_CACHE                      = "useCache";
    public static final String SUPPRESS_READING_BUILDING_MSG  = "supressReadingBuildingMsg";
    public static final String SILENT                         = "silent";
    public static final String KEEP_GOING                     = "keepGoing";
    public static final String IGNORE_ERROS                   = "ignoreErrors";
    public static final String MAX_DRIFT                      = "maxDrift";
    public static final String RANDOM                         = "random";
    public static final String MD5_CHUNK_SIZE                 = "md5ChunkSize";
    public static final String STACK_SIZE                     = "stackSize";
    public static final String ALL_WARNINGS_ENABLED           = "allWarningsEnabled";
    public static final String CACHE_WRITE_ERROR_WARNING      = "cache-write-error";
    public static final String CORRUPT_SCONSIGN_WARNING       = "corrupt-sconsign";
    public static final String DEPRECATED_COPY_WARNING        = "deprecated-copy";
    public static final String DEPRECATED_SOURCE_SIGNATURES   = "deprecated-source-signatures";
    public static final String DEPRECATED_TARGET_SIGNATURES   = "deprecated-target-signatures";
    public static final String DUPLICATE_ENV_WARNING          = "duplicate-environment";
    public static final String FORTRAN_CPP_MIX_WARNING        = "fortran-cxx-mix";
    public static final String FUTURE_DEPRECATED_WARNING      = "future-deprecated";
    public static final String LINK_WARNING                   = "link";
    public static final String MISLEADING_KEYWORD_WARNING     = "misleading-keywords";
    public static final String MISSING_SCONSCRIPT_WARNING     = "missing-sconscript";
    public static final String NO_MD5_MODULE                  = "no-md5-module";
    public static final String NO_META_CLASS_WARNINGS         = "no-metaclass-support";
    public static final String NO_OBJECT_COUNT_WARNINGS       = "no-object-count";
    public static final String NO_PARALLEL_SUPPORT_WARNINGS   = "no-parallel-support";
    public static final String NO_PYTHON_VERSION_WARNINGS     = "no-python-version";
    public static final String RESERVED_VARIABLE_WARNINGS     = "reserved-variable";
    public static final String STACK_SIZE_WARNINGS            = "stack-size";
    public static final String DEPENDENCIES_WARNINGS          = "dependency";
    public static final String EXPERT_MODE                    = "expertMode";
    public static final String PERF_ACCURACY_PROFILE          = "perfAccuracyProfile";
    public static final String IMPLICIT_DEPS_UNCHANGED        = "implicitDepsUnchanged";
    public static final String IMPLICIT_DEPS_CHANGED          = "implicitDepsChanged";
    public static final String SYSTEM_HEADER_CCFLAGS_TRICK    = "systemHeaderCCFlagsTrick";
    public static final String DECIDERS                       = "deciders";
    public static final String ADDITIONAL_COMMANDLINE_OPTIONS = "additionalCommandlineOptions";
    public static final String AVAILABLE_TARGETS              = "availableTargets";
    public static final String DEFAULT_TARGET                 = "defaultTarget";
    public static final String SCONSTRUCT_NAME                = "sconstructName";
    public static final String STARTING_DIRECTORY             = "startingDirectory";
    public static final String ENVIRONMENT_VARIABLES          = "environmentVariables";

    public static final String BUILD_SETTINGS_PAGE_ID   = "ch.hsr.sconsolidator.core.BuildSettingsPreferencePage";
    public static final String PERF_VS_ACCURACY_PAGE_ID = "ch.hsr.sconsolidator.core.PerformanceAccuracyPreferencePage";
    public static final String WARNINGS_PAGE_ID         = "ch.hsr.sconsolidator.core.WarningsPreferencePage";
    public static final String EXECUTABLE_PATH_PAGE_ID  = "ch.hsr.ifs.sconsolidator.core.ExecutablePathPreferencePage";
}
