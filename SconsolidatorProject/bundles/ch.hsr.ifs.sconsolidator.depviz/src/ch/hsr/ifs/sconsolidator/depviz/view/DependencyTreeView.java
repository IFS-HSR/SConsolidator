package ch.hsr.ifs.sconsolidator.depviz.view;

import static ch.hsr.ifs.sconsolidator.depviz.DependencyVisualization18N.DepTreeVizView_ChooseSConsTargetActionText;
import static ch.hsr.ifs.sconsolidator.depviz.DependencyVisualization18N.DepTreeVizView_ChooseSConsTargetActionTooltip;
import static ch.hsr.ifs.sconsolidator.depviz.DependencyVisualization18N.DepTreeVizView_SelectTargetDialogTitle;

import java.util.Collection;
import java.util.Iterator;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ResourceListSelectionDialog;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.zest.core.viewers.AbstractZoomableViewer;
import org.eclipse.zest.core.viewers.EntityConnectionData;
import org.eclipse.zest.core.viewers.GraphViewer;
import org.eclipse.zest.core.viewers.IZoomableWorkbenchPart;
import org.eclipse.zest.core.viewers.ZoomContributionViewItem;
import org.eclipse.zest.core.widgets.Graph;
import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.core.widgets.ZestStyles;
import org.eclipse.zest.layouts.LayoutStyles;
import org.eclipse.zest.layouts.algorithms.HorizontalTreeLayoutAlgorithm;

import ch.hsr.ifs.sconsolidator.core.SConsHelper;
import ch.hsr.ifs.sconsolidator.core.SConsNatureTypes;
import ch.hsr.ifs.sconsolidator.core.SConsPlugin;
import ch.hsr.ifs.sconsolidator.core.base.functional.VoidFunction;
import ch.hsr.ifs.sconsolidator.core.base.tuple.Triple;
import ch.hsr.ifs.sconsolidator.core.base.tuple.Tuple;
import ch.hsr.ifs.sconsolidator.core.base.utils.UIUtil;
import ch.hsr.ifs.sconsolidator.core.depanalysis.DependencyTreeNode;
import ch.hsr.ifs.sconsolidator.depviz.DependencyVisualization18N;
import ch.hsr.ifs.sconsolidator.depviz.DependencyVisualizationImages;
import ch.hsr.ifs.sconsolidator.depviz.DependencyVisualizationPlugin;
import ch.hsr.ifs.sconsolidator.depviz.model.DependencyTreeContentProvider;
import ch.hsr.ifs.sconsolidator.depviz.model.TargetDependencyCollectorJob;


public class DependencyTreeView extends ViewPart implements IZoomableWorkbenchPart {

    private static Display                DISPLAY = PlatformUI.getWorkbench().getDisplay();
    private GraphViewer                   viewer;
    private DependencyTreeNode            currentNode;
    private DependencyTreeLabelProvider   currentLabelProvider;
    private DependencyTreeContentProvider contentProvider;
    private ZoomContributionViewItem      contextZoomViewItem;
    private ZoomContributionViewItem      toolbarZoomViewItem;
    private Font                          searchFont;
    private Action                        chooseTargetAction;
    private Action                        chooseTargetDialogActionToolbar;

    @Override
    public void createPartControl(Composite parent) {
        setupView(parent);
        initFont();
        addSelectionListener();
        initZoom();
        registerChooseTargetActions();
        hookContextMenu();
        fillToolBar();
    }

    private void setupView(Composite parent) {
        viewer = new GraphViewer(parent, SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
        currentLabelProvider = new DependencyTreeLabelProvider();
        contentProvider = new DependencyTreeContentProvider();
        viewer.setLabelProvider(currentLabelProvider);
        viewer.setContentProvider(contentProvider);
        viewer.setInput(null);
        viewer.setConnectionStyle(ZestStyles.CONNECTIONS_DIRECTED);
        viewer.setLayoutAlgorithm(new HorizontalTreeLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING));
        viewer.applyLayout();
        parent.addControlListener(new ControlAdapter() {

            @Override
            public void controlResized(ControlEvent e) {
                viewer.applyLayout();
            }
        });
    }

    private void initFont() {
        FontData fontData = DISPLAY.getSystemFont().getFontData()[0];
        fontData.height = 40;
        searchFont = new Font(DISPLAY, fontData);
    }

    private void initZoom() {
        toolbarZoomViewItem = new ZoomContributionViewItem(this);
        contextZoomViewItem = new ZoomContributionViewItem(this);
    }

