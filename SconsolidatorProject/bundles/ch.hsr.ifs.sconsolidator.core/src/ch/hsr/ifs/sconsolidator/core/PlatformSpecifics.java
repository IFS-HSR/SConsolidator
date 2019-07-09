package ch.hsr.ifs.sconsolidator.core;

import java.io.File;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public final class PlatformSpecifics {

    public static final String   NEW_LINE              = System.getProperty("line.separator");
    public static final Pattern  CPP_RE                = Pattern.compile("(.+?(?:\\.(?:cpp|c|cc|C|cxx|h|hxx|hpp|ipp)))[:(]?([\\d]+)[)]?");
    public static final Pattern  FORT_RE               = Pattern.compile("(?i)(.+?(?:\\.(?:f95|f90|f|f77|for))):([\\d]+)(?:.([\\d]+))?:");
    private static final Pattern OBJ_FILE_RE           = Pattern.compile("([^\\s]+)(\\.(o|os|obj))$");
    private static final String  SCONS_EXECUTABLE_NAME = isWindows() ? "scons.bat" : "scons";

    private PlatformSpecifics() {}

    public static boolean isObjectFile(String file) {
        return OBJ_FILE_RE.matcher(file).matches();
    }

    public static String getSConsCommandName() {
        return SCONS_EXECUTABLE_NAME;
    }

    private static boolean isWindows() {
        String os = System.getProperty("os.name").toLowerCase();
        return os.startsWith("windows");
    }

    private static boolean isMacOs() {
        String os = System.getProperty("os.name").toLowerCase();
        return os.startsWith("mac");
    }

    public static File findExecOnSystemPath(String executableName) {
        String systemPath = System.getenv("PATH");
        if (isMacOs()) {
            systemPath += ":/usr/local/bin";
        }

        String[] pathDirs = systemPath.split(File.pathSeparator);

        for (String pathDir : pathDirs) {
            File file = new File(pathDir, executableName);

            if (file.isFile()) return file;
        }
        return null;
    }

    public static File findSConsExecOnSystemPath() {
        return findExecOnSystemPath(SCONS_EXECUTABLE_NAME);
    }

    public static int getNumberOfAvalaibleProcessors() {
        return Runtime.getRuntime().availableProcessors();
    }

    public static Map<String, String> getSystemEnv() {
        return System.getenv();
    }

    public static String expandEnvVariables(String toExpand) {
        Pattern envVarRe = Pattern.compile("\\$\\{([A-Za-z0-9_]+)\\}");
        Matcher matcher = envVarRe.matcher(toExpand);
        Map<String, String> env = getSystemEnv();

        while (matcher.find()) {
            String envVal = env.get(matcher.group(1).toUpperCase());

            if (envVal == null) {
                envVal = "";
            } else {
                envVal = envVal.replace("\\", "\\\\");
            }

            Pattern subExp = Pattern.compile(Pattern.quote(matcher.group(0)));
            toExpand = subExp.matcher(toExpand).replaceAll(envVal);
        }
        return toExpand;
    }
}
