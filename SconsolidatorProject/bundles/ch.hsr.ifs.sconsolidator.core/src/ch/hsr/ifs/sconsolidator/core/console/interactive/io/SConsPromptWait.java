package ch.hsr.ifs.sconsolidator.core.console.interactive.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.eclipse.ui.console.IOConsoleOutputStream;

import ch.hsr.ifs.sconsolidator.core.SConsPlugin;
import ch.hsr.ifs.sconsolidator.core.base.utils.TeeInputStream;

public class SConsPromptWait {
  private static final String SCONS_PROMPT = "scons>>>";
  private static final String SCONS_DONE_READING_SCONSCRIPT_FILES_MSG =
      "scons: done reading SConscript files.";
  private final InputStream is;
  private final IOConsoleOutputStream os;

  public SConsPromptWait(InputStream is, IOConsoleOutputStream os) {
    this.is = is;
    this.os = os;
  }

  public void waitForSConsPrompt() {
    TeeInputStream teeIs = new TeeInputStream(is, os);
    BufferedReader buf = new BufferedReader(new InputStreamReader(teeIs));

    try {
      String line;
      while ((line = buf.readLine()) != null) {
        if (line.startsWith(SCONS_DONE_READING_SCONSCRIPT_FILES_MSG)) {
          break;
        }
      }

      StringBuilder buff = new StringBuilder();
      int c;

      while ((c = buf.read()) != -1) {
        buff.append((char) c);
        if (buff.toString().equals(SCONS_PROMPT)) {
          break;
        }
      }
    } catch (IOException e) {
      SConsPlugin.log(e);
    }
  }
}
