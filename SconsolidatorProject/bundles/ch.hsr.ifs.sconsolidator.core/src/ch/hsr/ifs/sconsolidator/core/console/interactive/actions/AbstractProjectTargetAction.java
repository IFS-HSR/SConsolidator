package ch.hsr.ifs.sconsolidator.core.console.interactive.actions;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import ch.hsr.ifs.sconsolidator.core.targets.BuildSConsTargetJob;
import ch.hsr.ifs.sconsolidator.core.targets.TargetCommand;

abstract class AbstractProjectTargetAction implements IObjectActionDelegate {
  private IResource resource;

  @Override
  public void run(IAction action) {
    TargetCommand targetCommand = getTargetCommand();
    Job job = new BuildSConsTargetJob(targetCommand);
    job.schedule();
  }

  public abstract TargetCommand getTargetCommand();

  protected IProject getProject() {
    return resource.getProject();
  }

  protected IResource getSelectedResource() {
    return resource.getType() != IResource.PROJECT ? resource : null;
  }

  @Override
  public void selectionChanged(IAction action, ISelection selection) {
    if (selection instanceof IStructuredSelection) {
      IStructuredSelection sel = (IStructuredSelection) selection;
      resource = (IResource) sel.getFirstElement();
    }
  }

  @Override
  public void setActivePart(IAction action, IWorkbenchPart targetPart) {}
}
