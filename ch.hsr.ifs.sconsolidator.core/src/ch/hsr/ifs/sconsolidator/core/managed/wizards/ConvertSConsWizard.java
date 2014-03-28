package ch.hsr.ifs.sconsolidator.core.managed.wizards;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;

import ch.hsr.ifs.sconsolidator.core.SConsI18N;
import ch.hsr.ifs.sconsolidator.core.SConsImages;
import ch.hsr.ifs.sconsolidator.core.SConsPlugin;
import ch.hsr.ifs.sconsolidator.core.managed.SConsManagedProjectHandler;

public class ConvertSConsWizard extends Wizard implements INewWizard {
  private final Image wizardImage;
  private ConvertSConsWizardPage convertPage;
  private IStructuredSelection initialSelection;

  public ConvertSConsWizard() {
    wizardImage = SConsImages.getImageDescriptor(SConsImages.WIZARD).createImage();
  }

  @Override
  public void init(IWorkbench workbench, IStructuredSelection selection) {
    this.initialSelection = selection;
  }

  @Override
  public void addPages() {
    setWindowTitle(SConsI18N.ConvertSConsWizardPage_Title);
    convertPage = new ConvertSConsWizardPage();
    addPage(convertPage);
  }

  @Override
  public Image getDefaultPageImage() {
    return wizardImage;
  }

  @Override
  public void dispose() {
    super.dispose();
    wizardImage.dispose();
  }

  @Override
  public boolean performFinish() {
    if (convertPage == null && !isProjectSelected())
      return false;

    try {
      executeConversion(getConvertableProjects());
    } catch (InvocationTargetException e) {
      SConsPlugin.log(e);
      MessageDialog.openError(getShell(), SConsI18N.ConvertSConsWizard_ErrorTitle, e
          .getTargetException().getMessage());
      return false;
    } catch (InterruptedException e) {
      // User cancelled, so stop but don't close wizard
      return false;
    }
    return true;
  }

  private void executeConversion(final IProject[] selectedProjects)
      throws InvocationTargetException, InterruptedException {
    IProgressService progressService = PlatformUI.getWorkbench().getProgressService();
    progressService.busyCursorWhile(new IRunnableWithProgress() {
      @Override
      public void run(IProgressMonitor pm) throws InvocationTargetException, InterruptedException {
        try {
          performOperation(selectedProjects, pm);
        } catch (CoreException e) {
          throw new InvocationTargetException(e);
        }
      }
    });
  }

  private IProject[] getConvertableProjects() {
    IProject[] convertableProjects;

    if (convertPage == null && isProjectSelected()) {
      // User clicked finish before choosing projects
      convertableProjects = new IProject[] {toProject(initialSelection.getFirstElement())};
    } else {
      Object[] checkedElements = convertPage.getCheckedElements();
      convertableProjects = new IProject[checkedElements.length];

      for (int i = 0; i < checkedElements.length; i++) {
        convertableProjects[i] = toProject(checkedElements[i]);
      }
    }

    return convertableProjects;
  }

  private IProject toProject(Object obj) {
    if (obj instanceof IAdaptable) {
      IProject project = (IProject) ((IAdaptable) obj).getAdapter(IProject.class);
      if (project != null)
        return project;
    }
    throw new IllegalArgumentException("Not a project instance given");
  }

  private boolean isProjectSelected() {
    return initialSelection.getFirstElement() instanceof IProject;
  }

  private void performOperation(IProject[] selectedProjects, IProgressMonitor pm)
      throws CoreException {
    pm.beginTask(SConsI18N.ConvertSConsWizard_ConvertingStartMessage, selectedProjects.length);

    try {
      for (IProject p : selectedProjects) {
        pm.subTask(NLS.bind(SConsI18N.ConvertSConsWizard_ConvertingProjectMessage, p.getName()));

        if (pm.isCanceled()) {
          break;
        }
        configureProject(p, pm);
      }
    } finally {
      pm.done();
    }
  }

  private void configureProject(IProject project, IProgressMonitor monitor) throws CoreException {
    new SConsManagedProjectHandler(project, monitor).configureProject();
  }
}
