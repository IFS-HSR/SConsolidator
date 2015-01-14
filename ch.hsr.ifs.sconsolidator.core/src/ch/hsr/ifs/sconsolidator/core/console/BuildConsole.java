package ch.hsr.ifs.sconsolidator.core.console;

import java.io.IOException;
import java.io.OutputStream;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.IConsoleView;
import org.eclipse.ui.console.IOConsoleOutputStream;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

import ch.hsr.ifs.sconsolidator.core.SConsI18N;
import ch.hsr.ifs.sconsolidator.core.SConsImages;
import ch.hsr.ifs.sconsolidator.core.SConsPlugin;
import ch.hsr.ifs.sconsolidator.core.base.utils.UIUtil;
import ch.hsr.ifs.sconsolidator.core.commands.SConsConsole;
import ch.hsr.ifs.sconsolidator.core.preferences.PreferenceConstants;

public class BuildConsole implements SConsConsole {
  private static final String DEFAULT_NAME = SConsI18N.Console_Title;
  private final MessageConsole console;

  public BuildConsole(IProject project) {
    this(getConsoleName(project));
    addCompileErrorListener(project);
  }

  public BuildConsole(String title) {
    console = findOrCreateConsole(title);
  }

  private void addCompileErrorListener(IProject project) {
    console.addPatternMatchListener(new CompileErrorPatternMatcher(project));
    console.addPatternMatchListener(new FortranErrorPatternMatcher(project));
  }

  private static String getConsoleName(IProject project) {
    return String.format("%s [%s]", DEFAULT_NAME, project.getName());
  }

  private static MessageConsole findOrCreateConsole(String name) {
    IConsoleManager manager = getConsoleManager();

    for (IConsole console : manager.getConsoles())
      if (name.equals(console.getName()))
        return (MessageConsole) console;

    return createConsole(name, manager);
  }

  private static MessageConsole createConsole(String name, IConsoleManager manager) {
    ImageDescriptor img = SConsImages.getImageDescriptor(SConsImages.SCONS_TARGET);
    MessageConsole console = new MessageConsole(name, img);
    manager.addConsoles(new IConsole[] {console});
    return console;
  }

  private static IConsoleManager getConsoleManager() {
    return ConsolePlugin.getDefault().getConsoleManager();
  }

  @Override
  public OutputStream getConsoleOutputStream(final ConsoleOutput kind) {
    IOConsoleOutputStream output = console.newOutputStream();
    output.setActivateOnWrite(false);
    setColorInDisplayThread(output, kind.getColor());
    return output;
  }

  @Override
  public void print(String line) throws IOException {
    MessageConsoleStream output = console.newMessageStream();
    setColorInDisplayThread(output, SWT.COLOR_DARK_MAGENTA);
    output.print(line);
    output.close();
  }

  @Override
  public void println(String line) throws IOException {
    MessageConsoleStream output = console.newMessageStream();
    setColorInDisplayThread(output, SWT.COLOR_DARK_MAGENTA);
    output.println(line);
    output.close();
  }

  private void setColorInDisplayThread(final IOConsoleOutputStream output, final int color) {
    UIUtil.runInDisplayThread(new Runnable() {
      @Override
      public void run() {
        output.setColor(Display.getCurrent().getSystemColor(color));
      }
    });
  }

  @Override
  public void show() throws PartInitException {
    IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
    IConsoleView view = (IConsoleView) page.showView(IConsoleConstants.ID_CONSOLE_VIEW);
    view.display(console);
  }

  @Override
  public void clear() {
    console.clearConsole();
  }

  public static void showConsole(final SConsConsole console) {
    if (!openConsoleWhenBuildingActivated())
      return;

    UIUtil.runInDisplayThread(new Runnable() {
      @Override
      public void run() {
        try {
          console.show();
        } catch (PartInitException e) {
          SConsPlugin.log(new CoreException(new Status(IStatus.ERROR, SConsPlugin.PLUGIN_ID, e
              .getMessage())));
        }
      }
    });
  }

  private static boolean openConsoleWhenBuildingActivated() {
    return SConsPlugin.getConfigPreferenceStore().getBoolean(
        PreferenceConstants.OPEN_CONSOLE_WHEN_BUILDING);
  }
}
