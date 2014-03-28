package ch.hsr.ifs.sconsolidator.core.console;

import java.io.IOException;
import java.io.OutputStream;

import org.eclipse.ui.PartInitException;

import ch.hsr.ifs.sconsolidator.core.base.utils.NullOutputStream;
import ch.hsr.ifs.sconsolidator.core.commands.SConsConsole;

public class NullConsole implements SConsConsole {
  @Override
  public OutputStream getConsoleOutputStream(ConsoleOutput kind) {
    return new NullOutputStream();
  }

  @Override
  public void print(final String line) throws IOException {}

  @Override
  public void println(final String line) throws IOException {}

  @Override
  public void show() throws PartInitException {}

  @Override
  public void clear() {}
}
