package ch.hsr.ifs.sconsolidator.core.preferences.pages;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.swt.widgets.Composite;

public class DependentBooleanFieldEditor extends BooleanFieldEditor {
  private final BooleanFieldEditor dependencyFieldEditor;

  public DependentBooleanFieldEditor(BooleanFieldEditor dependencyFieldEditor) {
    this.dependencyFieldEditor = dependencyFieldEditor;
  }

  public DependentBooleanFieldEditor(BooleanFieldEditor dependencyFieldEditor, String name,
      String label, Composite parent) {
    super(name, label, parent);
    this.dependencyFieldEditor = dependencyFieldEditor;
  }

  public DependentBooleanFieldEditor(BooleanFieldEditor dependencyFieldEditor, String name,
      String labelText, int style, Composite parent) {
    super(name, labelText, style, parent);
    this.dependencyFieldEditor = dependencyFieldEditor;
  }

  @Override
  public void setEnabled(boolean enabled, Composite parent) {
    super.setEnabled(enabled ? !dependencyFieldEditor.getBooleanValue() : enabled, parent);
  }
}
