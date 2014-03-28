package ch.hsr.ifs.sconsolidator.core.base.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class StringUtil {
  private StringUtil() {}

  public static String join(Iterable<?> elements, String delimiter) {
    StringBuilder sb = new StringBuilder();

    for (Object e : elements) {
      if (sb.length() > 0) {
        sb.append(delimiter);
      }
      sb.append(e.toString());
    }

    return sb.toString();
  }

  public static List<String> split(String head, String tailToSplit) {
    List<String> result = new ArrayList<String>();
    result.add(head);
    result.addAll(split(tailToSplit));
    return result;
  }

  public static List<String> split(String toSplit) {
    List<String> matchList = new ArrayList<String>();
    // split by whitespace but only if not surrounded by single or double quotes
    Pattern pattern = Pattern.compile("\\s*(\\S*[\"'].*?[\"']\\S*)\\s*|\\s*(\\S+)\\s*");
    Matcher matcher = pattern.matcher(toSplit);
    while (matcher.find()) {
      matchList.add(matcher.group().replaceAll("\"", "").replaceAll("'", "").trim());
    }
    return matchList;
  }
}