    private void addSelectionListener() {
        viewer.addSelectionChangedListener(new ISelectionChangedListener() {

            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                Object selectedElement = ((IStructuredSelection) event.getSelection()).getFirstElement();
                if (selectedElement instanceof EntityConnectionData) return;
                setNewSelection(selectedElement);
            }
        });
    }

    private void setNewSelection(Object selectedItem) {
        currentLabelProvider.setCurrentSelection(currentNode, selectedItem);
        viewer.update(contentProvider.getElements(currentNode), null);
    }

    private void fillToolBar() {
        IActionBars bars = getViewSite().getActionBars();
        bars.getMenuManager().add(toolbarZoomViewItem);
        IToolBarManager toolBarManager = bars.getToolBarManager();
        toolBarManager.add(chooseTargetDialogActionToolbar);
    }

    private void focusOn(DependencyTreeNode node) {
        viewer.setSelection(new StructuredSelection());
        setNewSelection(null);
        viewer.setFilters(new ViewerFilter[] {});
        viewer.setInput(node);
        executeFocus();
        currentNode = node;
        viewer.setSelection(new StructuredSelection(node));
        setNewSelection(node);
    }

    private void executeFocus() {
        Graph graph = viewer.getGraphControl();
        Dimension center = new Dimension(graph.getBounds().width / 2, graph.getBounds().height / 2);
        Iterator<?> nodes = graph.getNodes().iterator();

        while (nodes.hasNext()) {
            GraphNode graphNode = (GraphNode) nodes.next();

            if (graphNode.getLocation().x <= 1 && graphNode.getLocation().y <= 1) {
                graphNode.setLocation(center.width, center.height);
            }
        }
    }

    private void registerChooseTargetActions() {
        chooseTargetAction = new Action() {

            @Override
            public void run() {
                chooseTarget();
            }
        };
        chooseTargetAction.setText(DepTreeVizView_ChooseSConsTargetActionText);
        chooseTargetAction.setToolTipText(DepTreeVizView_ChooseSConsTargetActionTooltip);
        chooseTargetDialogActionToolbar = new Action() {

            @Override
            public void run() {
                chooseTargetAction.run();
            }
        };
        chooseTargetDialogActionToolbar.setToolTipText(DepTreeVizView_ChooseSConsTargetActionText);
        ImageDescriptor chooseSconsTargetImg = DependencyVisualizationImages.getImageDescriptor(DependencyVisualizationImages.CHOOSE_SCONS_TARGET);
        chooseTargetDialogActionToolbar.setImageDescriptor(chooseSconsTargetImg);
    }

    private void chooseTarget() {
        Object[] targets = askForTarget();
        if (targets == null || targets.length == 0) return;
        createDependencyCollectorJob(collectTargetInfos(targets)).schedule();
    }

    private Triple<String, String, IProject> collectTargetInfos(Object[] targets) {
        IResource target = (IResource) targets[0];
        IProject project = getProject(target);
        String rootPath = SConsHelper.determineStartingDirectory(project);

        if (rootPath == null) {
            showNotFoundError();
        }

        String fullTargetPath = target.getLocation().makeRelativeTo(new Path(rootPath)).toOSString();
        return Tuple.from(rootPath, fullTargetPath, project);
    }

    private IProject getProject(IResource target) {
        return getWorkspaceRoot().getProject(target.getFullPath().segment(0));
    }

    private void showNotFoundError() {
        String msg = DependencyVisualization18N.DepTreeVizView_CouldNotDetermineTargetMessage;
        Status status = new Status(IStatus.ERROR, DependencyVisualizationPlugin.PLUGIN_ID, msg, null);
        SConsPlugin.showExceptionInDisplayThread(DependencyVisualization18N.DepTreeVizView_CouldNotDetermineTargetTitle, msg, new CoreException(
                status));
    }

    private Object[] askForTarget() {
        ResourceListSelectionDialog dialog = getTargetSelectionDialog();
        if (dialog.open() != Window.OK) return null;
        return dialog.getResult();
    }

    private ResourceListSelectionDialog getTargetSelectionDialog() {
        ResourceListSelectionDialog dialog = new ResourceListSelectionDialog(getShell(), getWorkspaceRoot(), IResource.FILE | IResource.FOLDER |
                                                                                                             IResource.PROJECT) {

            @Override
            protected boolean select(IResource resource) {
                // We can only consider targets in SCons projects
                return SConsNatureTypes.isOpenSConsProject(resource.getProject());
            }
        };
        dialog.setTitle(DepTreeVizView_SelectTargetDialogTitle);
        return dialog;
    }

    private IWorkspaceRoot getWorkspaceRoot() {
        return ResourcesPlugin.getWorkspace().getRoot();
    }

    private Shell getShell() {
        return getPlugin().getWorkbench().getActiveWorkbenchWindow().getShell();
    }

    private DependencyVisualizationPlugin getPlugin() {
        return DependencyVisualizationPlugin.getDefault();
    }

    private Job createDependencyCollectorJob(Triple<String, String, IProject> targetInfos) {
        return new TargetDependencyCollectorJob(targetInfos, new VoidFunction<Collection<DependencyTreeNode>>() {

            @Override
            public void apply(Collection<DependencyTreeNode> tree) {
                focusOn(tree);
            }
        });
    }

    private void focusOn(final Collection<DependencyTreeNode> tree) {
        if (tree.isEmpty()) return;

        UIUtil.runInDisplayThread(new Runnable() {

            @Override
            public void run() {
                focusOn(tree.iterator().next());
            }
        });
    }

    private void hookContextMenu() {
        MenuManager menuMgr = new MenuManager("#PopupMenu");
        menuMgr.setRemoveAllWhenShown(true);
        fillContextMenu(menuMgr);
        menuMgr.addMenuListener(new IMenuListener() {

            @Override
            public void menuAboutToShow(IMenuManager manager) {
                fillContextMenu(manager);
            }
        });
        Menu menu = menuMgr.createContextMenu(viewer.getControl());
        viewer.getControl().setMenu(menu);
        getSite().registerContextMenu(menuMgr, viewer);
    }

    private void fillContextMenu(IMenuManager manager) {
        manager.add(new Separator());
        manager.add(chooseTargetAction);
        if (((IStructuredSelection) viewer.getSelection()).size() > 0) {
            manager.add(new Separator());
        }
        manager.add(contextZoomViewItem);
    }

    @Override
    public void dispose() {
        searchFont.dispose();
        super.dispose();
    }

    @Override
    public AbstractZoomableViewer getZoomableViewer() {
        return viewer;
    }

    @Override
    public void setFocus() {}
}
