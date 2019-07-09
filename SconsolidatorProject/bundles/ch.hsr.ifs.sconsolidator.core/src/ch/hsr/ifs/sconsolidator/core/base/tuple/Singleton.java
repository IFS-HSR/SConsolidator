package ch.hsr.ifs.sconsolidator.core.base.tuple;

public class Singleton<T1> extends Tuple<T1, Sentinel> {

    public Singleton(T1 t1) {
        super(t1, Sentinel.instance);
    }
}
