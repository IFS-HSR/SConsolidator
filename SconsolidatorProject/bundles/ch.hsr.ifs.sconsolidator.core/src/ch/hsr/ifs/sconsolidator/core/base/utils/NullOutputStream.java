package ch.hsr.ifs.sconsolidator.core.base.utils;

import java.io.IOException;
import java.io.OutputStream;

public class NullOutputStream extends OutputStream {
  @Override
  public void write(final int b) throws IOException {}

  @Override
  public void write(final byte[] b, final int off, final int len) {}

  @Override
  public void write(final byte[] b) throws IOException {}
}
