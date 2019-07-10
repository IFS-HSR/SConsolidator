package ch.hsr.ifs.sconsolidator.depviz;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;


public enum DependencyVisualizationImages {
    VIEW_IMAGE("depviz_view.png"), //
    CHOOSE_SCONS_TARGET("choose_target.gif");

    private static final IPath ICONS_PATH = new Path("icons");
    private final String       fileName;

    @Override
    public String toString() {
        return fileName;
    }

    private DependencyVisualizationImages(String fileName) {
        this.fileName = fileName;
    }

    public static ImageDescriptor getImageDescriptor(DependencyVisualizationImages image) {
        IPath path = ICONS_PATH.append(image.toString());
        URL url = FileLocator.find(DependencyVisualizationPlugin.getDefault().getBundle(), path, null);
        return ImageDescriptor.createFromURL(url);
    }
}
