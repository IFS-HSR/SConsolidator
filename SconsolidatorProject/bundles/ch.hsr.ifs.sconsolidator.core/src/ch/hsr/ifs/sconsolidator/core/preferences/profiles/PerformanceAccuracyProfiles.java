package ch.hsr.ifs.sconsolidator.core.preferences.profiles;

import java.util.HashMap;
import java.util.Map;

import ch.hsr.ifs.sconsolidator.core.SConsI18N;

public enum PerformanceAccuracyProfiles {
  //@formatter:off
  BALANCED(SConsI18N.PerformanceAccuracyProfile_balancedName,
           SConsI18N.PerformanceAccuracyProfile_balancedDescription,
           new BalancedProfile()),
  DEFAULT(SConsI18N.PerformanceAccuracyProfile_DefaultName,
          SConsI18N.PerformanceAccuracyProfile_DefaultDescription,
          new DefaultProfile()),
  MAXIMUM_PERFORMANCE(SConsI18N.PerformanceAccuracyProfile_MaxPerfName,
                      SConsI18N.PerformanceAccuracyProfile_MaxPerfDescription,
                      new MaximumPerformanceProfile());
  //@formatter:on

  private static Map<String, PerformanceAccuracyProfiles> STRING_TO_ENUM =
      new HashMap<String, PerformanceAccuracyProfiles>();

  static {
    for (PerformanceAccuracyProfiles profile : values()) {
      STRING_TO_ENUM.put(profile.toString(), profile);
    }
  }

  private PerformanceAccuracyProfiles(String name, String description,
      PerformanceAccuracyProfile profile) {
    this.name = name;
    this.description = description;
    this.profile = profile;
  }

  public static PerformanceAccuracyProfiles fromString(String name) {
    return STRING_TO_ENUM.get(name);
  }

  @Override
  public String toString() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public PerformanceAccuracyProfile getProfile() {
    return profile;
  }

  private final String name;
  private final String description;
  private final PerformanceAccuracyProfile profile;
}
