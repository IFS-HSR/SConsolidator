package ch.hsr.ifs.sconsolidator.core.preferences.profiles;

import java.util.HashMap;
import java.util.Map;

import ch.hsr.ifs.sconsolidator.core.SConsI18N;

public enum SConsDecider {
  //@formatter:off
  MD5(SConsI18N.SConsDecider_MD5,  SConsI18N.SConsDecider_MD5Desc, SConsI18N.SConsDecider_MD5HelpText),
  MD5_TIMESTAMP(SConsI18N.SConsDecider_MD5Timestamp, SConsI18N.SConsDecider_MD5TimestampDesc,
                SConsI18N.SConsDecider_MD5TimestampHelpText),
  TIMESTAMP_NEWER(SConsI18N.SConsDecider_TimestampNewer, SConsI18N.SConsDecider_TimestampNewerDesc,
                  SConsI18N.SConsDecider_TimestampNewerHelpText),
  TIMESTAMP_MATCH(SConsI18N.SConsDecider_TimestampMatch, SConsI18N.SConsDecider_TimestampMatchDesc,
                  SConsI18N.SConsDecider_TimestampMatchHelpText);
  //@formatter:on
  private static Map<String, SConsDecider> STRING_TO_ENUM = new HashMap<String, SConsDecider>();

  static {
    for (SConsDecider decider : values()) {
      STRING_TO_ENUM.put(decider.toString(), decider);
    }
  }

  @Override
  public String toString() {
    return name;
  }

  public static SConsDecider fromString(String name) {
    return STRING_TO_ENUM.get(name);
  }

  public String getDescription() {
    return description;
  }

  public String getHelpText() {
    return helpText;
  }

  private SConsDecider(String name, String description, String helpText) {
    this.name = name;
    this.description = description;
    this.helpText = helpText;
  }

  private final String name;
  private final String description;
  private final String helpText;
}
