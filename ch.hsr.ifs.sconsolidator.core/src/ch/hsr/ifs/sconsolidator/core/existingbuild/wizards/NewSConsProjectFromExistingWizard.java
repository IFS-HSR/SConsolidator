package ch.hsr.ifs.sconsolidator.core.existingbuild.wizards;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.cdt.core.CCProjectNature;
import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.actions.WorkspaceModifyOperation;

import ch.hsr.ifs.sconsolidator.core.SConsI18N;
import ch.hsr.ifs.sconsolidator.core.SConsPlugin;
import ch.hsr.ifs.sconsolidator.core.existingbuild.SConsExistingProjectHandler;

public class NewSConsProjectFromExistingWizard extends Wizard implements IImportWizard, INewWizard {
  private NewSConsProjectFromExistingPage page;

  @Override
  public void init(IWorkbench workbench, IStructuredSelection selection) {
    setWindowTitle(SConsI18N.NewSConsProjectFromExistingWizard_WindowTitle);
  }

  @Override
  public void addPages() {
    page = new NewSConsProjectFromExistingPage();
    addPage(page);
  }

  @Override
  public boolean performFinish() {
    try {
      getContainer().run(true, true, createBuildInfoCollectorJob());
    } catch (InvocationTargetException e) {
      SConsPlugin.showExceptionInDisplayThread(
          SConsI18N.NewSConsProjectFromExistingWizard_ImportFailedTitle,
          SConsI18N.NewSConsProjectFromExistingWizard_ImportFailedMessage, e.getTargetException());
      return false;
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
    return true;
  }

  private IRunnableWithProgress createBuildInfoCollectorJob() {
    final String projectName = page.getProjectName();
    final boolean isCPP = page.isCPP();
    final String additionalSConsOptions = page.getAdditionalSConsOptions();
    final String location = page.getLocation();

    IRunnableWithProgress extractJob = new WorkspaceModifyOperation() {
      @Override
      protected void execute(IProgressMonitor pm) throws CoreException, InvocationTargetException,
          InterruptedException {
        pm.beginTask(SConsI18N.NewSConsProjectFromExistingWizard_StartConvertingMsg, 5);

        try {
          IWorkspace workspace = ResourcesPlugin.getWorkspace();
          IProject project = getProject(projectName, workspace);
          IProjectDescription description = createNewProjectDesc(projectName, workspace);
          setLocation(projectName, workspace, description, location);
          createCDTProject(project, description, isCPP, pm);
          configureProject(project, additionalSConsOptions, pm);
        } catch (Exception e) {
          report(e);
        } finally {
          pm.done();
        }
      }
    };
    return extractJob;
  }

  private void report(Exception e) throws CoreException {
    SConsPlugin.log(e);
    IStatus status =
        new Status(
            IStatus.ERROR,
            SConsPlugin.PLUGIN_ID,
            0,
            SConsI18N.NewSConsProjectFromExistingWizard_ExtractionOfExistingProjectInformationFailed,
            e.getCause());
    throw new CoreException(status);
  }

  private IProject getProject(String projectName, IWorkspace workspace) {
    return workspace.getRoot().getProject(projectName);
  }

  private IProjectDescription createNewProjectDesc(String projectName, IWorkspace workspace) {
    return workspace.newProjectDescription(projectName);
  }

  private void configureProject(IProject project, String additionalSConsOptions, IProgressMonitor pm)
      throws CoreException {
    SConsExistingProjectHandler projectHandler =
        new SConsExistingProjectHandler(project, SubMonitor.convert(pm, 4));
    projectHandler.configureProject(additionalSConsOptions);
  }

  private void createCDTProject(IProject project, IProjectDescription description, boolean isCPP,
      IProgressMonitor pm) throws CoreException {
    SubMonitor subMonitor = SubMonitor.convert(pm, 1);
    CCorePlugin.getDefault().createCDTProject(description, project, subMonitor);

    if (isCPP) {
      CCProjectNature.addCCNature(project, subMonitor);
    }
  }

  private void setLocation(String projectName, IWorkspace workspace,
      IProjectDescription description, String location) {
    IPath defaultLocation = workspace.getRoot().getLocation().append(projectName);
    Path locationPath = new Path(location);

    if (!locationPath.isEmpty() && !locationPath.equals(defaultLocation)) {
      description.setLocation(locationPath);
    }
  }

  @Override
  public boolean needsProgressMonitor() {
    // extracting all project information can take a while
    // therefore we show progress with a progress bar in the wizard
    return true;
  }
}
