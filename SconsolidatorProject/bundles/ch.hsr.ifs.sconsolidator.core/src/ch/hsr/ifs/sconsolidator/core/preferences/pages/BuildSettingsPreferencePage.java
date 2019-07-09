package ch.hsr.ifs.sconsolidator.core.preferences.pages;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.eclipse.cdt.ui.newui.MultiLineTextFieldEditor;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import ch.hsr.ifs.sconsolidator.core.SConsI18N;
import ch.hsr.ifs.sconsolidator.core.SConsPlugin;
import ch.hsr.ifs.sconsolidator.core.base.utils.UIUtil;
import ch.hsr.ifs.sconsolidator.core.existingbuild.RefreshFromSConsJob;
import ch.hsr.ifs.sconsolidator.core.preferences.PreferenceConstants;


public class BuildSettingsPreferencePage extends FieldEditorOverlayPage implements IWorkbenchPreferencePage {

    private Composite                parent;
    private MultiLineTextFieldEditor additionalCommandlineEditor;
    private MultiLineTextFieldEditor environmentOptionsEditor;

    public BuildSettingsPreferencePage() {
        super(GRID, true);
        setPreferenceStore(SConsPlugin.getWorkspacePreferenceStore());
        setDescription(SConsI18N.SettingsPreferencePage_Description);
    }

    @Override
    public void init(IWorkbench workbench) {}

    @Override
    protected String getPageId() {
        return PreferenceConstants.BUILD_SETTINGS_PAGE_ID;
    }

    @Override
    protected void createFieldEditors() {
        parent = getFieldEditorParent();
        createBuildOptions();
    }

    private void createBuildOptions() {
        addSuppressReadingField();
        addSilentField();
        addKeepGoingField();
        addIgnoreErrorsField();
        addRandomField();
        addNumberOfJobsField();
        addSConstructField();
        addExecutablePathField();
        addAdditionalCommandLineField();
        addEnvironmentOptionsField();
    }

    private void addEnvironmentOptionsField() {
        environmentOptionsEditor = new MultiLineTextFieldEditor(PreferenceConstants.ENVIRONMENT_VARIABLES,
                SConsI18N.BuildSettingsPreferencePage_EnvironmentVariables, parent);
        environmentOptionsEditor.setEmptyStringAllowed(true);
        addField(environmentOptionsEditor);
    }

    private void addAdditionalCommandLineField() {
        additionalCommandlineEditor = new MultiLineTextFieldEditor(PreferenceConstants.ADDITIONAL_COMMANDLINE_OPTIONS,
                SConsI18N.BuildSettingsPreferencePage_AdditionalCommandlineArgs, parent);
        additionalCommandlineEditor.setEmptyStringAllowed(true);
        addField(additionalCommandlineEditor);
    }

    private void addExecutablePathField() {
        DirectoryFieldEditor executablePath = new DirectoryFieldEditor(PreferenceConstants.STARTING_DIRECTORY,
                SConsI18N.BuildSettingsPreferencePage_StartingDirectory, parent);
        executablePath.setEmptyStringAllowed(true);
        addField(executablePath);
    }

    private void addSConstructField() {
        StringFieldEditor sconstructName = new StringFieldEditor(PreferenceConstants.SCONSTRUCT_NAME,
                SConsI18N.BuildSettingsPreferencePage_NameOfSConstructFile, parent);
        sconstructName.setEmptyStringAllowed(false);
        addField(sconstructName);
    }

    private void addNumberOfJobsField() {
        IntegerFieldEditor numberOfJobs = new IntegerFieldEditor(PreferenceConstants.NUMBER_OF_JOBS, SConsI18N.SettingsPreferencePage_NumberOfJobs,
                parent);
        numberOfJobs.setValidRange(0, Integer.MAX_VALUE);
        addField(numberOfJobs);
    }

    private void addRandomField() {
        BooleanFieldEditor random = new BooleanFieldEditor(PreferenceConstants.RANDOM, SConsI18N.SettingsPreferencePage_RandomOrderOptionName,
                parent);
        addField(random);
    }

    private void addIgnoreErrorsField() {
        BooleanFieldEditor ignoreErrors = new BooleanFieldEditor(PreferenceConstants.IGNORE_ERROS,
                SConsI18N.BuildSettingsPreferencePage_IGNORE_ERRORS, parent);
        addField(ignoreErrors);
    }

    private void addKeepGoingField() {
        BooleanFieldEditor keepGoing = new BooleanFieldEditor(PreferenceConstants.KEEP_GOING, SConsI18N.SettingsPreferencePage_KeepGoingOptionName,
                parent);
        addField(keepGoing);
    }

    private void addSilentField() {
        BooleanFieldEditor silent = new BooleanFieldEditor(PreferenceConstants.SILENT, SConsI18N.SettingsPreferencePage_SilentOptionName, parent);
        addField(silent);
    }

    private void addSuppressReadingField() {
        BooleanFieldEditor suppressReadingBuilding = new BooleanFieldEditor(PreferenceConstants.SUPPRESS_READING_BUILDING_MSG,
                SConsI18N.SettingsPreferencePage_SuppressMessageOptionName, parent);
        addField(suppressReadingBuilding);
    }

    @Override
    public boolean performOk() {
        boolean result = super.performOk();

        if (needsSConsRefresh() && userAgrees()) {
            executeSConsRefresh();
        }

        return result;
    }

    private boolean userAgrees() {
        Shell shell = UIUtil.getWindowShell();
        return MessageDialog.openQuestion(shell, SConsI18N.BuildSettingsPreferencePage_PerformRefreshTitle,
                SConsI18N.BuildSettingsPreferencePage_PerformRefreshMessage);
    }

    private void executeSConsRefresh() {
        RefreshFromSConsJob job = new RefreshFromSConsJob(getAffectedProjects());
        job.schedule();
    }

    private Collection<IProject> getAffectedProjects() {
        return isPropertyPage() ? Collections.singletonList(getProject()) : Arrays.asList(ResourcesPlugin.getWorkspace().getRoot().getProjects());
    }

    private boolean needsSConsRefresh() {
        String oldValue = getPreferenceStore().getString(PreferenceConstants.ADDITIONAL_COMMANDLINE_OPTIONS);
        return !additionalCommandlineEditor.getStringValue().equals(oldValue);
    }
}
