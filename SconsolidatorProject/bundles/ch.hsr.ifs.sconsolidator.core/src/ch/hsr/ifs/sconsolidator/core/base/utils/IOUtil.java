package ch.hsr.ifs.sconsolidator.core.base.utils;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

public final class IOUtil {
  private IOUtil() {}

  public static void safeClose(Closeable toClose) {
    if (toClose != null) {
      try {
        toClose.close();
      } catch (IOException e) {
        // Do nothing
      }
    }
  }

  public static InputStream stringToStream(String text) {
    try {
      return new ByteArrayInputStream(text.getBytes("UTF-8")); 
    } catch (UnsupportedEncodingException e) {
      return new ByteArrayInputStream(text.getBytes());
    }
  }
}
