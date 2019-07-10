package ch.hsr.ifs.sconsolidator.core.commands;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SConsVersion {

    // Minimum requirement for this plug-in is SCons v2.0.0
    public static final SConsVersion MIN_VERSION   = new SConsVersion(2, 0, 0);
    private static final String      ENGINE_PREFIX = "engine:";
    private static final Pattern     VERSION_RE    = Pattern.compile(ENGINE_PREFIX + " v(\\d)*.(\\d)*.(\\d)*(.*)");
    private final int                majorVersion;
    private final int                minorVersion;
    private final int                revision;

    private SConsVersion(int majorVersion, int minorVersion, int revision) {
        if (majorVersion < 0 || minorVersion < 0 || revision < 0) throw new IllegalArgumentException("Version number parts should not be negative!");
        this.majorVersion = majorVersion;
        this.minorVersion = minorVersion;
        this.revision = revision;
    }

    public SConsVersion(String version) {
        Matcher versionMatcher = VERSION_RE.matcher(version);

        if (!versionMatcher.find()) throw new IllegalArgumentException("Version string has wrong format");

        majorVersion = Integer.parseInt(versionMatcher.group(1));
        minorVersion = Integer.parseInt(versionMatcher.group(2));
        revision = Integer.parseInt(versionMatcher.group(3));
    }

    public int getMajorVersion() {
        return majorVersion;
    }

    @Override
    public String toString() {
        StringBuilder version = new StringBuilder();
        version.append(majorVersion).append(".").append(minorVersion).append(".").append(revision);
        return version.toString();
    }

    public int getMinorVersion() {
        return minorVersion;
    }

    public int getRevision() {
        return revision;
    }

    public boolean isGreaterThan(SConsVersion version) {
        if (majorVersion > version.majorVersion)
            return true;
        else if (majorVersion == version.majorVersion && minorVersion > version.minorVersion)
            return true;
        else if (majorVersion == version.majorVersion && minorVersion == version.minorVersion && revision > version.revision) return true;

        return false;
    }

    public boolean isGreaterOrEqual(SConsVersion version) {
        boolean result = isGreaterThan(version);
        if (!result && equals(version)) return true;
        return result;
    }

    public boolean isCompatible() {
        return isGreaterOrEqual(MIN_VERSION);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + majorVersion;
        result = prime * result + minorVersion;
        result = prime * result + revision;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        SConsVersion other = (SConsVersion) obj;
        if (majorVersion != other.majorVersion) return false;
        if (minorVersion != other.minorVersion) return false;
        if (revision != other.revision) return false;
        return true;
    }
}
