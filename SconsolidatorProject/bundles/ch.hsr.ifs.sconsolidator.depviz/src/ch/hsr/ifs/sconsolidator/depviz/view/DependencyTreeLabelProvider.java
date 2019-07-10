package ch.hsr.ifs.sconsolidator.depviz.view;

import org.eclipse.draw2d.IFigure;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.zest.core.viewers.IConnectionStyleProvider;
import org.eclipse.zest.core.viewers.IEntityStyleProvider;
import org.eclipse.zest.core.widgets.ZestStyles;

import ch.hsr.ifs.sconsolidator.core.depanalysis.DependencyTreeNode;


class DependencyTreeLabelProvider implements ILabelProvider, IConnectionStyleProvider, IEntityStyleProvider {

    private static Display DISPLAY     = PlatformUI.getWorkbench().getDisplay();
    private static Color   GRAY        = new Color(DISPLAY, 125, 125, 125);
    private static Color   LIGHT_GRAY  = new Color(DISPLAY, 155, 155, 155);
    private static Color   LIGHT_BLUE  = new Color(DISPLAY, 216, 220, 235);
    private static Color   BLACK       = new Color(DISPLAY, 0, 0, 0);
    private static Color   DARK_RED    = new Color(DISPLAY, 130, 0, 0);
    private static Color   LIGHT_GREEN = new Color(DISPLAY, 80, 255, 80);
    private Object         selected;
    private Object         rootNode;
    private Color          disabledColor;
    private Color          rootColor;

    @Override
    public Image getImage(Object element) {
        return null;
    }

    @Override
    public String getText(Object element) {
        if (element instanceof DependencyTreeNode) return ((DependencyTreeNode) element).getValue();
        return null;
    }

    @Override
    public void addListener(ILabelProviderListener listener) {}

    @Override
    public boolean isLabelProperty(Object element, String property) {
        return false;
    }

    @Override
    public void removeListener(ILabelProviderListener listener) {}

    @Override
    public Color getColor(Object element) {
        return LIGHT_GRAY;
    }

    @Override
    public int getConnectionStyle(Object rel) {
        return ZestStyles.CONNECTIONS_DIRECTED;
    }

    @Override
    public Color getHighlightColor(Object rel) {
        return DARK_RED;
    }

    @Override
    public Color getNodeHighlightColor(Object entity) {
        return null;
    }

    @Override
    public int getLineWidth(Object rel) {
        return 1;
    }

    @Override
    public Color getBorderColor(Object entity) {
        return BLACK;
    }

    @Override
    public Color getBorderHighlightColor(Object entity) {
        return null;
    }

    @Override
    public int getBorderWidth(Object entity) {
        return 0;
    }

    @Override
    public Color getBackgroundColour(Object entity) {
        if (entity == rootNode) {
            if (rootColor == null) {
                rootColor = LIGHT_GREEN;
            }

            return rootColor;
        }

        if (entity == selected) return LIGHT_BLUE;

        return getDisabledColor();
    }

    @Override
    public Color getForegroundColour(Object entity) {
        if (selected != null) return BLACK;

        if (entity == selected) return BLACK;

        return GRAY;
    }

    @Override
    public boolean fisheyeNode(Object entity) {
        return true;
    }

    public void setCurrentSelection(Object root, Object currentSelection) {
        rootNode = root;
        selected = currentSelection;
    }

    private Color getDisabledColor() {
        if (disabledColor == null) {
            disabledColor = new Color(DISPLAY, new RGB(225, 238, 255));
        }
        return disabledColor;
    }

    @Override
    public IFigure getTooltip(Object entity) {
        return null;
    }

    @Override
    public void dispose() {
        disposeColorSafely(disabledColor);
        disposeColorSafely(rootColor);
    }

    private void disposeColorSafely(Color color) {
        if (color != null) {
            color.dispose();
            color = null;
        }
    }
}
