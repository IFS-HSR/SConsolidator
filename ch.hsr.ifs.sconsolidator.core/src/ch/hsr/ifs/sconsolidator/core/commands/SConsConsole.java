package ch.hsr.ifs.sconsolidator.core.commands;

import java.io.IOException;
import java.io.OutputStream;

import org.eclipse.cdt.internal.ui.preferences.BuildConsolePreferencePage;
import org.eclipse.cdt.ui.CUIPlugin;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.swt.SWT;
import org.eclipse.ui.PartInitException;

public interface SConsConsole {
  OutputStream getConsoleOutputStream(ConsoleOutput kind);

  void print(String line) throws IOException;

  void println(String line) throws IOException;

  void show() throws PartInitException;

  void clear();
  
  void addBuildConsoleColorLink();

  enum ConsoleOutput {
    ERROR {
      @Override
      public String getColorPreference() {
        return BuildConsolePreferencePage.PREF_BUILDCONSOLE_ERROR_COLOR;
      }
    },
    NORMAL {
      @Override
      public String getColorPreference() {
        return BuildConsolePreferencePage.PREF_BUILDCONSOLE_OUTPUT_COLOR;
      }
    };

    public abstract String getColorPreference();
  }
}
