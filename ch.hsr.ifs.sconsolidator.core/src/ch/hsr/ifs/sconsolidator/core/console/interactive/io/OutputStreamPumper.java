package ch.hsr.ifs.sconsolidator.core.console.interactive.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import ch.hsr.ifs.sconsolidator.core.console.interactive.InteractiveConsole;

public class OutputStreamPumper implements Runnable {
  private final InteractiveConsole console;
  private final InputStream is;
  private final OutputStream os;
  private volatile boolean stop;

  public OutputStreamPumper(InteractiveConsole console, InputStream is, OutputStream os) {
    this.console = console;
    this.is = is;
    this.os = os;
    this.stop = false;
  }

  public void stopProcessing() {
    stop = true;
  }

  @Override
  public void run() {
    try {
      byte[] buf = new byte[1024];
      int length = 0;

      while (!stop && (length = is.read(buf)) > 0) {
        synchronized (console) {
          os.write(buf, 0, length);
        }
      }
    } catch (IOException e) {
      // Ignore
    }

    if (!stop) {
      // user must have entered exit which caused process to die
      onExit();
    }
  }

  private void onExit() {
    console.onProcessFinish();
    console.closeStreams();
    console.stopInputPumper();
  }
}
