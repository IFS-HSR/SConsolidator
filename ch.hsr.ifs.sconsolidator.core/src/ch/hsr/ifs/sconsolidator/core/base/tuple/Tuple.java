package ch.hsr.ifs.sconsolidator.core.base.tuple;

public class Tuple<Head, Tail> implements StringAppender {

  public static <T1, Tail> T1 _1(Tuple<T1, Tail> tuple) {
    return tuple.head;
  }

  public static <T1, T2, Tail> T2 _2(Tuple<T1, Tuple<T2, Tail>> tuple) {
    return tuple.tail.head;
  }

  public static <T1, T2, T3, Tail> T3 _3(Tuple<T1, Tuple<T2, Tuple<T3, Tail>>> tuple) {
    return tuple.tail.tail.head;
  }

  public static <T1> Singleton<T1> from(T1 t1) {
    return new Singleton<T1>(t1);
  }

  public static <T1, T2> Pair<T1, T2> from(T1 t1, T2 t2) {
    return new Pair<T1, T2>(t1, t2);
  }

  public static <T1, T2, T3> Triple<T1, T2, T3> from(T1 t1, T2 t2, T3 t3) {
    return new Triple<T1, T2, T3>(t1, t2, t3);
  }

  private final Head head;
  private final Tail tail;

  protected Tuple(Head head, Tail tail) {
    this.head = head;
    this.tail = tail;
  }

  @Override
  public void appendString(StringBuilder buffer, String separator) {
    buffer.append(separator).append(head);
    ((StringAppender) tail).appendString(buffer, separator);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null)
      return false;

    if (!(obj instanceof Tuple))
      return false;

    Tuple<?, ?> that = (Tuple<?, ?>) obj;
    return (head == null ? that.head == null : head.equals(that.head)) && tail.equals(that.tail);
  }

  @Override
  public int hashCode() {
    return (head == null ? 0 : head.hashCode()) + tail.hashCode() * 37;
  }

  @Override
  public String toString() {
    StringBuilder result = new StringBuilder();
    result.append("(").append(head);
    ((StringAppender) tail).appendString(result, ", ");
    return result.append(")").toString();
  }
}
