package ch.hsr.ifs.sconsolidator.core.targets;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import ch.hsr.ifs.sconsolidator.core.SConsBuilder;
import ch.hsr.ifs.sconsolidator.core.SConsI18N;
import ch.hsr.ifs.sconsolidator.core.SConsPlugin;
import ch.hsr.ifs.sconsolidator.core.targets.model.SConsBuildTarget;
import ch.hsr.ifs.sconsolidator.core.targets.model.SConsBuildTargetManager;

public class SConsTargetDialog extends Dialog {
  private final SConsBuildTargetManager targetManager;
  private final IContainer container;
  private String additonalCommandLineArgs;
  private String targetName;
  private SConsBuildTarget target;
  private DialogInformationLine statusLine;
  private Text targetNameText;
  private Text targetDescriptionText;
  private Text additionalCommandLineArgsText;
  private Button sameAsNameCheckBox;
  private boolean initializing;
  private String targetBuildID;
  private String targetDescription;

  public static SConsTargetDialog fromNewTarget(Shell parentShell, IContainer container) {
    return new SConsTargetDialog(parentShell, container);
  }

  public static SConsTargetDialog fromExistingTarget(Shell parentShell, SConsBuildTarget target) {
    return new SConsTargetDialog(parentShell, target);
  }

  private SConsTargetDialog(Shell parentShell, IContainer container) {
    super(parentShell);
    this.container = container;
    targetManager = SConsPlugin.getDefault().getSConsTargetManager();
    targetName = "";
    targetBuildID = SConsBuilder.BUILDER_ID;
    targetDescription = "";
    additonalCommandLineArgs = "";
    setShellStyle(getShellStyle() | SWT.RESIZE);
  }

  private SConsTargetDialog(Shell parentShell, SConsBuildTarget target) {
    this(parentShell, target.getContainer());
    this.target = target;
    targetName = target.getTargetName();
    targetBuildID = target.getTargetBuilderID();
    targetDescription = target.getDescription();
    additonalCommandLineArgs = target.getAdditionalCmdLineArgs();
  }

  @Override
  protected void configureShell(Shell newShell) {
    newShell.setText(getTitle());
    super.configureShell(newShell);
  }

  private String getTitle() {
    return target == null ? SConsI18N.SConsTargetDialog_CreateTargetTitle
        : SConsI18N.SConsTargetDialog_ModifyTargetTitle;
  }

  @Override
  protected Control createDialogArea(Composite parent) {
    parent = (Composite) super.createDialogArea(parent);
    initializeDialogUnits(parent);
    createNameControl(parent);
    createTargetControl(parent);
    createAdditionalBuildArgsControl(parent);
    createStatusLine(parent);
    initializing = false;
    return parent;
  }

  private void createStatusLine(Composite parent) {
    statusLine = new DialogInformationLine(parent);
    GridData gd = new GridData(GridData.FILL_HORIZONTAL);
    gd.widthHint = convertWidthInCharsToPixels(50);
    statusLine.setLayoutData(gd);
  }

  private void createNameControl(Composite parent) {
    Composite composite = createComposite(parent, 2);
    ((GridLayout) composite.getLayout()).makeColumnsEqualWidth = false;
    GridData gd = new GridData(GridData.FILL_HORIZONTAL);
    gd.widthHint = convertWidthInCharsToPixels(50);
    composite.setLayoutData(gd);
    Label label = createLabel(composite, SConsI18N.SConsTargetDialog_TargetNameLabel);
    ((GridData) (label.getLayoutData())).horizontalAlignment = GridData.BEGINNING;
    ((GridData) (label.getLayoutData())).grabExcessHorizontalSpace = false;
    targetNameText = createTextField(composite, SWT.SINGLE | SWT.BORDER);
    ((GridData) (targetNameText.getLayoutData())).horizontalAlignment = GridData.FILL;
    ((GridData) (targetNameText.getLayoutData())).grabExcessHorizontalSpace = true;
    targetNameText.addListener(SWT.Modify, new UpdateStatusLineListener());
  }

