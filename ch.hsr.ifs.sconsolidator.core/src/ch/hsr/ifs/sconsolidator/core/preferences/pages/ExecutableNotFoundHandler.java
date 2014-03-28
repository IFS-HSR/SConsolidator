package ch.hsr.ifs.sconsolidator.core.preferences.pages;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.PreferencesUtil;

import ch.hsr.ifs.sconsolidator.core.EmptySConsPathException;
import ch.hsr.ifs.sconsolidator.core.SConsI18N;
import ch.hsr.ifs.sconsolidator.core.SConsPlugin;
import ch.hsr.ifs.sconsolidator.core.base.utils.UIUtil;
import ch.hsr.ifs.sconsolidator.core.preferences.PreferenceConstants;

public final class ExecutableNotFoundHandler {
  private ExecutableNotFoundHandler() {}

  public static void handleError() {
    Runnable runnable = new Runnable() {
      @Override
      public void run() {
        Shell shell = UIUtil.getWindowShell();
        if (askUser(shell)) {
          openDialog(shell);
        } else {
          logMissingSConsExecutableWarning();
        }
      }
    };
    UIUtil.runInDisplayThread(runnable);
  }

  private static void openDialog(Shell shell) {
    PreferenceDialog dialog =
        PreferencesUtil.createPreferenceDialogOn(shell,
            PreferenceConstants.EXECUTABLE_PATH_PAGE_ID, null, null);
    dialog.open();
  }

  private static void logMissingSConsExecutableWarning() {
    EmptySConsPathException e = new EmptySConsPathException();
    IStatus status = new Status(IStatus.WARNING, SConsPlugin.PLUGIN_ID, 0, e.getMessage(), e);
    SConsPlugin.log(new CoreException(status));
  }

  private static boolean askUser(Shell shell) {
    return MessageDialog.openQuestion(shell, SConsI18N.SConsBuilder_EmptySConsExecutableErrorTitle,
        SConsI18N.SConsBuilder_EmptySConsExecutableErrorMsg);
  }
}
