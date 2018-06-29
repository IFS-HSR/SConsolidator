package ch.hsr.ifs.sconsolidator.core.commands;

import static ch.hsr.ifs.sconsolidator.core.SConsI18N.AbstractSConsCommand_ProblemsOccuredCallingSCons;
import static ch.hsr.ifs.sconsolidator.core.base.utils.CollectionUtil.map;
import static ch.hsr.ifs.sconsolidator.core.preferences.PreferenceConstants.ADDITIONAL_COMMANDLINE_OPTIONS;
import static ch.hsr.ifs.sconsolidator.core.preferences.PreferenceConstants.BUILD_SETTINGS_PAGE_ID;
import static ch.hsr.ifs.sconsolidator.core.preferences.PreferenceConstants.CLEAR_CONSOLE_BEFORE_BUILD;
import static ch.hsr.ifs.sconsolidator.core.preferences.PreferenceConstants.ENVIRONMENT_VARIABLES;
import static ch.hsr.ifs.sconsolidator.core.preferences.PreferenceConstants.EXECUTABLE_PATH;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteResultHandler;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.Executor;
import org.apache.commons.exec.PumpStreamHandler;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.osgi.util.NLS;

import ch.hsr.ifs.sconsolidator.core.EmptySConsPathException;
import ch.hsr.ifs.sconsolidator.core.PlatformSpecifics;
import ch.hsr.ifs.sconsolidator.core.SConsI18N;
import ch.hsr.ifs.sconsolidator.core.SConsPlugin;
import ch.hsr.ifs.sconsolidator.core.base.utils.StringUtil;
import ch.hsr.ifs.sconsolidator.core.base.utils.TeeOutputStream;
import ch.hsr.ifs.sconsolidator.core.commands.SConsConsole.ConsoleOutput;

abstract class SConsCommand {
  private static final String SCONS_COMMANDS_NOT_EXECUTED = "-n"; 
  private static final int SLEEP_TIME_MS = 100;
  private final SConsConsole console;
  private final String[] defaultArguments;
  private final IProject project;
  private ExecuteWatchdog watchdog;
  private Executor executor;
  private ByteArrayOutputStream out, err;
  private TeeOutputStream processStdOut, processStdErr;
  private String binaryPath;
  private CommandLine cmdLine;
  private long startTime;

  public SConsCommand(String binaryPath, IProject project, SConsConsole console,
      String[] defaultArguments) throws EmptySConsPathException {
    this.console = console;
    this.project = project;
    this.defaultArguments = defaultArguments;
    this.binaryPath = binaryPath;
    initBinaryPath();
    initExecutor();
    initStreams();
  }

  private void initBinaryPath() throws EmptySConsPathException {
    if (binaryPath == null) {
      binaryPath = SConsPlugin.getConfigPreferenceStore().getString(EXECUTABLE_PATH);
    }
    if (binaryPath.isEmpty())
      throw new EmptySConsPathException();
  }

  private void initExecutor() {
    executor = new DefaultExecutor();
    executor.setExitValue(0);
    watchdog = new ExecuteWatchdog(ExecuteWatchdog.INFINITE_TIMEOUT);
    executor.setWatchdog(watchdog);
  }

  private void initStreams() {
    out = new ByteArrayOutputStream();
    err = new ByteArrayOutputStream();
    processStdOut = new TeeOutputStream(out, console.getConsoleOutputStream(ConsoleOutput.NORMAL));
    processStdErr = new TeeOutputStream(err, console.getConsoleOutputStream(ConsoleOutput.ERROR));
    executor.setStreamHandler(new PumpStreamHandler(processStdOut, processStdErr));
  }

  public ByteArrayOutputStream getErrorStream() {
    return err;
  }

  protected String getOutput() {
    String output = out.toString();
    out.reset();
    return output;
  }

  protected String getError() {
    String output = err.toString();
    err.reset();
    return output;
  }

  protected abstract Collection<String> getArguments();

  public String run(File workingDir, IProgressMonitor pm) throws IOException, InterruptedException {
    setWorkingDirectory(workingDir);
    SConsProcessResultHandler resultHandler = runInternal(getArguments());

    try {
      performWait(resultHandler, pm);
      handleExit(resultHandler);
      return out.toString();
    } finally {
      if (resultHandler.isRunning()) {
        watchdog.destroyProcess();
      }
    }
  }

  protected void performWait(SConsProcessResultHandler handler, IProgressMonitor pm)
      throws InterruptedException {
    while (handler.isRunning()) {
      Thread.sleep(SLEEP_TIME_MS);
      if (pm.isCanceled()) {
        watchdog.destroyProcess();
        throw new InterruptedException("Process manually killed"); 
      }
    }
  }

  private void setWorkingDirectory(File dir) {
    executor.setWorkingDirectory(dir);
  }

  protected SConsProcessResultHandler runInternal(Collection<String> arguments) throws IOException {
    initCmdLine(arguments);
    clearConsoleIfNecessary();
    printConsoleHeader();
    startTimer();
    return execute();
  }

