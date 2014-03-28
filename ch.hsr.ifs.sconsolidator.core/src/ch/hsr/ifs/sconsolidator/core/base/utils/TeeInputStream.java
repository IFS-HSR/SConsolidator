package ch.hsr.ifs.sconsolidator.core.base.utils;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class TeeInputStream extends FilterInputStream {
  private final OutputStream branch;

  public TeeInputStream(InputStream istream, OutputStream ostream) {
    super(istream);
    this.branch = ostream;
  }

  @Override
  public int read() throws IOException {
    int ch = super.read();
    if (ch != -1) {
      branch.write(ch);
    }
    return ch;
  }

  @Override
  public int read(byte[] bts, int st, int end) throws IOException {
    int n = super.read(bts, st, end);
    if (n != -1) {
      branch.write(bts, st, n);
    }
    return n;
  }

  @Override
  public int read(byte[] bts) throws IOException {
    int n = super.read(bts);
    if (n != -1) {
      branch.write(bts, 0, n);
    }
    return n;
  }

  @Override
  public void close() throws IOException {
    super.close();
    branch.close();
  }
}
