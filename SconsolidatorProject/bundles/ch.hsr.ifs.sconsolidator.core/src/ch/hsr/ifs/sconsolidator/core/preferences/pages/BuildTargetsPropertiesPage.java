package ch.hsr.ifs.sconsolidator.core.preferences.pages;

import static ch.hsr.ifs.sconsolidator.core.base.functional.FunctionalHelper.map;

import java.io.IOException;
import java.util.Collection;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPersistentPreferenceStore;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.dialogs.PropertyPage;

import ch.hsr.ifs.sconsolidator.core.SConsI18N;
import ch.hsr.ifs.sconsolidator.core.SConsImages;
import ch.hsr.ifs.sconsolidator.core.SConsPlugin;
import ch.hsr.ifs.sconsolidator.core.base.functional.UnaryFunction;
import ch.hsr.ifs.sconsolidator.core.preferences.PreferenceConstants;
import ch.hsr.ifs.sconsolidator.core.targets.SConsTargetDialog;
import ch.hsr.ifs.sconsolidator.core.targets.model.SConsBuildTarget;
import ch.hsr.ifs.sconsolidator.core.targets.model.SConsBuildTargetEvent;
import ch.hsr.ifs.sconsolidator.core.targets.model.SConsBuildTargetListener;


public class BuildTargetsPropertiesPage extends PropertyPage implements IWorkbenchPropertyPage, SConsBuildTargetListener {

    private final SConsBuildTargetLabelProvider labelProvider;
    private Image                               defaultTargetImage;
    private TableViewer                         targetViewer;
    private Button                              editTargetButton;
    private Button                              deleteTargetButton;
    private Button                              defaultTargetButton;
    private Composite                           targetArea;
    private SConsBuildTarget                    currentTarget;

    public BuildTargetsPropertiesPage() {
        this.defaultTargetImage = SConsImages.getImageDescriptor(SConsImages.DEFAULT_TARGET).createImage();
        this.labelProvider = new SConsBuildTargetLabelProvider(defaultTargetImage);
        SConsPlugin.getDefault().getSConsTargetManager().addListener(this);
    }

    @Override
    protected void performDefaults() {
        initDefaultTarget();
        super.performDefaults();
    }

    @Override
    public boolean performOk() {
        saveDefaultTarget();
        return super.performOk();
    }

    @Override
    protected void performApply() {
        saveDefaultTarget();
    }

    private void saveDefaultTarget() {
        IPersistentPreferenceStore store = SConsPlugin.getProjectPreferenceStore(getProject());
        SConsBuildTarget defaultTarget = getDefaultTarget();

        if (defaultTarget != null) {
            store.setValue(PreferenceConstants.DEFAULT_TARGET, getDefaultTarget().getCommandLine());

            try {
                store.save();
            } catch (IOException e) {
                SConsPlugin.showExceptionInDisplayThread(e);
            }
        }
    }

    @Override
    public void targetChanged(SConsBuildTargetEvent event) {
        updateListTargets();
    }

    @Override
    public void dispose() {
        super.dispose();
        targetArea.dispose();
        labelProvider.dispose();
        defaultTargetImage.dispose();
        defaultTargetImage = null;
        SConsPlugin.getDefault().getSConsTargetManager().removeListener(this);
    }

    @Override
    public String getDescription() {
        return SConsI18N.BuildTargetsPreferencePage_PageTitle;
    }

    @Override
    protected Control createContents(Composite parent) {
        createTargetArea(parent);
        return targetArea;
    }

    private void createTargetArea(Composite parent) {
        initTargetArea(parent);
        createAddTargetButton();
        createEditTargetButton();
        createDeleteTargetButton();
        createDefaultTargetButton();
    }

