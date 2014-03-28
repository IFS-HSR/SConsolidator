package ch.hsr.ifs.sconsolidator.core.base.utils;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class CollectionUtil {

  // some generic type inference magic to get rid of
  // boilerplate java collection creation
  private CollectionUtil() {}

  public static <T> List<T> list() {
    return new ArrayList<T>();
  }

  @SafeVarargs
  public static <T> List<T> list(T... elements) {
    return new ArrayList<T>(asList(elements));
  }

  public static <T> Set<T> set() {
    return new HashSet<T>();
  }

  @SafeVarargs
  public static <T> Set<T> set(T... elements) {
    return new HashSet<T>(asList(elements));
  }

  public static <T> Set<T> orderPreservingSet(T... elements) {
    return new LinkedHashSet<T>(asList(elements));
  }

  public static <K, V> Map<K, V> map() {
    return new HashMap<K, V>();
  }

  public static <K, V> Map<K, V> map(Map<K, V> map) {
    return new HashMap<K, V>(map);
  }
}
