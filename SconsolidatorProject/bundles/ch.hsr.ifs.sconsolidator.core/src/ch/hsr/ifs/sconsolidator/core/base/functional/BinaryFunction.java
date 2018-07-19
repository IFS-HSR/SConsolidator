package ch.hsr.ifs.sconsolidator.core.base.functional;

public interface BinaryFunction<S, U, V> {
  V apply(S param1, U param2);
}
