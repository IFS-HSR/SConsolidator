package ch.hsr.ifs.sconsolidator.core.preferences.pages;

import static ch.hsr.ifs.sconsolidator.core.preferences.profiles.PerformanceAccuracyProfiles.BALANCED;
import static ch.hsr.ifs.sconsolidator.core.preferences.profiles.PerformanceAccuracyProfiles.DEFAULT;
import static ch.hsr.ifs.sconsolidator.core.preferences.profiles.PerformanceAccuracyProfiles.MAXIMUM_PERFORMANCE;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import ch.hsr.ifs.sconsolidator.core.SConsI18N;
import ch.hsr.ifs.sconsolidator.core.SConsNatureTypes;
import ch.hsr.ifs.sconsolidator.core.SConsPlugin;
import ch.hsr.ifs.sconsolidator.core.preferences.PreferenceConstants;
import ch.hsr.ifs.sconsolidator.core.preferences.profiles.PerformanceAccuracyProfiles;
import ch.hsr.ifs.sconsolidator.core.preferences.profiles.SConsDecider;


public class PerformanceAccuracyPreferencePage extends FieldEditorOverlayPage implements IWorkbenchPreferencePage {

    private static String[][] ACCURACY_VS_SPEED = //
                                                { //
                                                  { DEFAULT.getDescription(), DEFAULT.toString() }, //
                                                  { BALANCED.getDescription(), BALANCED.toString() }, //
                                                  { MAXIMUM_PERFORMANCE.getDescription(), MAXIMUM_PERFORMANCE.toString() } //
                                                };

    private Composite             parent;
    private RadioGroupFieldEditor accuracyVsSpeed;
    private Text                  stackSize;
    private Text                  maxDrift;
    private Text                  md5ChunkSize;
    private Button                useCache;
    private Button                implicitDepsUnchanged;
    private Button                includesInCCFlags;
    private BooleanFieldEditor    expertMode;
    private List<FieldEditor>     fieldEditors;
    private Button                md5DeciderButton;
    private Button                md5TimestampButton;
    private Button                timestampNewerButton;
    private Button                timestampMatchButton;
    private Composite             radioGroup;

    public PerformanceAccuracyPreferencePage() {
        super(GRID, true);
        setPreferenceStore(SConsPlugin.getWorkspacePreferenceStore());
        setDescription(SConsI18N.SettingsPreferencePage_PerfVSAccuracydescription);
    }

    @Override
    public void init(IWorkbench workbench) {}

    @Override
    protected String getPageId() {
        return PreferenceConstants.PERF_VS_ACCURACY_PAGE_ID;
    }

    @Override
    protected void createFieldEditors() {
        parent = getFieldEditorParent();
        createPerfRelatedBuildOptions();
        initFields();
    }

    @Override
    protected void performDefaults() {
        super.performDefaults();

        setPerfAccuracySettings(PerformanceAccuracyProfiles.DEFAULT.getProfile().getSettings());
        toggleExpertMode(false);
    }

    @Override
    public boolean performOk() {
        boolean result = super.performOk();

        if (result) {
            getPreferenceStore().setValue(PreferenceConstants.USE_CACHE, useCache.getSelection());
            getPreferenceStore().setValue(PreferenceConstants.IMPLICIT_DEPS_UNCHANGED, implicitDepsUnchanged.getSelection());
            getPreferenceStore().setValue(PreferenceConstants.SYSTEM_HEADER_CCFLAGS_TRICK, includesInCCFlags.getSelection());
            getPreferenceStore().setValue(PreferenceConstants.MAX_DRIFT, maxDrift.getText());
            getPreferenceStore().setValue(PreferenceConstants.STACK_SIZE, stackSize.getText());
            getPreferenceStore().setValue(PreferenceConstants.MD5_CHUNK_SIZE, md5ChunkSize.getText());
            getPreferenceStore().setValue(PreferenceConstants.DECIDERS, getCurrentDecider());
        }

        return result;
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        super.propertyChange(event);

        if (accuracyVsSpeed.equals(event.getSource()) && !expertMode.getBooleanValue()) {
            String newValue = (String) event.getNewValue();
            PerformanceAccuracyProfiles profile = PerformanceAccuracyProfiles.fromString(newValue);
            Map<String, String> settings = profile.getProfile().getSettings();
            setPerfAccuracySettings(settings);
        }
    }

