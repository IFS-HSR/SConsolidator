package ch.hsr.ifs.sconsolidator.core.existingbuild.wizards;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;

import ch.hsr.ifs.sconsolidator.core.SConsI18N;
import ch.hsr.ifs.sconsolidator.core.SConsImages;
import ch.hsr.ifs.sconsolidator.core.SConsPlugin;
import ch.hsr.ifs.sconsolidator.core.preferences.PreferenceConstants;

@SuppressWarnings("restriction")
class NewSConsProjectFromExistingPage extends WizardPage {
  private static String previouslyBrowsedDirectory = "";
  private final IWorkspaceRoot root;
  private Text projectName;
  private Text additionalSConsOptions;
  private Text location;
  private Button cButton;
  private Button cppButton;

  public NewSConsProjectFromExistingPage() {
    super(SConsI18N.NewSConsProjectFromExistingPage_PageName,
        SConsI18N.NewSConsProjectFromExistingPage_PageTitle, SConsImages
            .getImageDescriptor(SConsImages.WIZARD));
    setDescription(SConsI18N.NewSConsProjectFromExistingPage_PageDescription);
    root = ResourcesPlugin.getWorkspace().getRoot();
  }

  @Override
  public void createControl(Composite parent) {
    Composite comp = getCompositeFor(parent);
    addProjectNameSelector(comp);
    addSourceSelector(comp);
    addLanguageSelector(comp);
    addAdditionalSConsOptions(comp);
    setControl(comp);
    setPageComplete(false);
  }

  private Composite getCompositeFor(Composite parent) {
    Composite comp = new Composite(parent, SWT.NONE);
    GridLayout layout = new GridLayout();
    comp.setLayout(layout);
    comp.setLayoutData(getTextFieldLayout());
    return comp;
  }

  private void addProjectNameSelector(Composite parent) {
    Group group =
        createNewGroup(parent, SConsI18N.NewSConsProjectFromExistingPage_ProjectNameText, false);
    projectName = createNewTextField(group);
    projectName.addModifyListener(new ModifyListener() {
      @Override
      public void modifyText(ModifyEvent e) {
        validateProjectName();
      }
    });
  }

