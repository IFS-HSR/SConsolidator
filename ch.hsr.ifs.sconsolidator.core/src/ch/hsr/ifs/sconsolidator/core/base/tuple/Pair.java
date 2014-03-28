package ch.hsr.ifs.sconsolidator.core.base.tuple;

public class Pair<T1, T2> extends Tuple<T1, Tuple<T2, Sentinel>> {
  public Pair(T1 t1, T2 t2) {
    super(t1, Tuple.from(t2));
  }
}
