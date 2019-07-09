package ch.hsr.ifs.sconsolidator.core;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;


public enum SConsImages {
    LOGO("logo.png"), //
    WIZARD("scons_wizard.png"), // 
    CONSOLE_TERMINATE("console_terminate.gif"), // 
    BUILD_CURRENT_TARGET("build_exec.gif"), //
    CLEAN_CURRENT_TARGET("clean_exec.gif"), //
    REDO_LAST_TARGET("redo_action.gif"), //
    SCONS_TARGET("scons_target.png"), //
    SCONS_TARGET_ADD("scons_target_add.gif"), // 
    SCONS_TARGET_BUILD("scons_target_build.png"), // 
    SCONS_TARGET_BUILD_INTERACTIVE("scons_target_build_interactive.png"), // 
    SCONS_TARGET_EDIT("scons_target_edit.gif"), //
    SCONS_TARGET_DELETE("scons_target_delete.gif"), // 
    ERROR("error.gif"), //
    DEFAULT_TARGET("default_target.gif");

    private static final String                   ENABLED_PREFIX  = "e_";
    private static final String                   DISABLED_PREFIX = "d_";
    private static final IPath                    ICONS_PATH      = new Path("icons");
    private static final Map<String, SConsImages> STRING_TO_ENUM  = new HashMap<String, SConsImages>();

    static {
        for (SConsImages image : values()) {
            STRING_TO_ENUM.put(image.toString(), image);
        }
    }

    @Override
    public String toString() {
        return fileName;
    }

    public static SConsImages fromString(String fileName) {
        return STRING_TO_ENUM.get(fileName);
    }

    private SConsImages(String fileName) {
        this.fileName = fileName;
    }

    public static ImageDescriptor getImageDescriptor(SConsImages image) {
        IPath path = ICONS_PATH.append(image.toString());
        return create(path);
    }

    public static void setImageDescriptors(IAction action, SConsImages img) {
        action.setDisabledImageDescriptor(create(ICONS_PATH.append(DISABLED_PREFIX + img.toString())));
        action.setHoverImageDescriptor(create(ICONS_PATH.append(ENABLED_PREFIX + img.toString())));
        action.setImageDescriptor(create(ICONS_PATH.append(ENABLED_PREFIX + img.toString())));
    }

    private static ImageDescriptor create(IPath path) {
        URL url = FileLocator.find(SConsPlugin.getDefault().getBundle(), path, null);
        return ImageDescriptor.createFromURL(url);
    }

    private final String fileName;
}
