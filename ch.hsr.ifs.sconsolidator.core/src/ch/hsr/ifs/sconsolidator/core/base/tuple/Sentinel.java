package ch.hsr.ifs.sconsolidator.core.base.tuple;

public enum Sentinel implements StringAppender {
  instance;

  @Override
  public void appendString(StringBuilder buffer, String separator) {}
}
