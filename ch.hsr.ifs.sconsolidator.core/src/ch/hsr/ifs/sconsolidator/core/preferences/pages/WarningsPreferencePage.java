package ch.hsr.ifs.sconsolidator.core.preferences.pages;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import ch.hsr.ifs.sconsolidator.core.SConsI18N;
import ch.hsr.ifs.sconsolidator.core.SConsPlugin;
import ch.hsr.ifs.sconsolidator.core.preferences.PreferenceConstants;

public class WarningsPreferencePage extends FieldEditorOverlayPage implements
    IWorkbenchPreferencePage {
  private List<BooleanFieldEditor> checkEditors;
  private Composite parent;

  public WarningsPreferencePage() {
    super(GRID, true);
    setPreferenceStore(SConsPlugin.getWorkspacePreferenceStore());
    setDescription(SConsI18N.WarningsPreferencePage_SConsWarningSettings);
  }

  @Override
  public void init(IWorkbench workbench) {}

  @Override
  protected String getPageId() {
    return PreferenceConstants.WARNINGS_PAGE_ID;
  }

  @Override
  protected void createFieldEditors() {
    parent = getFieldEditorParent();
    checkEditors = new LinkedList<BooleanFieldEditor>();

    BooleanFieldEditor allWarnings = getAllWarningsField();
    addField(allWarnings, parent);
    addCacheWriteErrorWarningField(allWarnings);
    addCorruptSConsignWarningField(allWarnings);
    addDependencyWarningField(allWarnings);
    addDeprecatedCopyWarningField(allWarnings);
    addDeprecatedSourceSignaturesWarningField(allWarnings);
    addDeprecatedTargetSignaturesWarningField(allWarnings);
    addDuplicateEnvWarningField(allWarnings);
    addFortranCppMixWarningField(allWarnings);
    addFutureDeprecatedWarningField(allWarnings);
    addLinkWarningsField(allWarnings);
    addMisleadingKeywordsWarningsField(allWarnings);
    addMissingSConscriptWarningsField(allWarnings);
    addNoMd5ModuleWarningsField(allWarnings);
    addNoMetaClassWarningsField(allWarnings);
    addNoObjectCountWarningsField(allWarnings);
    addNoParallelSupportWarningsField(allWarnings);
    addNoPythonVersionWarningsField(allWarnings);
    addReservedVariableWarningsField(allWarnings);
    addStackSizeWarningsField(allWarnings);
  }

  private void addStackSizeWarningsField(BooleanFieldEditor allWarnings) {
    BooleanFieldEditor stackSizeWarnings =
        new DependentBooleanFieldEditor(allWarnings, PreferenceConstants.STACK_SIZE_WARNINGS,
            SConsI18N.WarningsPreferencePage_StackSizeWarning, parent);
    addField(stackSizeWarnings, parent);
    checkEditors.add(stackSizeWarnings);
  }

  private void addReservedVariableWarningsField(BooleanFieldEditor allWarnings) {
    BooleanFieldEditor reservedVariableWarnings =
        new DependentBooleanFieldEditor(allWarnings,
            PreferenceConstants.RESERVED_VARIABLE_WARNINGS,
            SConsI18N.WarningsPreferencePage_ReservedVariableWarning, parent);
    addField(reservedVariableWarnings, parent);
    checkEditors.add(reservedVariableWarnings);
  }

  private void addNoPythonVersionWarningsField(BooleanFieldEditor allWarnings) {
    BooleanFieldEditor noPythonVersionWarnings =
        new DependentBooleanFieldEditor(allWarnings,
            PreferenceConstants.NO_PYTHON_VERSION_WARNINGS,
            SConsI18N.WarningsPreferencePage_NoPythonVersionWarning, parent);
    addField(noPythonVersionWarnings, parent);
    checkEditors.add(noPythonVersionWarnings);
  }

  private void addNoParallelSupportWarningsField(BooleanFieldEditor allWarnings) {
    BooleanFieldEditor noParallelSupportWarnings =
        new DependentBooleanFieldEditor(allWarnings,
            PreferenceConstants.NO_PARALLEL_SUPPORT_WARNINGS,
            SConsI18N.WarningsPreferencePage_NoParallelSupportWarning, parent);
    addField(noParallelSupportWarnings, parent);
    checkEditors.add(noParallelSupportWarnings);
  }

  private void addNoObjectCountWarningsField(BooleanFieldEditor allWarnings) {
    BooleanFieldEditor noObjectCountWarnings =
        new DependentBooleanFieldEditor(allWarnings, PreferenceConstants.NO_OBJECT_COUNT_WARNINGS,
            SConsI18N.WarningsPreferencePage_NoObjectCountWarning, parent);
    addField(noObjectCountWarnings, parent);
    checkEditors.add(noObjectCountWarnings);
  }

  private void addNoMetaClassWarningsField(BooleanFieldEditor allWarnings) {
    BooleanFieldEditor noMetaClassWarnings =
        new DependentBooleanFieldEditor(allWarnings, PreferenceConstants.NO_META_CLASS_WARNINGS,
            SConsI18N.WarningsPreferencePage_NoMetaClassWarning, parent);
    addField(noMetaClassWarnings, parent);
    checkEditors.add(noMetaClassWarnings);
  }

  private void addNoMd5ModuleWarningsField(BooleanFieldEditor allWarnings) {
    BooleanFieldEditor noMd5ModuleWarnings =
        new DependentBooleanFieldEditor(allWarnings, PreferenceConstants.NO_MD5_MODULE,
            SConsI18N.WarningsPreferencePage_NoMD5Warning, parent);
    addField(noMd5ModuleWarnings, parent);
    checkEditors.add(noMd5ModuleWarnings);
  }

  private void addMissingSConscriptWarningsField(BooleanFieldEditor allWarnings) {
    BooleanFieldEditor missingSConscriptWarnings =
        new DependentBooleanFieldEditor(allWarnings,
            PreferenceConstants.MISSING_SCONSCRIPT_WARNING,
            SConsI18N.WarningsPreferencePage_MissingSConscriptWarning, parent);
    addField(missingSConscriptWarnings, parent);
    checkEditors.add(missingSConscriptWarnings);
  }

  private void addMisleadingKeywordsWarningsField(BooleanFieldEditor allWarnings) {
    BooleanFieldEditor misleadingKeywordsWarnings =
        new DependentBooleanFieldEditor(allWarnings,
            PreferenceConstants.MISLEADING_KEYWORD_WARNING,
            SConsI18N.WarningsPreferencePage_MisleadingKeywordWarning, parent);
    addField(misleadingKeywordsWarnings, parent);
    checkEditors.add(misleadingKeywordsWarnings);
  }

  private void addLinkWarningsField(BooleanFieldEditor allWarnings) {
    BooleanFieldEditor linkWarnings =
        new DependentBooleanFieldEditor(allWarnings, PreferenceConstants.LINK_WARNING,
            SConsI18N.WarningsPreferencePage_LinkWarning, parent);
    addField(linkWarnings, parent);
    checkEditors.add(linkWarnings);
  }

  private void addFutureDeprecatedWarningField(BooleanFieldEditor allWarnings) {
    BooleanFieldEditor futureDeprecatedWarning =
        new DependentBooleanFieldEditor(allWarnings, PreferenceConstants.FUTURE_DEPRECATED_WARNING,
            SConsI18N.WarningsPreferencePage_FutureDeprecatedWarning, parent);
    addField(futureDeprecatedWarning, parent);
    checkEditors.add(futureDeprecatedWarning);
  }

  private void addFortranCppMixWarningField(BooleanFieldEditor allWarnings) {
    BooleanFieldEditor fortranCppMixWarning =
        new DependentBooleanFieldEditor(allWarnings, PreferenceConstants.FORTRAN_CPP_MIX_WARNING,
            SConsI18N.WarningsPreferencePage_FortranCppWarning, parent);
    addField(fortranCppMixWarning, parent);
    checkEditors.add(fortranCppMixWarning);
  }

  private void addDuplicateEnvWarningField(BooleanFieldEditor allWarnings) {
    BooleanFieldEditor duplicateEnvWarning =
        new DependentBooleanFieldEditor(allWarnings, PreferenceConstants.DUPLICATE_ENV_WARNING,
            SConsI18N.WarningsPreferencePage_DiplicateEnvWarning, parent);
    addField(duplicateEnvWarning, parent);
    checkEditors.add(duplicateEnvWarning);
  }

  private void addDeprecatedTargetSignaturesWarningField(BooleanFieldEditor allWarnings) {
    BooleanFieldEditor deprecatedTargetSignaturesWarning =
        new DependentBooleanFieldEditor(allWarnings,
            PreferenceConstants.DEPRECATED_TARGET_SIGNATURES,
            SConsI18N.WarningsPreferencePage_DeprecatedTargetSigWarning, parent);
    addField(deprecatedTargetSignaturesWarning, parent);
    checkEditors.add(deprecatedTargetSignaturesWarning);
  }

  private void addDeprecatedSourceSignaturesWarningField(BooleanFieldEditor allWarnings) {
    BooleanFieldEditor deprecatedSourceSignaturesWarning =
        new DependentBooleanFieldEditor(allWarnings,
            PreferenceConstants.DEPRECATED_SOURCE_SIGNATURES,
            SConsI18N.WarningsPreferencePage_DeprecatedSourceSigWarning, parent);
    addField(deprecatedSourceSignaturesWarning, parent);
    checkEditors.add(deprecatedSourceSignaturesWarning);
  }

  private void addDeprecatedCopyWarningField(BooleanFieldEditor allWarnings) {
    BooleanFieldEditor deprecatedCopyWarning =
        new DependentBooleanFieldEditor(allWarnings, PreferenceConstants.DEPRECATED_COPY_WARNING,
            SConsI18N.WarningsPreferencePage_DeprecatedCopyWarning, parent);
    addField(deprecatedCopyWarning, parent);
    checkEditors.add(deprecatedCopyWarning);
  }

  private void addDependencyWarningField(BooleanFieldEditor allWarnings) {
    BooleanFieldEditor dependencyWarning =
        new DependentBooleanFieldEditor(allWarnings, PreferenceConstants.DEPENDENCIES_WARNINGS,
            SConsI18N.WarningsPreferencePage_DependencyWarning, parent);
    addField(dependencyWarning, parent);
    checkEditors.add(dependencyWarning);
  }

  private void addCorruptSConsignWarningField(BooleanFieldEditor allWarnings) {
    BooleanFieldEditor corruptSConsignWarning =
        new DependentBooleanFieldEditor(allWarnings, PreferenceConstants.CORRUPT_SCONSIGN_WARNING,
            SConsI18N.WarningsPreferencePage_CorruptSConsignWarning, parent);
    addField(corruptSConsignWarning, parent);
    checkEditors.add(corruptSConsignWarning);
  }

  private void addCacheWriteErrorWarningField(BooleanFieldEditor allWarnings) {
    BooleanFieldEditor cacheWriteErrorWarning =
        new DependentBooleanFieldEditor(allWarnings, PreferenceConstants.CACHE_WRITE_ERROR_WARNING,
            SConsI18N.WarningsPreferencePage_CacheWriteWarning, parent);
    addField(cacheWriteErrorWarning, parent);
    checkEditors.add(cacheWriteErrorWarning);
  }

  private BooleanFieldEditor getAllWarningsField() {
    BooleanFieldEditor allWarnings =
        new BooleanFieldEditor(PreferenceConstants.ALL_WARNINGS_ENABLED,
            SConsI18N.WarningsPreferencePage_AllWarnings, parent) {
          @Override
          protected void valueChanged(boolean oldValue, boolean newValue) {
            super.valueChanged(oldValue, newValue);

            for (BooleanFieldEditor checkEditor : checkEditors) {
              checkEditor.setEnabled(!newValue, parent);
            }
          }
        };
    return allWarnings;
  }
}
