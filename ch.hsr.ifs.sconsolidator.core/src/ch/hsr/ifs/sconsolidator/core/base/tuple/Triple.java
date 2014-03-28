package ch.hsr.ifs.sconsolidator.core.base.tuple;

public class Triple<T1, T2, T3> extends Tuple<T1, Tuple<T2, Tuple<T3, Sentinel>>> {
  public Triple(T1 t1, T2 t2, T3 t3) {
    super(t1, Tuple.from(t2, t3));
  }
}
