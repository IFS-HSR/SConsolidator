package ch.hsr.ifs.sconsolidator.core.targets.actions;

import org.eclipse.cdt.core.model.ICContainer;
import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionDelegate;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.ide.ResourceUtil;
import org.eclipse.ui.part.EditorPart;

import ch.hsr.ifs.sconsolidator.core.SConsI18N;
import ch.hsr.ifs.sconsolidator.core.SConsPlugin;
import ch.hsr.ifs.sconsolidator.core.targets.SConsTargetView;
import ch.hsr.ifs.sconsolidator.core.targets.model.SConsBuildTarget;
import ch.hsr.ifs.sconsolidator.core.targets.model.SConsBuildTargetManager;

public class BuildLastTargetAction extends ActionDelegate implements IObjectActionDelegate,
    IWorkbenchWindowActionDelegate {
  protected IContainer container;
  private IWorkbenchPart workbenchPart;
  private IWorkbenchWindow windowPart;
  private boolean enabled;

  @Override
  public void run(IAction action) {
    IContainer container = getSelectedContainer();

    if (container == null)
      return;

    container = container.getProject();

    try {
      String lastTargetName =
          (String) container.getSessionProperty(new QualifiedName(SConsPlugin.getPluginId(),
              "lastTarget"));

      if (lastTargetName != null) {
        rebuildLastTarget(container, lastTargetName);
      } else {
        // no target for selected project built yet
        showTargetView();
      }
    } catch (CoreException e) {
      SConsPlugin.showExceptionInDisplayThread(e);
    }
  }

  private void showTargetView() throws PartInitException {
    PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
        .showView(SConsTargetView.VIEW_ID);
  }

  private void rebuildLastTarget(IContainer container, String lastTargetName) throws CoreException {
    SConsBuildTarget target = getTargetManager().findTarget(container, lastTargetName);
    if (target != null) {
      Job targetJob = getTargetJob(target, isInteractive(container));
      targetJob.schedule();
    }
  }

  private SConsBuildTargetManager getTargetManager() {
    return SConsPlugin.getDefault().getSConsTargetManager();
  }

  private boolean isInteractive(IContainer container) throws CoreException {
    Object val =
        container.getSessionProperty(new QualifiedName(SConsPlugin.getPluginId(),
            "lastTargetInteractive"));
    if (val == null)
      return false;
    return (Boolean) val;
  }

  private Job getTargetJob(final SConsBuildTarget target, final boolean interactive) {
    return new Job(SConsI18N.BuildTargetAction_BuildingSConsTargetsInProgress) {
      @Override
      protected IStatus run(final IProgressMonitor monitor) {
        monitor.beginTask(SConsI18N.BuildTargetAction_BuildingSConsTargetsInProgress, 1);

        try {
          if (interactive) {
            TargetBuilder.buildTargetInteractive(target, new NullProgressMonitor());
          } else {
            TargetBuilder.buildTarget(target, new NullProgressMonitor());
          }
        } catch (CoreException e) {
          SConsPlugin.showExceptionInDisplayThread(e);
        } finally {
          monitor.done();
        }

        return Status.OK_STATUS;
      }
    };
  }

  private IContainer getSelectedContainer() {
    return container;
  }

  @Override
  public void setActivePart(IAction action, IWorkbenchPart targetPart) {
    workbenchPart = targetPart;
  }

  @Override
  public void init(IWorkbenchWindow window) {
    windowPart = window;
  }

  @Override
  public void selectionChanged(IAction action, ISelection selection) {
    enabled = false;

    if (selection instanceof IStructuredSelection) {
      handleStructuredSelection(selection);
    } else if (selection instanceof ITextSelection) {
      handleEditorSelection();
    }

    if (container != null && getTargetManager().hasTargetBuilder(container.getProject())) {
      enabled = true;
    }

    if (action != null) {
      action.setEnabled(enabled);
    }
  }

  private void handleEditorSelection() {
    container = null;
    IWorkbenchPart part =
        workbenchPart != null ? workbenchPart : windowPart.getActivePage().getActivePart();

    if (part instanceof TextEditor) {
      IFile file = ResourceUtil.getFile(((EditorPart) part).getEditorInput());

      if (file != null) {
        container = file.getProject();
      }
    }
  }

  private void handleStructuredSelection(ISelection selection) {
    IStructuredSelection sel = (IStructuredSelection) selection;
    Object obj = sel.getFirstElement();

    if (obj instanceof ICElement) {
      if (obj instanceof ICContainer || obj instanceof ICProject) {
        container = (IContainer) ((ICElement) obj).getUnderlyingResource();
      } else {
        obj = ((ICElement) obj).getResource();
        if (obj != null) {
          container = ((IResource) obj).getParent();
        }
      }
    } else if (obj instanceof IResource) {
      if (obj instanceof IContainer) {
        container = (IContainer) obj;
      } else {
        container = ((IResource) obj).getParent();
      }
    } else if (obj instanceof SConsBuildTarget) {
      container = ((SConsBuildTarget) obj).getContainer();
    } else {
      container = null;
    }
  }

  boolean isEnabled() {
    return enabled;
  }
}
