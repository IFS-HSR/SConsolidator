package ch.hsr.ifs.sconsolidator.core.base.utils;

import static ch.hsr.ifs.sconsolidator.core.base.functional.FunctionalHelper.map;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import ch.hsr.ifs.sconsolidator.core.base.functional.UnaryFunction;

public final class PythonUtil {
  private PythonUtil() {}

  public static String toPythonStringLiteral(String value) {
    if (value.equals("None")) // None is used to represent the absence of a value in Python
      return value;

    if (value.startsWith("[")) // do not quote Python lists
      return value;

    StringBuilder buffer = new StringBuilder();
    buffer.append("'");
    buffer.append(value.replace("'", "\\'"));
    buffer.append("'");
    return buffer.toString();
  }

  public static <T> String toPythonList(Collection<T> elements) {
    Collection<String> pythonizedElements = map(elements, new UnaryFunction<T, String>() {
      @Override
      public String apply(final T arg) {
        return toPythonStringLiteral(arg.toString());
      }
    });
    StringBuilder sb = new StringBuilder();
    sb.append("[");
    sb.append(StringUtil.join(pythonizedElements, ", "));
    sb.append("]");
    return sb.toString();
  }

  public static String toPythonDict(Map<?, ?> dict) {
    StringBuilder sb = new StringBuilder();
    sb.append("{");

    for (Entry<?, ?> entry : dict.entrySet()) {
      if (sb.length() > 1) {
        sb.append(", ");
      }
      sb.append(toPythonStringLiteral(entry.getKey().toString()));
      sb.append(":");
      sb.append(toPythonStringLiteral(entry.getValue().toString()));
    }

    sb.append("}");
    return sb.toString();
  }

  public static String toPythonBoolean(boolean b) {
    return b ? "True" : "False";
  }
}
