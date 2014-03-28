package ch.hsr.ifs.sconsolidator.core.base.functional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class FunctionalHelper {
  private FunctionalHelper() {}

  public static <T> Collection<T> filter(Iterable<T> elements, UnaryFunction<T, Boolean> filter) {
    List<T> filtered = new ArrayList<T>();

    for (T each : elements) {
      if (filter.apply(each)) {
        filtered.add(each);
      }
    }

    return filtered;
  }

  public static <S, T> Collection<T> map(Iterable<S> elements, UnaryFunction<S, T> f) {
    List<T> result = new ArrayList<T>();

    for (S each : elements) {
      result.add(f.apply(each));
    }

    return result;
  }

  public static <T, Y> Y fold(Iterable<T> elements, Injector<T, Y> filter) {
    for (T each : elements) {
      filter.apply(each);
    }

    return filter.yield();
  }
}