  private Group createNewGroup(Composite parent, String title, boolean fillVerticalSpace) {
    Group group = new Group(parent, SWT.NONE);
    GridLayout layout = new GridLayout();
    layout.numColumns = 2;
    group.setLayout(layout);
    group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, fillVerticalSpace));
    group.setText(title);
    return group;
  }

  private void validateProjectName() {
    if (!isValidProjectName()) {
      setErrorMessage(SConsI18N.NewSConsProjectFromExistingPage_ProjectAlreadyExistsMsg);
      setPageComplete(false);
    } else {
      setErrorMessage(null);
    }
    togglePageComplete();
  }

  private void addSourceSelector(Composite parent) {
    Group group =
        createNewGroup(parent, SConsI18N.NewSConsProjectFromExistingPage_ExistingCodeLocationText,
            false);
    createLocationField(group);
    validateSource();
    createBrowseButton(group);
  }

  private void createBrowseButton(Group group) {
    Button browse = new Button(group, SWT.NONE);
    browse.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));
    browse.setText(SConsI18N.NewSConsProjectFromExistingPage_BrowseButtonTitle);
    browse.addSelectionListener(new SelectionListener() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        chooseLocation();
      }

      @Override
      public void widgetDefaultSelected(SelectionEvent e) {}
    });
  }

  private void createLocationField(Group group) {
    location = createNewTextField(group);
    location.addModifyListener(new ModifyListener() {
      @Override
      public void modifyText(ModifyEvent e) {
        validateSource();
      }
    });
  }

  private void chooseLocation() {
    String dirName = getLocation();

    if (dirName.length() == 0) {
      dirName = previouslyBrowsedDirectory;
    }

    DirectoryDialog dialog = new DirectoryDialog(location.getShell());
    dialog.setMessage(SConsI18N.NewSConsProjectFromExistingPage_DialogHelpCodeBrowseMessage);

    if (dirName.length() == 0) {
      dialog.setFilterPath(IDEWorkbenchPlugin.getPluginWorkspace().getRoot().getLocation()
          .toOSString());
    } else {
      if (new File(dirName).exists()) {
        dialog.setFilterPath(new Path(dirName).toOSString());
      }
    }

    String selectedDirectory = dialog.open();

    if (selectedDirectory != null) {
      previouslyBrowsedDirectory = selectedDirectory;
      location.setText(selectedDirectory);
    }
  }

  private boolean isValidSourceDir() {
    File dir = getChosenSourceLocation();
    return dir.isDirectory() && dir.exists();
  }

  private File getChosenSourceLocation() {
    return new File(location.getText());
  }

  private void validateSource() {
    if (!isValidSourceDir()) {
      setPageComplete(false);
      setErrorMessage(SConsI18N.NewSConsProjectFromExistingPage_NotAValidDirectoryMsg);
      return;
    }

    setErrorMessage(null);
    File[] contents = getChosenSourceLocation().listFiles();

    if (contents == null)
      return;

    String dotProject = IProjectDescription.DESCRIPTION_FILE_NAME;

    for (File file : contents) {
      if (file.isFile() && file.getName().equals(dotProject)) {
        ProjectRecord projectRecord = new ProjectRecord(file);
        projectName.setText(projectRecord.getProjectName());
        return;
      }
    }

    projectName.setText(getChosenSourceLocation().getName());
    togglePageComplete();
  }

  private void togglePageComplete() {
    setPageComplete(isValidSourceDir() && isValidProjectName());
  }

  private boolean isValidProjectName() {
    String name = projectName.getText().trim();
    if (name.isEmpty())
      return false;
    IProject project = root.getProject(name);
    return !project.exists();
  }

  private void addLanguageSelector(Composite parent) {
    Group group =
        createNewGroup(parent, SConsI18N.NewSConsProjectFromExistingPage_LnaguagesSelection, false);
    createCButton(group);
    createCppButton(group);
  }

  private void createCppButton(Group group) {
    cppButton = new Button(group, SWT.CHECK);
    cppButton.setText("C++");
    cppButton.setSelection(true);
  }

  private void createCButton(Group group) {
    cButton = new Button(group, SWT.CHECK);
    cButton.setText("C");
    cButton.setSelection(true);
  }

  private void addAdditionalSConsOptions(Composite parent) {
    Group group =
        createNewGroup(parent, SConsI18N.NewSConsProjectFromExistingPage_AdditionalSConsOptions,
            true);
    additionalSConsOptions = createNewMultiLineTextField(group);
    additionalSConsOptions.setText(SConsPlugin.getWorkspacePreferenceStore().getString(
        PreferenceConstants.ADDITIONAL_COMMANDLINE_OPTIONS));
  }

  private Text createNewMultiLineTextField(Composite parent) {
    Text textField = new Text(parent, SWT.MULTI | SWT.V_SCROLL | SWT.BORDER | SWT.WRAP);
    textField.setLayoutData(getTextFieldLayout());
    return textField;
  }

  private GridData getTextFieldLayout() {
    return new GridData(SWT.FILL, SWT.FILL, true, true);
  }

  private Text createNewTextField(Composite parent) {
    Text textField = new Text(parent, SWT.BORDER);
    textField.setLayoutData(getTextFieldLayout());
    return textField;
  }

  String getProjectName() {
    return projectName.getText().trim();
  }

  String getLocation() {
    return location.getText().trim();
  }

  String getAdditionalSConsOptions() {
    return additionalSConsOptions.getText().trim();
  }

  boolean isCPP() {
    return cppButton.getSelection();
  }

  private static final class ProjectRecord {
    private final File projectSystemFile;
    private String projectName;
    private IProjectDescription description;

    private ProjectRecord(File file) {
      projectSystemFile = file;
      setProjectName();
    }

    private void setProjectName() {
      try {
        IPath path = new Path(projectSystemFile.getPath());

        if (isDefaultLocation(path)) {
          projectName = path.segment(path.segmentCount() - 2);
          description = IDEWorkbenchPlugin.getPluginWorkspace().newProjectDescription(projectName);
        } else {
          description = IDEWorkbenchPlugin.getPluginWorkspace().loadProjectDescription(path);
          projectName = description.getName();
        }
      } catch (CoreException e) {
        SConsPlugin.showExceptionInDisplayThread(e);
      }
    }

    private boolean isDefaultLocation(IPath path) {
      if (path.segmentCount() < 2)
        return false;
      return path.removeLastSegments(2).toFile().equals(Platform.getLocation().toFile());
    }

    private String getProjectName() {
      return projectName;
    }
  }
}
