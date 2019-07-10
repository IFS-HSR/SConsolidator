package ch.hsr.ifs.sconsolidator.core.preferences.pages;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import ch.hsr.ifs.sconsolidator.core.EmptySConsPathException;
import ch.hsr.ifs.sconsolidator.core.PlatformSpecifics;
import ch.hsr.ifs.sconsolidator.core.SConsI18N;
import ch.hsr.ifs.sconsolidator.core.SConsPlugin;
import ch.hsr.ifs.sconsolidator.core.commands.SConsVersion;
import ch.hsr.ifs.sconsolidator.core.commands.SConsVersionCommand;
import ch.hsr.ifs.sconsolidator.core.console.BuildConsole;
import ch.hsr.ifs.sconsolidator.core.preferences.PreferenceConstants;


public class ExecutablePathPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

    private FileFieldEditor executablePath;
    private boolean         hasBinaryPathChanged;
    private String          currentVersion;
    private Label           versionLabel;

    public ExecutablePathPreferencePage() {
        super(FLAT);
        setPreferenceStore(SConsPlugin.getConfigPreferenceStore());
        setDescription(SConsI18N.ExecutablePathPreferencePage_PageTitle);
    }

    @Override
    public void init(IWorkbench workbench) {
        setExecutablePathIfNotExisting();
    }

    @Override
    protected void createFieldEditors() {
        Composite parent = getFieldEditorParent();
        createExecutablePathField(parent);
        addField(executablePath);

        parent = getFieldEditorParent();
        beforeInsertion(parent);
        addVersionLabel(parent);

        addClearConsoleOption(parent);
        addOpenConsoleOption(parent);
    }

    private void createExecutablePathField(Composite parent) {
        executablePath = new SConsExecutableEditor(parent);
        executablePath.setEmptyStringAllowed(false);
        executablePath.setErrorMessage(SConsI18N.ExecutablePathPreferencePage_WrongPathMessage);
    }

    private SConsVersion determineSConsVersion(String path) throws EmptySConsPathException, IOException, InterruptedException {
        SConsVersionCommand versionCommand = new SConsVersionCommand(path, new BuildConsole(
                SConsI18N.ExecutablePathPreferencePage_SConsVersionConsoleTitle));
        return versionCommand.run(new NullProgressMonitor());
    }

    private void addVersionLabel(Composite parent) {
        versionLabel = new Label(parent, SWT.NONE);
        setCurrentVersion();
        versionLabel.setFont(parent.getFont());
        afterInsertion(versionLabel);
    }

    private void addOpenConsoleOption(Composite parent) {
        BooleanFieldEditor openConsoleWhenBuildingCheck = new BooleanFieldEditor(PreferenceConstants.OPEN_CONSOLE_WHEN_BUILDING,
                SConsI18N.BuildSettingsPreferencePage_OpenConsoleWhenBuilding, parent);
        addField(openConsoleWhenBuildingCheck);
    }

    private void addClearConsoleOption(Composite parent) {
        BooleanFieldEditor clearConsoleBeforeBuildCheck = new BooleanFieldEditor(PreferenceConstants.CLEAR_CONSOLE_BEFORE_BUILD,
                SConsI18N.ExecutablePathPreferencePage_ClearConsoleBeforeFieldCheck, parent);
        addField(clearConsoleBeforeBuildCheck);
    }

    private void setExecutablePathIfNotExisting() {
        if (!isExecutablePathSet()) {
            lookupSConsExecutable();
        }
    }

    private boolean isExecutablePathSet() {
        return !getPreferenceStore().getString(PreferenceConstants.EXECUTABLE_PATH).isEmpty();
    }

    private void lookupSConsExecutable() {
        File fullyQualifiedExec = PlatformSpecifics.findSConsExecOnSystemPath();

        if (fullyQualifiedExec != null) {
            setSConsPathInPreferences(fullyQualifiedExec.getAbsolutePath());
        }
    }

    private void setSConsPathInPreferences(String fullyQualifiedPath) {
        getPreferenceStore().setValue(PreferenceConstants.EXECUTABLE_PATH, fullyQualifiedPath);
    }

    private void beforeInsertion(Composite parent) {
        GridLayout layout = new GridLayout();
        layout.numColumns = 1;
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        layout.horizontalSpacing = 8;
        parent.setLayout(layout);
    }

    private void afterInsertion(Control control) {
        GridData gd = new GridData();
        gd.horizontalSpan = 1;
        control.setLayoutData(gd);
    }

    private void setCurrentVersion() {
        versionLabel.setText(NLS.bind(SConsI18N.ExecutablePathPreferencePage_SConsVersionLabel, currentVersion));
        Point size = versionLabel.computeSize(SWT.DEFAULT, SWT.DEFAULT);
        versionLabel.setSize(size);
    }

    @Override
    public boolean okToLeave() {
        if (!super.okToLeave()) return false;

        if (hasBinaryPathChanged) return askBeforeLeave();

        return true;
    }

    private boolean askBeforeLeave() {
        int clickedButtonIndex = showConfirmationDialog();
        boolean okToLeave = false;

        switch (clickedButtonIndex) {
        case 0: // Save
            executablePath.store();
            okToLeave = true;
            break;
        case 1: // Discard
            executablePath.load();
            okToLeave = true;
            break;
        default:
            throw new IllegalStateException("Unknown button index");
        }

        if (okToLeave) {
            hasBinaryPathChanged = false;
        }

        return okToLeave;
    }

    private int showConfirmationDialog() {
        String[] buttonLabels = { SConsI18N.ExecutablePathPreferencePage_SaveButton, SConsI18N.ExecutablePathPreferencePage_DiscardButton,
                                  IDialogConstants.CANCEL_LABEL };
        MessageDialog messageDialog = new MessageDialog(getShell(), SConsI18N.ExecutablePathPreferencePage_SaveFirstTitle, null,
                SConsI18N.ExecutablePathPreferencePage_SaveFirstMessage, MessageDialog.QUESTION, buttonLabels, 0);
        return messageDialog.open();
    }

    private class SConsExecutableEditor extends FileFieldEditor {

        SConsExecutableEditor(Composite parent) {
            super(PreferenceConstants.EXECUTABLE_PATH, SConsI18N.ExecutablePathPreferencePage_PathSConsExecField, false,
                    StringFieldEditor.VALIDATE_ON_FOCUS_LOST, parent);
        }

        @Override
        protected boolean checkState() {
            boolean validSConsPath = false;
            currentVersion = "?";

            if (isValidSConsExecutable()) {
                try {
                    String path = getTextControl().getText();
                    setSConsPathInPreferences(path);
                    SConsVersion sconsVersion = determineSConsVersion(path);
                    currentVersion = sconsVersion.toString();

                    if (!sconsVersion.isCompatible()) {
                        showIncompatibleSConsError();
                    } else {
                        validSConsPath = true;
                    }
                } catch (Exception e) {
                    showErrorMessage();
                }
            }
            setCurrentVersion();
            return validSConsPath;
        }

        private boolean isValidSConsExecutable() {
            String msg = null;
            String path = getPath();

            if (path.isEmpty() && !isEmptyStringAllowed()) {
                msg = getErrorMessage();
            } else {
                File file = new File(path);

                if (!file.isAbsolute()) {
                    File execInSysPath = PlatformSpecifics.findExecOnSystemPath(path);

                    if (execInSysPath == null) {
                        msg = getErrorMessage();
                    } else {
                        path = execInSysPath.toString();
                        file = new File(path);
                    }
                }

                if (!file.isFile()) {
                    msg = getErrorMessage();
                }
            }

            if (msg != null) {
                showErrorMessage(msg);
                return false;
            }

            if (doCheckState()) {
                clearErrorMessage();
                return true;
            }

            msg = getErrorMessage();
            if (msg != null) {
                showErrorMessage(msg);
            }
            return false;
        }

        private String getPath() {
            String path = getTextControl().getText();
            return path != null ? path.trim() : "";
        }

        private void showIncompatibleSConsError() {
            showErrorMessage(NLS.bind(SConsI18N.ExecutablePathPreferencePage_WrongSConsVersionFound, SConsVersion.MIN_VERSION, currentVersion));
        }

        @Override
        protected void fireValueChanged(String property, Object oldValue, Object newValue) {
            hasBinaryPathChanged = true;
            super.fireValueChanged(property, oldValue, newValue);
        }
    };
}
