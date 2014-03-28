package ch.hsr.ifs.sconsolidator.core.commands;

import java.io.IOException;
import java.io.OutputStream;

import org.eclipse.swt.SWT;
import org.eclipse.ui.PartInitException;

public interface SConsConsole {
  OutputStream getConsoleOutputStream(ConsoleOutput kind);

  void print(String line) throws IOException;

  void println(String line) throws IOException;

  void show() throws PartInitException;

  void clear();

  enum ConsoleOutput {
    ERROR {
      @Override
      public int getColor() {
        return SWT.COLOR_RED;
      }
    },
    NORMAL {
      @Override
      public int getColor() {
        return SWT.COLOR_BLUE;
      }
    };

    public abstract int getColor();
  }
}
