package ch.hsr.ifs.sconsolidator.core.targets.actions;

import java.util.Collection;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;

import ch.hsr.ifs.sconsolidator.core.SConsI18N;
import ch.hsr.ifs.sconsolidator.core.SConsImages;
import ch.hsr.ifs.sconsolidator.core.targets.model.SConsBuildTarget;


public class BuildTargetAction extends AbstractBuildTargetAction {

    public BuildTargetAction() {
        super(SConsI18N.BuildTargetAction_BuildTargetName);
        setToolTipText(SConsI18N.BuildTargetAction_BuildTargetTooltip);
        SConsImages.setImageDescriptors(this, SConsImages.SCONS_TARGET_BUILD);
        setEnabled(false);
    }

    @Override
    protected void buildProjects(Collection<IProject> projects, IProgressMonitor pm) throws CoreException {
        for (IProject project : projects) {
            build(project, pm);
        }
    }

    private void build(final IProject project, IProgressMonitor pm) throws CoreException {
        IWorkspaceRunnable runnable = new IWorkspaceRunnable() {

            @Override
            public void run(IProgressMonitor pm) throws CoreException {
                project.build(IncrementalProjectBuilder.FULL_BUILD, SubMonitor.convert(pm, 1));
            }
        };
        ResourcesPlugin.getWorkspace().run(runnable, null, IResource.NONE, pm);
    }

    @Override
    protected void buildTargets(Collection<SConsBuildTarget> targets, IProgressMonitor pm) throws CoreException {
        for (SConsBuildTarget target : targets) {
            TargetBuilder.buildTarget(target, pm);
        }
    }
}
