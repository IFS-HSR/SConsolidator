package ch.hsr.ifs.sconsolidator.core.existingbuild.actions;

import java.util.Collection;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import ch.hsr.ifs.sconsolidator.core.SConsI18N;
import ch.hsr.ifs.sconsolidator.core.base.utils.UIUtil;
import ch.hsr.ifs.sconsolidator.core.existingbuild.AddExistingSConsSupportJob;
import ch.hsr.ifs.sconsolidator.core.preferences.SConsOptionHandler;

public class AddSConsSupportDialog extends Dialog {
  private Text additionalCmdLineArgs;
  private final Collection<IProject> projects;

  public AddSConsSupportDialog(Collection<IProject> projects) {
    super(UIUtil.getWindowShell());
    this.projects = projects;
    setShellStyle(getShellStyle() | SWT.RESIZE);
  }

  @Override
  protected void configureShell(Shell newShell) {
    newShell.setText(getTitle());
    super.configureShell(newShell);
  }

  private String getTitle() {
    return SConsI18N.SConsExtractInformationDialog_SettingsDialogTitle;
  }

  @Override
  protected Control createDialogArea(Composite parent) {
    Composite composite = (Composite) super.createDialogArea(parent);
    initializeDialogUnits(composite);
    createControls(composite);
    initializeDefaultValues();
    return composite;
  }

  private void initializeDefaultValues() {
    String workspaceAdditionalCmdLineOpts = SConsOptionHandler.getNormalizedCommandLineOpts();
    additionalCmdLineArgs.setText(workspaceAdditionalCmdLineOpts);
  }

  private void createControls(Composite parent) {
    Composite composite = createComposite(parent, 2);
    ((GridLayout) composite.getLayout()).makeColumnsEqualWidth = false;
    GridData gd = new GridData(GridData.FILL_HORIZONTAL);
    gd.widthHint = convertWidthInCharsToPixels(50);
    composite.setLayoutData(gd);
    createLabel(composite, SConsI18N.NewSConsProjectFromExistingPage_AdditionalSConsOptions);
    additionalCmdLineArgs =
        createTextField(composite, SWT.MULTI | SWT.V_SCROLL | SWT.BORDER | SWT.WRAP);
  }

  private Text createTextField(Composite parent, int style) {
    Text text = new Text(parent, style);
    GridData data = new GridData();
    data.horizontalAlignment = GridData.FILL;
    data.grabExcessHorizontalSpace = true;
    data.verticalAlignment = GridData.CENTER;
    data.grabExcessVerticalSpace = false;
    text.setLayoutData(data);
    ((GridData) (text.getLayoutData())).horizontalAlignment = GridData.FILL;
    ((GridData) (text.getLayoutData())).grabExcessHorizontalSpace = true;
    return text;
  }

  private Composite createComposite(Composite parent, int numColumns) {
    Composite composite = new Composite(parent, SWT.NULL);
    composite.setFont(parent.getFont());
    composite.setLayout(new GridLayout(numColumns, true));
    composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    return composite;
  }

  private Label createLabel(Composite parent, String text) {
    Label label = new Label(parent, SWT.LEFT);
    label.setFont(parent.getFont());
    label.setText(text);
    GridData gd = new GridData(GridData.FILL_HORIZONTAL);
    gd.horizontalSpan = 1;
    gd.widthHint = SWT.DEFAULT;
    gd.heightHint = SWT.DEFAULT;
    label.setLayoutData(gd);
    ((GridData) (label.getLayoutData())).horizontalAlignment = GridData.BEGINNING;
    ((GridData) (label.getLayoutData())).grabExcessHorizontalSpace = false;
    return label;
  }

  @Override
  protected void okPressed() {
    scheduleAddSConsSupportJob();
    super.okPressed();
  }

  private void scheduleAddSConsSupportJob() {
    new AddExistingSConsSupportJob(projects, getAdditionalCommandLineArgs()).schedule();
  }

  private String getAdditionalCommandLineArgs() {
    return additionalCmdLineArgs.getText().trim();
  }
}
