package ch.hsr.ifs.sconsolidator.core.targets;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.bindings.BindingManagerEvent;
import org.eclipse.jface.bindings.IBindingManagerListener;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.keys.IBindingService;
import org.eclipse.ui.part.DrillDownAdapter;
import org.eclipse.ui.part.ViewPart;

import ch.hsr.ifs.sconsolidator.core.SConsI18N;
import ch.hsr.ifs.sconsolidator.core.targets.actions.AddTargetAction;
import ch.hsr.ifs.sconsolidator.core.targets.actions.BuildInteractiveTargetAction;
import ch.hsr.ifs.sconsolidator.core.targets.actions.BuildTargetAction;
import ch.hsr.ifs.sconsolidator.core.targets.actions.DeleteTargetAction;
import ch.hsr.ifs.sconsolidator.core.targets.actions.EditTargetAction;
import ch.hsr.ifs.sconsolidator.core.targets.actions.RebuildLastTargetAction;


public class SConsTargetView extends ViewPart {

    public static String                 VIEW_ID                   = "ch.hsr.ifs.sconsolidator.core.SConsTargetView";
    private static String                TARGET_BUILD_LAST_COMMAND = "ch.hsr.ifs.sconsolidator.core.targetBuildLastCommand";
    private TreeViewer                   treeViewer;
    private BuildTargetAction            buildTargetAction;
    private AddTargetAction              newTargetAction;
    private EditTargetAction             editTargetAction;
    private DrillDownAdapter             drillDownAdapter;
    private DeleteTargetAction           deleteTargetAction;
    private BuildInteractiveTargetAction buildInteractiveTargetAction;
    private RebuildLastTargetAction      buildLastTargetAction;
    private IBindingService              bindingService;

    @Override
    public void createPartControl(Composite parent) {
        initTreeViewer(parent);
        initListeners();
        initSorter();
        initTreeInput();
        makeActions();
        contributeToActionBars();
        hookContextMenu();
        updateActions((IStructuredSelection) treeViewer.getSelection());
        initBindingService();
    }

    private void initTreeInput() {
        treeViewer.setInput(ResourcesPlugin.getWorkspace().getRoot());
        getSite().setSelectionProvider(treeViewer);
    }

    private void initTreeViewer(Composite parent) {
        treeViewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
        treeViewer.setUseHashlookup(true);
        treeViewer.setContentProvider(new SConsTargetContentProvider());
        treeViewer.setLabelProvider(new SConsTargetLabelProvider());
        drillDownAdapter = new DrillDownAdapter(treeViewer);
    }

    @Override
    public void dispose() {
        if (bindingService != null) {
            bindingService.removeBindingManagerListener(bindingManagerListener);
            bindingService = null;
        }
        super.dispose();
    }

    private void initBindingService() {
        bindingService = (IBindingService) PlatformUI.getWorkbench().getService(IBindingService.class);
        if (bindingService != null) {
            bindingService.addBindingManagerListener(bindingManagerListener);
        }
    }

    private void initSorter() {
        treeViewer.setComparator(new ViewerComparator() {

            @Override
            public int category(Object element) {
                if (element instanceof IResource) return 0;
                return 1;
            }
        });
    }

    private void initListeners() {
        treeViewer.addDoubleClickListener(new IDoubleClickListener() {

            @Override
            public void doubleClick(DoubleClickEvent event) {
                handleDoubleClick();
            }
        });
        treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {

            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                handleSelectionChanged(event);
            }
        });
    }

    @Override
    public void setFocus() {
        treeViewer.getTree().setFocus();
    }

    private void hookContextMenu() {
        MenuManager menuMgr = new MenuManager("#PopupMenu");
        menuMgr.setRemoveAllWhenShown(true);
        menuMgr.addMenuListener(new IMenuListener() {

            @Override
            public void menuAboutToShow(IMenuManager manager) {
                fillContextMenu(manager);
                updateActions((IStructuredSelection) treeViewer.getSelection());
            }
        });

        Menu menu = menuMgr.createContextMenu(treeViewer.getControl());
        treeViewer.getControl().setMenu(menu);
    }

    private void makeActions() {
        Shell shell = treeViewer.getControl().getShell();
        buildTargetAction = new BuildTargetAction();
        buildInteractiveTargetAction = new BuildInteractiveTargetAction();
        buildLastTargetAction = new RebuildLastTargetAction();
        newTargetAction = new AddTargetAction(shell);
        editTargetAction = new EditTargetAction(shell);
        deleteTargetAction = new DeleteTargetAction(shell);
    }

    private void contributeToActionBars() {
        IActionBars actionBars = getViewSite().getActionBars();
        fillLocalToolBar(actionBars.getToolBarManager());
    }

    private void fillLocalToolBar(IToolBarManager toolBar) {
        toolBar.add(newTargetAction);
        toolBar.add(editTargetAction);
        toolBar.add(deleteTargetAction);
        toolBar.add(buildTargetAction);
        toolBar.add(buildInteractiveTargetAction);
        toolBar.add(new Separator());
    }

    private void handleDoubleClick() {
        buildTargetAction.run();
    }

    private void handleSelectionChanged(SelectionChangedEvent event) {
        IStructuredSelection sel = (IStructuredSelection) event.getSelection();
        updateActions(sel);
    }

    private void updateActions(IStructuredSelection sel) {
        newTargetAction.selectionChanged(sel);
        buildTargetAction.selectionChanged(sel);
        editTargetAction.selectionChanged(sel);
        deleteTargetAction.selectionChanged(sel);
        buildInteractiveTargetAction.selectionChanged(sel);
        buildLastTargetAction.selectionChanged(sel);
    }

    private void fillContextMenu(IMenuManager manager) {
        manager.add(newTargetAction);
        manager.add(editTargetAction);
        manager.add(deleteTargetAction);
        manager.add(new Separator());
        drillDownAdapter.addNavigationActions(manager);
        manager.add(new Separator());
        manager.add(buildTargetAction);
        manager.add(buildInteractiveTargetAction);
        manager.add(buildLastTargetAction);
    }

    private final IBindingManagerListener bindingManagerListener = new IBindingManagerListener() {

        @Override
        public void bindingManagerChanged(BindingManagerEvent event) {
            if (event.isActiveBindingsChanged()) {
                // FIXME these key bindings currently not work. investigate why!
                // String keyBinding = bindingService
                // .getBestActiveBindingFormattedFor(IWorkbenchCommandConstants.FILE_RENAME);
                // if (keyBinding != null) {
                // editTargetAction.setText(Messages.EditTargetAction_EditTargetName + "\t" + keyBinding);
                // }
                //
                // keyBinding =
                // bindingService.getBestActiveBindingFormattedFor(IWorkbenchCommandConstants.EDIT_DELETE);
                // if (keyBinding != null) {
                // deleteTargetAction.setText(Messages.DeleteTargetAction_DeleteTargetName + "\t" +
                // keyBinding);
                // }

                String keyBinding = bindingService.getBestActiveBindingFormattedFor(TARGET_BUILD_LAST_COMMAND);
                if (keyBinding != null) {
                    buildLastTargetAction.setText(SConsI18N.RebuildLastTargetAction_RebuildLastTargetActionName + "\t" + keyBinding);
                }
            }
        }
    };
}