    private void setPerfAccuracySettings(Map<String, String> settings) {
        useCache.setSelection(Boolean.valueOf(settings.get(PreferenceConstants.USE_CACHE)));
        implicitDepsUnchanged.setSelection(Boolean.valueOf(settings.get(PreferenceConstants.IMPLICIT_DEPS_UNCHANGED)));
        includesInCCFlags.setSelection(Boolean.valueOf(settings.get(PreferenceConstants.SYSTEM_HEADER_CCFLAGS_TRICK)));
        maxDrift.setText(settings.get(PreferenceConstants.MAX_DRIFT));
        stackSize.setText(settings.get(PreferenceConstants.STACK_SIZE));
        md5ChunkSize.setText(settings.get(PreferenceConstants.MD5_CHUNK_SIZE));
        setDecider(settings.get(PreferenceConstants.DECIDERS));
    }

    private void setDecider(String decider) {
        md5DeciderButton.setSelection(false);
        md5TimestampButton.setSelection(false);
        timestampMatchButton.setSelection(false);
        timestampNewerButton.setSelection(false);

        switch (SConsDecider.fromString(decider)) {
        case MD5:
            md5DeciderButton.setSelection(true);
            break;
        case MD5_TIMESTAMP:
            md5TimestampButton.setSelection(true);
            break;
        case TIMESTAMP_MATCH:
            timestampMatchButton.setSelection(true);
            break;
        case TIMESTAMP_NEWER:
            timestampNewerButton.setSelection(true);
            break;
        default:
            throw new IllegalArgumentException(decider + " not known!");
        }
    }

    private String getCurrentDecider() {
        if (md5DeciderButton.getSelection())
            return SConsDecider.MD5.toString();
        else if (md5TimestampButton.getSelection())
            return SConsDecider.MD5_TIMESTAMP.toString();
        else if (timestampMatchButton.getSelection())
            return SConsDecider.TIMESTAMP_MATCH.toString();
        else return SConsDecider.TIMESTAMP_NEWER.toString();
    }

    private void initFields() {
        useCache.setSelection(getPreferenceStore().getBoolean(PreferenceConstants.USE_CACHE));
        implicitDepsUnchanged.setSelection(getPreferenceStore().getBoolean(PreferenceConstants.IMPLICIT_DEPS_UNCHANGED));
        includesInCCFlags.setSelection(getPreferenceStore().getBoolean(PreferenceConstants.SYSTEM_HEADER_CCFLAGS_TRICK));
        maxDrift.setText(getPreferenceStore().getString(PreferenceConstants.MAX_DRIFT));
        stackSize.setText(getPreferenceStore().getString(PreferenceConstants.STACK_SIZE));
        md5ChunkSize.setText(getPreferenceStore().getString(PreferenceConstants.MD5_CHUNK_SIZE));
        setDecider(getPreferenceStore().getString(PreferenceConstants.DECIDERS));
        toggleExpertMode(getPreferenceStore().getBoolean(PreferenceConstants.EXPERT_MODE));
    }

