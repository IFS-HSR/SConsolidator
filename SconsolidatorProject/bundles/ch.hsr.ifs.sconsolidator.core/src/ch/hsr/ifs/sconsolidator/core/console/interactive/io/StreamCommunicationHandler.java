package ch.hsr.ifs.sconsolidator.core.console.interactive.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;

import org.eclipse.cdt.internal.ui.preferences.BuildConsolePreferencePage;
import org.eclipse.cdt.ui.CUIPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.console.IOConsoleInputStream;
import org.eclipse.ui.console.IOConsoleOutputStream;

import ch.hsr.ifs.sconsolidator.core.SConsI18N;
import ch.hsr.ifs.sconsolidator.core.SConsPlugin;
import ch.hsr.ifs.sconsolidator.core.base.utils.IOUtil;
import ch.hsr.ifs.sconsolidator.core.base.utils.TeeOutputStream;
import ch.hsr.ifs.sconsolidator.core.base.utils.UIUtil;
import ch.hsr.ifs.sconsolidator.core.console.interactive.InteractiveConsole;

@SuppressWarnings("restriction")
public class StreamCommunicationHandler {
  private static final int MAX_WAIT_TIME_FOR_BUMPERS_IN_MS = 1000;
  private final Process process;
  private final InteractiveConsole console;
  private IOConsoleInputStream bisInInner;
  private BufferedOutputStream bosOutOuter;
  private IOConsoleOutputStream bosOutInner;
  private BufferedInputStream bisInOuter;
  private TeeOutputStream bosErrInner;
  private BufferedInputStream bisErrOuter;
  private InputStreamPumper stdinPumper;
  private OutputStreamPumper stdoutPumper;
  private OutputStreamPumper stderrPumper;
  private Thread stdInThread;
  private Thread stdOutThread;
  private Thread stdErrThread;

  public StreamCommunicationHandler(InteractiveConsole console, Process process) {
    this.console = console;
    this.process = process;
    prepareStreams();
    preparePumpers();
  }

  private void prepareStreams() {
    bisInInner = console.getInputStream();
    setInputStreamColor(bisInInner);
    bosOutOuter = new BufferedOutputStream(process.getOutputStream());
    bosOutInner = console.newOutputStream();
    bisInOuter = new BufferedInputStream(process.getInputStream());
    IOConsoleOutputStream ioc = console.newOutputStream();
    setErrorStreamColor(ioc);
    bisErrOuter = new BufferedInputStream(process.getErrorStream());
    bosErrInner = new TeeOutputStream(ioc, console.getErrorParserManager().getOutputStream());
  }

  private void preparePumpers() {
    stdinPumper = new InputStreamPumper(bisInInner, bosOutOuter);
    stdInThread = new Thread(stdinPumper, "SConsolidator interactive stdin");
    stdInThread.setDaemon(true);
    stdoutPumper = new OutputStreamPumper(console, bisInOuter, bosOutInner);
    stdOutThread = new Thread(stdoutPumper, "SConsolidator interactive stdout");
    stdOutThread.setDaemon(true);
    stderrPumper = new OutputStreamPumper(console, bisErrOuter, bosErrInner);
    stdErrThread = new Thread(stderrPumper, "SConsolidator interactive stderr");
    stdErrThread.setDaemon(true);
  }

  private void setErrorStreamColor(final IOConsoleOutputStream tmpbosErrInner) {
    UIUtil.runInDisplayThread(new Runnable() {
      @Override
      public void run() {
        Color color = createColor(Display.getCurrent(), BuildConsolePreferencePage.PREF_BUILDCONSOLE_ERROR_COLOR);
        tmpbosErrInner.setColor(color);
      }
    });
  }

  private void setInputStreamColor(final IOConsoleInputStream iStream) {
    UIUtil.runInDisplayThread(new Runnable() {
      @Override
      public void run() {
        Color color = createColor(Display.getCurrent(), BuildConsolePreferencePage.PREF_BUILDCONSOLE_INFO_COLOR);
        iStream.setColor(color);
      }
    });
  }
  
  private Color createColor(final Display display, final String preference) {
    RGB rgb = PreferenceConverter.getColor(CUIPlugin.getDefault().getPreferenceStore(), preference);
    return new Color(display, rgb);
  }

  public IOConsoleOutputStream getConsoleOutputStream() {
    return bosOutInner;
  }

  public void closeStreams() {
    IOUtil.safeClose(bisInInner);
    IOUtil.safeClose(bosOutOuter);
    IOUtil.safeClose(bosOutInner);
    IOUtil.safeClose(bisErrOuter);
    IOUtil.safeClose(bosErrInner);
    IOUtil.safeClose(console.getErrorParserManager());
    IOUtil.safeClose(console.getErrorParserManager().getOutputStream());
  }

  public void writeCommand(byte[] commandLine) throws CoreException {
    try {
      bosOutInner.write(commandLine);
      bosOutInner.flush();
      bosOutOuter.write(commandLine);
      bosOutOuter.flush();
    } catch (IOException e) {
      IStatus status =
          new Status(IStatus.ERROR, SConsPlugin.PLUGIN_ID, 0,
              SConsI18N.InteractiveConsole_ErrorSendingCommand, e);
      throw new CoreException(status);
    }
  }

  public void startPumpers() {
    stdInThread.start();
    stdOutThread.start();
    stdErrThread.start();
  }

  public void stopPumpers() {
    stopOutputPumpers();
    stopInputPumper();
  }

  private void stopOutputPumpers() {
    stdoutPumper.stopProcessing();

    try {
      stdOutThread.join(MAX_WAIT_TIME_FOR_BUMPERS_IN_MS);
    } catch (InterruptedException e) {
    }

    stderrPumper.stopProcessing();

    try {
      stdErrThread.join(MAX_WAIT_TIME_FOR_BUMPERS_IN_MS);
    } catch (InterruptedException e) {
    }
  }

  public void stopInputPumper() {
    stdinPumper.stopProcessing();

    try {
      stdInThread.join(MAX_WAIT_TIME_FOR_BUMPERS_IN_MS);
    } catch (InterruptedException e) {
    }
  }
}
