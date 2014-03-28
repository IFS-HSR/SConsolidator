package ch.hsr.ifs.sconsolidator.core.base.functional;

public interface Injector<S, T> extends UnaryFunction<S, Void> {
  T yield();
}
