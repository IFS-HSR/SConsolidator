package ch.hsr.ifs.sconsolidator.core.console.interactive.actions;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;

import ch.hsr.ifs.sconsolidator.core.SConsImages;
import ch.hsr.ifs.sconsolidator.core.targets.BuildSConsTargetJob;

abstract class AbstractCurrentTargetAction extends Action implements CurrentTargetAction {
  private IFile currentTarget;

  public AbstractCurrentTargetAction(String tooltipText, SConsImages actionImg) {
    setToolTipText(tooltipText);
    SConsImages.setImageDescriptors(this, actionImg);
    setEnabled(false);
  }

  @Override
  public void setCurrentTarget(IFile target) {
    currentTarget = target;
  }

  public IFile getCurrentTarget() {
    return currentTarget;
  }

  @Override
  public void run() {
    if (currentTarget != null) {
      Job buildSConsTarget = new BuildSConsTargetJob(getTargetCommand());
      buildSConsTarget.schedule();
    }
  }
}
