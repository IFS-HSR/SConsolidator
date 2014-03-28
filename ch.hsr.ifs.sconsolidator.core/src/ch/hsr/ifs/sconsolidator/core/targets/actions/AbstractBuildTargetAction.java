package ch.hsr.ifs.sconsolidator.core.targets.actions;

import static ch.hsr.ifs.sconsolidator.core.SConsI18N.BuildTargetAction_BuildingSConsTargetsInProgress;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.BuildAction;
import org.eclipse.ui.actions.SelectionListenerAction;

import ch.hsr.ifs.sconsolidator.core.EmptySConsPathException;
import ch.hsr.ifs.sconsolidator.core.SConsNatureTypes;
import ch.hsr.ifs.sconsolidator.core.SConsPlugin;
import ch.hsr.ifs.sconsolidator.core.preferences.pages.ExecutableNotFoundHandler;
import ch.hsr.ifs.sconsolidator.core.targets.model.SConsBuildTarget;

public abstract class AbstractBuildTargetAction extends SelectionListenerAction {

  protected AbstractBuildTargetAction(String text) {
    super(text);
  }

  @Override
  public void run() {
    if (!canBuild())
      return;

    List<SConsBuildTarget> buildTargets = new ArrayList<SConsBuildTarget>();
    List<IProject> projects = new ArrayList<IProject>();

    for (Object e : getSelectedElements())
      if (e instanceof SConsBuildTarget) {
        buildTargets.add((SConsBuildTarget) e);
      } else if (isSConsProject(e)) {
        projects.add((IProject) e);
      }

    build(buildTargets, projects);
  }

  private void build(Collection<SConsBuildTarget> targets, Collection<IProject> projects) {
    saveAllResources(targets, projects);
    Job targetBuildJob = getTargetBuildJob(targets, projects);
    targetBuildJob.schedule();
  }

  private Job getTargetBuildJob(final Collection<SConsBuildTarget> targets,
      final Collection<IProject> projects) {
    return new Job(BuildTargetAction_BuildingSConsTargetsInProgress) {
      @Override
      protected IStatus run(IProgressMonitor pm) {
        int totalWork = targets.size() + projects.size();
        pm.beginTask(BuildTargetAction_BuildingSConsTargetsInProgress, totalWork);
        try {
          buildTargets(targets, pm);
          buildProjects(projects, pm);
        } catch (CoreException e) {
          SConsPlugin.showExceptionInDisplayThread(e);
        } catch (OperationCanceledException e) {
        } catch (EmptySConsPathException e) {
          ExecutableNotFoundHandler.handleError();
        } finally {
          pm.done();
        }
        return Status.OK_STATUS;
      }

      @Override
      public boolean belongsTo(Object family) {
        return ResourcesPlugin.FAMILY_MANUAL_BUILD == family;
      }
    };
  }

  protected abstract void buildTargets(Collection<SConsBuildTarget> targets, IProgressMonitor pm)
      throws EmptySConsPathException, CoreException;

  protected abstract void buildProjects(Collection<IProject> projects, IProgressMonitor pm)
      throws EmptySConsPathException, CoreException;

  @Override
  protected boolean updateSelection(IStructuredSelection selection) {
    return super.updateSelection(selection) && canBuild();
  }

  private boolean canBuild() {
    List<?> elements = getSelectedElements();

    for (Object e : elements)
      if (!(e instanceof SConsBuildTarget || isSConsProject(e)))
        return false;

    return !elements.isEmpty();
  }

  private boolean isSConsProject(Object e) {
    if (!(e instanceof IProject))
      return false;

    IProject project = (IProject) e;
    return SConsNatureTypes.isOpenSConsProject(project);
  }

  private List<?> getSelectedElements() {
    return getStructuredSelection().toList();
  }

  protected void saveAllResources(Collection<SConsBuildTarget> targets,
      Collection<IProject> projects) {
    if (!BuildAction.isSaveAllSet())
      return;

    Set<IProject> projectsToCheck = getProjectsToCheck(targets, projects);
    for (IWorkbenchWindow window : PlatformUI.getWorkbench().getWorkbenchWindows()) {
      for (IWorkbenchPage page : window.getPages()) {
        for (IEditorReference editorReference : page.getEditorReferences()) {
          saveEditorIfNecessary(projectsToCheck, page, editorReference.getEditor(false));
        }
      }
    }
  }

  private Set<IProject> getProjectsToCheck(Collection<SConsBuildTarget> targets,
      Collection<IProject> projects) {
    Set<IProject> projectsToCheck = new HashSet<IProject>();

    for (SConsBuildTarget target : targets) {
      projectsToCheck.add(target.getProject());
    }
    projectsToCheck.addAll(projects);
    return projectsToCheck;
  }

  private void saveEditorIfNecessary(Collection<IProject> projects, IWorkbenchPage page,
      IEditorPart editor) {
    if (editor == null || !editor.isDirty())
      return;
    IEditorInput input = editor.getEditorInput();

    if (!(input instanceof IFileEditorInput))
      return;

    IFile inputFile = ((IFileEditorInput) input).getFile();

    if (projects.contains(inputFile.getProject())) {
      page.saveEditor(editor, false);
    }
  }
}
