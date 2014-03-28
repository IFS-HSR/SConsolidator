package ch.hsr.ifs.sconsolidator.core;

public class EmptySConsPathException extends Exception {
  private static final long serialVersionUID = 1L;

  public EmptySConsPathException() {
    super(SConsI18N.EmptyPathException_Message);
  }
}
