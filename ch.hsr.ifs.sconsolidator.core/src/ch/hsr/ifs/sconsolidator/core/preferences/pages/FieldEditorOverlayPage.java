/*******************************************************************************
 * Adapted from http://www.eclipse.org/articles/Article-Mutatis-mutandis/overlay-pages.html
 *******************************************************************************/
package ch.hsr.ifs.sconsolidator.core.preferences.pages;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.cdt.ui.newui.MultiLineTextFieldEditor;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceNode;
import org.eclipse.jface.preference.IPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbenchPropertyPage;

import ch.hsr.ifs.sconsolidator.core.SConsI18N;
import ch.hsr.ifs.sconsolidator.core.SConsPlugin;
import ch.hsr.ifs.sconsolidator.core.preferences.PreferenceConstants;

public abstract class FieldEditorOverlayPage extends FieldEditorPreferencePage implements
    IWorkbenchPropertyPage {
  private final boolean isWorkspacePreferenceAvailable;
  private final Map<FieldEditor, Composite> editors;
  private IAdaptable element;
  private Button useWorkspaceSettingsButton;
  private Button useProjectSettingsButton;
  private Button configureButton;
  private IPreferenceStore overlayStore;
  private String pageId;

  public FieldEditorOverlayPage(int style, boolean isWorkspacePreferenceAvailable) {
    super(style);
    this.isWorkspacePreferenceAvailable = isWorkspacePreferenceAvailable;
    this.editors = new HashMap<FieldEditor, Composite>();
  }

  protected abstract String getPageId();

  @Override
  public void setElement(IAdaptable element) {
    this.element = element;
  }

  @Override
  public IAdaptable getElement() {
    return element;
  }

  public IProject getProject() {
    return (IProject) element.getAdapter(IProject.class);
  }

  public boolean isPropertyPage() {
    return getElement() != null;
  }

  protected void addField(FieldEditor editor, Composite parent) {
    editors.put(editor, parent);
    super.addField(editor);
  }

  @Override
  protected void addField(FieldEditor editor) {
    addField(editor, getFieldEditorParent());
  }

  @Override
  public void createControl(Composite parent) {
    if (isPropertyPage()) {
      pageId = getPageId();
      overlayStore = SConsPlugin.getProjectPreferenceStore(getProject());
    }

    super.createControl(parent);

    if (isPropertyPage() && isWorkspacePreferenceAvailable) {
      updateFieldEditors();
    }
  }

  @Override
  protected Control createContents(Composite parent) {
    if (isPropertyPage() && isWorkspacePreferenceAvailable) {
      createSelectionGroup(parent);
    }
    return super.createContents(parent);
  }

  private void createSelectionGroup(Composite parent) {
    Composite comp = new Composite(parent, SWT.NONE);
    GridLayout layout = new GridLayout(2, false);
    layout.marginHeight = 0;
    layout.marginWidth = 0;
    comp.setLayout(layout);
    comp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    Composite radioGroup = new Composite(comp, SWT.NONE);
    radioGroup.setLayout(new GridLayout());
    radioGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    useWorkspaceSettingsButton =
        createRadioButton(radioGroup, SConsI18N.OverlayPage_UseWorkspaceSettings);
    useProjectSettingsButton =
        createRadioButton(radioGroup, SConsI18N.OverlayPage_UseProjectSettings);
    configureButton = new Button(comp, SWT.PUSH);
    configureButton.setText(SConsI18N.OverlayPage_ConfigureWorkspaceSettings);
    configureButton.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        configureWorkspaceSettings();
      }
    });

    try {
      if (useWorkspace()) {
        useWorkspaceSettingsButton.setSelection(true);
      } else {
        useProjectSettingsButton.setSelection(true);
        configureButton.setEnabled(false);
      }
    } catch (Exception e) {
      useWorkspaceSettingsButton.setSelection(true);
    }
  }

  private Boolean useWorkspace() {
    return getPreferenceStore().getBoolean(getPageId() + PreferenceConstants.USE_PARENT_SUFFIX);
  }

  private Button createRadioButton(Composite parent, String label) {
    final Button button = new Button(parent, SWT.RADIO);
    button.setText(label);
    button.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        configureButton.setEnabled(button.equals(useWorkspaceSettingsButton));
        updateFieldEditors();
      }
    });
    return button;
  }

  @Override
  public IPreferenceStore getPreferenceStore() {
    if (isPropertyPage())
      return overlayStore;
    return super.getPreferenceStore();
  }

  private void updateFieldEditors() {
    boolean enabled = useProjectSettingsButton.getSelection();

    for (Map.Entry<FieldEditor, Composite> entry : editors.entrySet()) {
      if (entry.getKey() instanceof MultiLineTextFieldEditor) {
        ((MultiLineTextFieldEditor) entry.getKey()).getTextControl(entry.getValue()).setEnabled(
            enabled);
      } else {
        entry.getKey().setEnabled(enabled, entry.getValue());
      }
    }
  }

  @Override
  public boolean performOk() {
    boolean result = super.performOk();

    if (result && isPropertyPage() && isWorkspacePreferenceAvailable) {
      getPreferenceStore().setValue(getPageId() + PreferenceConstants.USE_PARENT_SUFFIX,
          !useProjectSettingsButton.getSelection());
    }

    return result;
  }

  @Override
  protected void performDefaults() {
    if (isPropertyPage() && isWorkspacePreferenceAvailable) {
      useWorkspaceSettingsButton.setSelection(true);
      useProjectSettingsButton.setSelection(false);
      configureButton.setEnabled(true);
      updateFieldEditors();
    }
    super.performDefaults();
  }

  private void configureWorkspaceSettings() {
    try {
      IPreferencePage page = this.getClass().newInstance();
      page.setTitle(getTitle());
      showPreferencePage(pageId, page);
    } catch (Exception e) {
      SConsPlugin.log(e);
    }
  }

  private void showPreferencePage(String id, IPreferencePage page) {
    final IPreferenceNode targetNode = new PreferenceNode(id, page);
    final PreferenceDialog dialog =
        new PreferenceDialog(getControl().getShell(), getManager(targetNode));
    BusyIndicator.showWhile(getControl().getDisplay(), new Runnable() {
      @Override
      public void run() {
        dialog.create();
        dialog.setMessage(targetNode.getLabelText());
        dialog.open();
      }
    });
  }

  private PreferenceManager getManager(IPreferenceNode targetNode) {
    PreferenceManager manager = new PreferenceManager();
    manager.addToRoot(targetNode);
    return manager;
  }
}
