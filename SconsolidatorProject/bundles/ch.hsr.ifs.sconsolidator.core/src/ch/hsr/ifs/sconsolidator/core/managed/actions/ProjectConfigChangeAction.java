package ch.hsr.ifs.sconsolidator.core.managed.actions;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.actions.WorkspaceModifyOperation;

import ch.hsr.ifs.sconsolidator.core.SConsI18N;
import ch.hsr.ifs.sconsolidator.core.managed.SConsFileWriter;

public class ProjectConfigChangeAction extends WorkspaceModifyOperation {
  private SConsFileWriter fileWriter;

  @Override
  protected void execute(IProgressMonitor monitor) throws CoreException, InvocationTargetException,
      InterruptedException {
    monitor.beginTask(SConsI18N.ProjectConfigChangeAction_UpdatingSConfigMessage, 1);

    try {
      fileWriter.writeSConfig();
    } finally {
      monitor.done();
    }
  }

  public void run(IProject project, IProgressMonitor monitor) throws CoreException,
      InvocationTargetException, InterruptedException {
    fileWriter = new SConsFileWriter(project);
    execute(monitor);
  }
}
