package ch.hsr.ifs.sconsolidator.core.targets.actions;

import java.util.Collection;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;

import ch.hsr.ifs.sconsolidator.core.EmptySConsPathException;
import ch.hsr.ifs.sconsolidator.core.SConsI18N;
import ch.hsr.ifs.sconsolidator.core.SConsImages;
import ch.hsr.ifs.sconsolidator.core.targets.BuildSConsTargetJob;
import ch.hsr.ifs.sconsolidator.core.targets.TargetCommand;
import ch.hsr.ifs.sconsolidator.core.targets.model.SConsBuildTarget;

public class BuildInteractiveTargetAction extends AbstractBuildTargetAction {
  public BuildInteractiveTargetAction() {
    super(SConsI18N.BuildInteractiveTargetAction_BuildInteractiveTargetName);
    setToolTipText(SConsI18N.BuildInteractiveTargetAction_BuildInteractiveTargetTooltip);
    SConsImages.setImageDescriptors(this, SConsImages.SCONS_TARGET_BUILD_INTERACTIVE);
  }

  @Override
  protected void buildTargets(Collection<SConsBuildTarget> targets, IProgressMonitor pm)
      throws EmptySConsPathException, CoreException {
    for (SConsBuildTarget target : targets) {
      TargetBuilder.buildTargetInteractive(target, pm);
    }
  }

  @Override
  protected void buildProjects(Collection<IProject> projects, IProgressMonitor pm)
      throws EmptySConsPathException, CoreException {
    for (IProject p : projects) {
      Job job = new BuildSConsTargetJob(createDefaultTargetCommand(p));
      job.schedule();
    }
  }

  private TargetCommand createDefaultTargetCommand(IProject project) {
    return new TargetCommand(TargetCommand.CommandType.BuildDefaultTarget, null, project);
  }
}