  private SConsProcessResultHandler execute() throws IOException {
    SConsProcessResultHandler resultHandler = new SConsProcessResultHandler();
    try {
      executor.execute(cmdLine, getEnvironment(), resultHandler);
    } catch (ExecuteException e) {
      SConsPlugin.log(e);
    }
    return resultHandler;
  }

  private Map<String, String> getEnvironment() {
    // the copy here is necessary
    Map<String, String> sysEnv = map(PlatformSpecifics.getSystemEnv());

    if (project == null)
      return sysEnv;

    for (String option : StringUtil.split(getProjectEnvOptions())) {
      String[] splitted = option.split("="); 

      if (splitted.length == 2) {
        String expandedVal = PlatformSpecifics.expandEnvVariables(splitted[1]);
        sysEnv.put(splitted[0], expandedVal);
      }
    }

    return sysEnv;
  }

  private String getProjectEnvOptions() {
    IPreferenceStore buildSettings =
        SConsPlugin.getActivePreferences(project, BUILD_SETTINGS_PAGE_ID);
    return buildSettings.getString(ENVIRONMENT_VARIABLES);
  }

  private void startTimer() {
    startTime = System.currentTimeMillis();
  }

  private void initCmdLine(Collection<String> arguments) {
    cmdLine = new CommandLine(binaryPath);

    if (defaultArguments != null) {
      cmdLine.addArguments(defaultArguments, false);
    }

    if (arguments != null) {
      cmdLine.addArguments(arguments.toArray(new String[arguments.size()]), false);
    }

    SConsPlugin.log(cmdLine.toString());
  }

  private void printConsoleHeader() throws IOException {
    SimpleDateFormat format = new SimpleDateFormat();
    console.println(getConsoleColorInfo());
    console.println(getSConsHeader(format));
    console.println(getCommandLine());
  }

  private String getConsoleColorInfo() {
    return NLS.bind(SConsI18N.AbstractSConsCommand_ConsoleColorInfo, 
                  SConsI18N.AbstractSConsCommand_ConsoleColorInfoLinkText);
  }
  
  private String getCommandLine() {
    return NLS.bind(SConsI18N.AbstractSConsCommand_CommandLinePrefix, cmdLine.toString());
  }

  private String getSConsHeader(SimpleDateFormat format) {
    return NLS.bind(SConsI18N.AbstractSConsCommand_RunningSConsHeader, format.format(new Date()));
  }

  private void clearConsoleIfNecessary() {
    if (SConsPlugin.getConfigPreferenceStore().getBoolean(CLEAR_CONSOLE_BEFORE_BUILD)) {
      console.clear();
    }
  }

  protected void handleExit(SConsProcessResultHandler resultHandler) throws IOException {
    try {
      int exitValue = resultHandler.getExitValue();
      raiseProblemIfNecessary(exitValue, null);
    } catch (ExecuteException e) {
      raiseProblemIfNecessary(e.getExitValue(), e);
    } finally {
      processStdOut.close();
      processStdErr.close();
    }
    printCommandDurationOnConsole();
    // we have to defer linking to the build console preference page because if we do it right 
    // away the link is not always rendered
    console.addBuildConsoleColorLink(); 
  }

  private void raiseProblemIfNecessary(int exitValue, Exception e) {
    // SCons executable returns value 2 if a problem with a tool invocation
    // (e.g., compiler) happened; therefore we only want to report errors
    // which have not been generated by a tool and are really SCons related
    if (exitValue != 0 && !haveCommandsBeenExecuted())
      throw new RuntimeException(AbstractSConsCommand_ProblemsOccuredCallingSCons + ": "
          + PlatformSpecifics.NEW_LINE + getError(), e);
  }

  private boolean haveCommandsBeenExecuted() {
    for (String arg : defaultArguments)
      if (arg.equals(SCONS_COMMANDS_NOT_EXECUTED))
        return false;
    return true;
  }

  private void printCommandDurationOnConsole() throws IOException {
    long endTime = System.currentTimeMillis();
    String duration = String.valueOf(endTime - startTime);
    console.println(NLS.bind(SConsI18N.AbstractSConsCommand_CommandDurationMsg, duration));
    console.println("");
  }

  protected List<String> getAdditionalSConsOptions(IProject project) {
    IPreferenceStore prefs = SConsPlugin.getActivePreferences(project, BUILD_SETTINGS_PAGE_ID);
    return StringUtil.split(PlatformSpecifics.expandEnvVariables(prefs
        .getString(ADDITIONAL_COMMANDLINE_OPTIONS)));
  }

  protected static class SConsProcessResultHandler implements ExecuteResultHandler {
    private boolean runs = true;
    private int exitValue = 0;
    private ExecuteException exception = null;

    @Override
    public synchronized void onProcessComplete(int exitValue) {
      runs = false;
      this.exitValue = exitValue;
    }

    @Override
    public synchronized void onProcessFailed(ExecuteException exception) {
      runs = false;
      this.exception = exception;
    }

    public synchronized int getExitValue() throws ExecuteException {
      if (exception != null)
        throw exception;
      return exitValue;
    }

    public synchronized boolean isRunning() {
      return runs;
    }
  }
}