    private void initTargetArea(Composite parent) {
        targetArea = createSubsection(parent, SConsI18N.BuildSettingsPreferencePage_BuildTargetsArea);
        GridLayout layout = new GridLayout(4, false);
        layout.verticalSpacing = 10;
        targetArea.setLayout(layout);

        targetViewer = new TableViewer(targetArea);
        targetViewer.setContentProvider(new ArrayContentProvider());
        targetViewer.setLabelProvider(labelProvider);
        updateListTargets();
        initDefaultTarget();

        GridData gd = new GridData(GridData.FILL_BOTH);
        gd.verticalSpan = 10;
        gd.horizontalSpan = 3;
        targetViewer.getTable().setLayoutData(gd);

        targetViewer.addDoubleClickListener(new IDoubleClickListener() {

            @Override
            public void doubleClick(DoubleClickEvent event) {
                editCurrentTarget();
            }
        });

        targetViewer.addSelectionChangedListener(new ISelectionChangedListener() {

            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                targetSelectionChanged(event);
            }
        });
    }

    private void createDefaultTargetButton() {
        defaultTargetButton = createNewButton(targetArea, SConsI18N.BuildTargetsPreferencePage_DefaultTargetButton);
        defaultTargetButton.setEnabled(false);
        defaultTargetButton.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                setTargetsToCurrent();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {}
        });
    }

    private void createDeleteTargetButton() {
        deleteTargetButton = createNewButton(targetArea, SConsI18N.BuildSettingsPreferencePage_RemoveTargetButton);
        deleteTargetButton.setEnabled(false);
        deleteTargetButton.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                deleteCurrentTarget();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {}
        });
    }

    private void createEditTargetButton() {
        editTargetButton = createNewButton(targetArea, SConsI18N.BuildSettingsPreferencePage_EditTargetButton);
        editTargetButton.setEnabled(false);
        editTargetButton.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                editCurrentTarget();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {}
        });
    }

    private void createAddTargetButton() {
        Button addTargetButton = createNewButton(targetArea, SConsI18N.BuildSettingsPreferencePage_AddTargetButton);
        addTargetButton.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                addTargetClicked();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {}
        });
    }

    private IProject getProject() {
        return (IProject) getElement().getAdapter(IProject.class);
    }

    private void updateListTargets() {
        try {
            targetViewer.setInput(getTargets());
        } catch (CoreException e) {
            SConsPlugin.showExceptionInDisplayThread(e);
        }
    }

    private void initDefaultTarget() {
        IPreferenceStore store = SConsPlugin.getProjectPreferenceStore(getProject());
        final String defaultTarget = store.getString(PreferenceConstants.DEFAULT_TARGET);

        try {
            map(getTargets(), new UnaryFunction<SConsBuildTarget, Void>() {

                @Override
                public Void apply(SConsBuildTarget target) {
                    target.setDefault(target.getCommandLine().equals(defaultTarget));
                    return null;
                }
            });

            updateListTargets();
        } catch (CoreException e) {
            SConsPlugin.showExceptionInDisplayThread(e);
        }
    }

    private void setTargetsToCurrent() {
        try {
            map(getTargets(), new UnaryFunction<SConsBuildTarget, Void>() {

                @Override
                public Void apply(SConsBuildTarget target) {
                    target.setDefault(target.equals(currentTarget));
                    return null;
                }
            });

            updateListTargets();
        } catch (CoreException e) {
            SConsPlugin.showExceptionInDisplayThread(e);
        }
    }

    private SConsBuildTarget getDefaultTarget() {
        try {
            for (SConsBuildTarget target : getTargets())
                if (target.isDefault()) return target;
        } catch (CoreException e) {
            SConsPlugin.showExceptionInDisplayThread(e);
        }

        return null;
    }

    private Collection<SConsBuildTarget> getTargets() throws CoreException {
        return SConsPlugin.getDefault().getSConsTargetManager().getTargets(getProject());
    }

    private Composite createSubsection(Composite parent, String label) {
        Group group = new Group(parent, SWT.SHADOW_NONE);
        group.setText(label);
        GridData data = new GridData(SWT.FILL, SWT.CENTER, true, false);
        group.setLayoutData(data);
        return group;
    }

    private Button createNewButton(Composite parent, String text) {
        Button button = new Button(parent, SWT.NONE);
        GridData gd = new GridData();
        gd.widthHint = 100;
        button.setLayoutData(gd);
        button.setText(text);
        return button;
    }

    private void editCurrentTarget() {
        if (currentTarget != null) {
            SConsTargetDialog dialog = SConsTargetDialog.fromExistingTarget(getShell(), currentTarget);
            dialog.open();
        }
    }

    private void deleteCurrentTarget() {
        try {
            if (currentTarget != null && confirmDelete()) {
                SConsPlugin.getDefault().getSConsTargetManager().removeTarget(currentTarget);
                targetSelectionChanged(null);
            }
        } catch (CoreException ex) {
            SConsPlugin.showExceptionInDisplayThread(ex);
        }
    }

    private boolean confirmDelete() {
        String title = SConsI18N.DeleteTargetAction_ConfirmTitle;
        String msg = NLS.bind(SConsI18N.DeleteTargetAction_ConfirmMessage, currentTarget.getDescription());
        return MessageDialog.openQuestion(getShell(), title, msg);
    }

    private void targetSelectionChanged(SelectionChangedEvent event) {
        boolean targetSelected = false;

        if (event != null) {
            IStructuredSelection sel = (IStructuredSelection) event.getSelection();

            if (sel.getFirstElement() instanceof SConsBuildTarget) {
                currentTarget = (SConsBuildTarget) sel.getFirstElement();
                targetSelected = true;
                defaultTargetButton.setEnabled(!currentTarget.isDefault());
            }
        }

        editTargetButton.setEnabled(targetSelected);
        deleteTargetButton.setEnabled(targetSelected);
    }

    private void addTargetClicked() {
        SConsTargetDialog dialog = SConsTargetDialog.fromNewTarget(getShell(), getProject());
        dialog.open();
    }

    private static class SConsBuildTargetLabelProvider extends LabelProvider {

        private final Image defaultTargetImage;

        public SConsBuildTargetLabelProvider(Image defaultTargetImage) {
            this.defaultTargetImage = defaultTargetImage;
        }

        @Override
        public Image getImage(Object obj) {
            if (obj instanceof SConsBuildTarget) {
                SConsBuildTarget target = (SConsBuildTarget) obj;

                if (target.isDefault()) return defaultTargetImage;
            }
            return null;
        }

        @Override
        public String getText(Object obj) {
            if (obj instanceof SConsBuildTarget) {
                SConsBuildTarget target = (SConsBuildTarget) obj;
                return target.toString();
            }
            return null;
        }
    }
}