  private void createTargetControl(Composite parent) {
    Group group = createGroup(parent, SConsI18N.SConsTargetDialog_SConsTargetGroup, 1);
    GridLayout layout = new GridLayout(2, false);
    group.setLayout(layout);
    GridData gd = new GridData(GridData.FILL_HORIZONTAL);
    gd.widthHint = convertWidthInCharsToPixels(50);
    group.setLayoutData(gd);

    sameAsNameCheckBox = new Button(group, SWT.CHECK);
    gd = new GridData();
    gd.horizontalSpan = 2;
    sameAsNameCheckBox.setLayoutData(gd);
    sameAsNameCheckBox.setText(SConsI18N.SConsTargetDialog_SameAsTargetName);

    targetNameText.addModifyListener(new ModifyListener() {
      @Override
      public void modifyText(ModifyEvent e) {
        if (sameAsNameCheckBox.getSelection()) {
          targetDescriptionText.setText(targetNameText.getText());
        }
      }
    });

    Label label = createLabel(group, SConsI18N.SConsTargetDialog_SConsTargetTextField);
    ((GridData) (label.getLayoutData())).horizontalAlignment = GridData.BEGINNING;
    ((GridData) (label.getLayoutData())).grabExcessHorizontalSpace = false;
    targetDescriptionText = createTextField(group, SWT.SINGLE | SWT.BORDER);
    ((GridData) (targetDescriptionText.getLayoutData())).horizontalAlignment = GridData.FILL;
    ((GridData) (targetDescriptionText.getLayoutData())).grabExcessHorizontalSpace = true;
    targetDescriptionText.setText(targetDescription);
    targetDescriptionText.addListener(SWT.Modify, new Listener() {
      @Override
      public void handleEvent(Event e) {
        updateButtons();
      }
    });

    sameAsNameCheckBox.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        sameAsNameSelected();
      }
    });

    sameAsNameCheckBox.setSelection(targetDescription.equals(targetName)
        || (targetDescription.length() == 0 && targetName == null));
    sameAsNameSelected();
  }

  private void sameAsNameSelected() {
    targetDescriptionText.setEnabled(!sameAsNameCheckBox.getSelection());

    if (sameAsNameCheckBox.getSelection()) {
      targetDescriptionText.setText(targetNameText.getText());
    }
  }

  private Composite createComposite(Composite parent, int numColumns) {
    Composite composite = new Composite(parent, SWT.NULL);
    composite.setFont(parent.getFont());
    composite.setLayout(new GridLayout(numColumns, true));
    composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    return composite;
  }

  private Text createTextField(Composite parent, int style) {
    Text text = new Text(parent, style);
    GridData data = new GridData();
    data.horizontalAlignment = GridData.FILL;
    data.grabExcessHorizontalSpace = true;
    data.verticalAlignment = GridData.CENTER;
    data.grabExcessVerticalSpace = false;
    text.setLayoutData(data);
    return text;
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
    return label;
  }

  private Group createGroup(Composite parent, String label, int nColumns) {
    Group group = new Group(parent, SWT.NONE);
    group.setFont(parent.getFont());
    group.setText(label);
    GridLayout layout = new GridLayout();
    layout.numColumns = nColumns;
    group.setLayout(layout);
    group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    return group;
  }

  private void createAdditionalBuildArgsControl(Composite parent) {
    Group group =
        createGroup(parent, SConsI18N.SConsTargetDialog_AdditionalCommandLineGroupLabel, 1);
    GridLayout layout = new GridLayout(2, false);
    group.setLayout(layout);
    GridData gd = new GridData(GridData.FILL_HORIZONTAL);
    gd.widthHint = convertWidthInCharsToPixels(50);
    group.setLayoutData(gd);

    Label label = createLabel(group, SConsI18N.SConsTargetDialog_AdditionalCommandLineArgsLabel);
    ((GridData) (label.getLayoutData())).horizontalAlignment = GridData.BEGINNING;
    ((GridData) (label.getLayoutData())).grabExcessHorizontalSpace = false;
    additionalCommandLineArgsText = createTextField(group, SWT.SINGLE | SWT.BORDER);
    ((GridData) (additionalCommandLineArgsText.getLayoutData())).horizontalAlignment =
        GridData.FILL;
    ((GridData) (additionalCommandLineArgsText.getLayoutData())).grabExcessHorizontalSpace = true;
    additionalCommandLineArgsText.setText(additonalCommandLineArgs);
    additionalCommandLineArgsText.addListener(SWT.Modify, new Listener() {
      @Override
      public void handleEvent(Event e) {
        updateButtons();
      }
    });
  }

  @Override
  protected void createButtonsForButtonBar(Composite parent) {
    if (target == null || SConsPlugin.getDefault().getSConsTargetManager().targetExists(target)) {
      createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
    }
    createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
    targetNameText.setFocus();
    targetNameText.setText(targetName != null ? targetName : "");
    targetNameText.selectAll();
  }

  private void updateButtons() {
    if (getButton(IDialogConstants.OK_ID) != null) {
      getButton(IDialogConstants.OK_ID).setEnabled(
          targetHasChanged() && !statusLine.hasErrorMessage());
    }
  }

  private boolean targetHasChanged() {
    if (initializing || target == null
        || !SConsPlugin.getDefault().getSConsTargetManager().targetExists(target))
      return true;

    if (!targetName.equals(getTargetName()) || !targetDescription.equals(getTargetDescription())
        || !additonalCommandLineArgs.equals(getAdditionalCommandLineArgs()))
      return true;
    return false;
  }

  private String getTargetDescription() {
    return targetDescriptionText.getText().trim();
  }

  private String getTargetName() {
    return targetNameText.getText().trim();
  }

  private String getAdditionalCommandLineArgs() {
    return additionalCommandLineArgsText.getText().trim();
  }

  @Override
  protected void okPressed() {
    try {
      String name = getTargetName();
      String targetDesc = getTargetDescription();
      String addCmdLineArgs = getAdditionalCommandLineArgs();

      if (target == null) {
        target =
            targetManager.createTarget(container.getProject(), name, targetBuildID, targetDesc,
                addCmdLineArgs);
      } else {
        target.setAdditionalCmdLineArgs(addCmdLineArgs);
        target.setTargetName(name);
        target.setDescription(targetDesc);

        if (!target.getTargetName().equals(getTargetName())) {
          targetManager.renameTarget(target, name);
        } else {
          targetManager.updateTarget(target);
        }
      }

      if (target == null || !targetExists(target)) {
        targetManager.addTarget(container, target);
      }
    } catch (CoreException e) {
      SConsPlugin.showExceptionInDisplayThread(SConsI18N.SConsTargetDialog_ErrorAddingTargetTitle,
          SConsI18N.SConsTargetDialog_ErrorAddingTargetMsg, e);
    }
    super.okPressed();
  }

  private boolean targetExists(SConsBuildTarget target) {
    return SConsPlugin.getDefault().getSConsTargetManager().targetExists(target);
  }

  private class UpdateStatusLineListener implements Listener {
    @Override
    public void handleEvent(Event e) {
      setStatusLine();
      updateButtons();
    }

    private void setStatusLine() {
      statusLine.setErrorMessage(null);
      String newTargetName = targetNameText.getText().trim();

      if (newTargetName.isEmpty()) {
        statusLine.setErrorMessage(SConsI18N.SConsTargetDialog_TargetNameMissing);
      }
    }
  }
}
