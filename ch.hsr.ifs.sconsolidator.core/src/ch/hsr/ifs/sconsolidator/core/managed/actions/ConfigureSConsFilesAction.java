package ch.hsr.ifs.sconsolidator.core.managed.actions;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.actions.WorkspaceModifyOperation;

import ch.hsr.ifs.sconsolidator.core.SConsHelper;
import ch.hsr.ifs.sconsolidator.core.SConsI18N;
import ch.hsr.ifs.sconsolidator.core.base.utils.UIUtil;
import ch.hsr.ifs.sconsolidator.core.managed.SConsFileWriter;

public class ConfigureSConsFilesAction extends WorkspaceModifyOperation {
  private final IProject project;

  public ConfigureSConsFilesAction(IProject project) {
    this.project = project;
  }

  @Override
  protected void execute(IProgressMonitor pm) throws CoreException, InvocationTargetException,
      InterruptedException {
    if (doesSConstructExist() && !askUserToContinue())
      throw new OperationCanceledException(
          SConsI18N.DialogWarnUserAboutExistingSConstruct_CancelledError);

    pm.beginTask(SConsI18N.ConfigureSConsFilesAction_CreatingSConsBuilderFilesMessage, 3);

    try {
      SConsFileWriter fileWriter = new SConsFileWriter(project);
      fileWriter.writeSConstruct();
      pm.worked(1);
      fileWriter.writeSConfig();
      pm.worked(1);
      fileWriter.writeSConscripts();
    } finally {
      pm.done();
    }
  }

  private boolean askUserToContinue() {
    return MessageDialog.openConfirm(UIUtil.getWindowShell(),
        SConsI18N.DialogWarnUserAboutExistingSConstruct_Title,
        SConsI18N.DialogWarnUserAboutExistingSConstruct_Msg);
  }

  private boolean doesSConstructExist() {
    return project.getFile(SConsHelper.getSConstructFileName()).exists();
  }
}