    private void createPerfRelatedBuildOptions() {
        accuracyVsSpeed = new RadioGroupFieldEditor(PreferenceConstants.PERF_ACCURACY_PROFILE, SConsI18N.PerformanceAccuracyPreferencePage_Profiles,
                3, ACCURACY_VS_SPEED, parent);
        addField(accuracyVsSpeed, parent);
        fieldEditors = new LinkedList<FieldEditor>();
        expertMode = new BooleanFieldEditor(PreferenceConstants.EXPERT_MODE, SConsI18N.SettingsPreferencePage_ExpertMode, parent) {

            @Override
            protected void valueChanged(boolean oldValue, boolean newValue) {
                super.valueChanged(oldValue, newValue);
                toggleExpertMode(newValue);
            }
        };
        addField(expertMode, parent);

        createNewLabel(parent, SConsI18N.SettingsPreferencePage_StackSizeOptionName,
                SConsI18N.PerformanceAccuracyPreferencePage_StackSizeOptionTooltip);
        stackSize = new Text(parent, SWT.SINGLE | SWT.BORDER);
        stackSize.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        createNewLabel(parent, SConsI18N.SettingsPreferencePage_MaxDriftOptionName,
                SConsI18N.PerformanceAccuracyPreferencePage_MaxDriftOptionTooltip);
        maxDrift = createNewTextField(parent);

        createNewLabel(parent, SConsI18N.SettingsPreferencePage_MD5ChunkSizeOptionName,
                SConsI18N.PerformanceAccuracyPreferencePage_MD5ChunkSizeOptionTooltip);
        md5ChunkSize = createNewTextField(parent);

        useCache = createNewCheckbox(parent, SConsI18N.SettingsPreferencePage_ImplicitCacheOptionName,
                SConsI18N.PerformanceAccuracyPreferencePage_ImplicitCacheOptionTooltip);
        implicitDepsUnchanged = createNewCheckbox(parent, SConsI18N.SettingsPreferencePage_ImplicitDependenciesUnchanged,
                SConsI18N.PerformanceAccuracyPreferencePage_ImplicitDependenciesUnchangedTooltip);
        includesInCCFlags = createNewCheckbox(parent, SConsI18N.PerformanceAccuracyPreferencePage_SystemIncludesCCFlagsTrickOptionName,
                SConsI18N.PerformanceAccuracyPreferencePage_SystemIncludesCCFlagsTrickOptionTooltip);
        includesInCCFlags.setVisible(hasSConsManagedBuildNature());

        createDeciderRadioGroup();
    }

    private boolean hasSConsManagedBuildNature() {
        if (!isPropertyPage()) return true;

        try {
            return getProject().hasNature(SConsNatureTypes.MANAGED_PROJECT_NATURE.getId());
        } catch (CoreException e) {
            return false;
        }
    }

    private Text createNewTextField(Composite parent) {
        Text textField = new Text(parent, SWT.SINGLE | SWT.BORDER);
        textField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        return textField;
    }

    private Button createNewCheckbox(Composite parent, String text, String tooltip) {
        Button checkBox = new Button(parent, SWT.CHECK);
        checkBox.setText(text);
        checkBox.setToolTipText(tooltip);
        checkBox.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING | GridData.VERTICAL_ALIGN_BEGINNING));
        return checkBox;
    }

    private Button createNewDeciderRadioButton(SConsDecider decider) {
        Button radioButton = new Button(radioGroup, SWT.RADIO);
        radioButton.setText(decider.getDescription());
        radioButton.setToolTipText(decider.getHelpText());
        return radioButton;
    }

    private Label createNewLabel(Composite parent, String text, String tooltip) {
        Label label = new Label(parent, SWT.LEFT);
        label.setText(text);
        label.setToolTipText(tooltip);
        return label;
    }

    private void createDeciderRadioGroup() {
        Composite comp = new Composite(parent, SWT.NONE);
        comp.setVisible(hasSConsManagedBuildNature());
        new Label(comp, SWT.LEFT).setText(SConsI18N.SettingsPreferencePage_Decider);
        GridLayout layout = new GridLayout(2, false);
        comp.setLayout(layout);
        radioGroup = new Composite(comp, SWT.NONE);
        radioGroup.setLayout(layout);

        md5DeciderButton = createNewDeciderRadioButton(SConsDecider.MD5);
        md5TimestampButton = createNewDeciderRadioButton(SConsDecider.MD5_TIMESTAMP);
        timestampNewerButton = createNewDeciderRadioButton(SConsDecider.TIMESTAMP_NEWER);
        timestampMatchButton = createNewDeciderRadioButton(SConsDecider.TIMESTAMP_MATCH);
    }

    private void toggleExpertMode(boolean newValue) {
        toggleFieldEditors(newValue);
        useCache.setEnabled(newValue);
        implicitDepsUnchanged.setEnabled(newValue);
        includesInCCFlags.setEnabled(newValue);
        md5ChunkSize.setEnabled(newValue);
        maxDrift.setEnabled(newValue);
        stackSize.setEnabled(newValue);
        md5DeciderButton.setEnabled(newValue);
        md5TimestampButton.setEnabled(newValue);
        timestampNewerButton.setEnabled(newValue);
        timestampMatchButton.setEnabled(newValue);
        accuracyVsSpeed.setEnabled(!newValue, parent);
    }

    private void toggleFieldEditors(boolean newValue) {
        for (FieldEditor fieldEditor : fieldEditors) {
            fieldEditor.setEnabled(newValue, parent);
        }
    }
}
