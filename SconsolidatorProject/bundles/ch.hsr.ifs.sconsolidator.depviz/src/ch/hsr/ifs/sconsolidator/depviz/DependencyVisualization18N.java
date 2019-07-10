package ch.hsr.ifs.sconsolidator.depviz;

import org.eclipse.osgi.util.NLS;


public final class DependencyVisualization18N extends NLS {

    private static final String BUNDLE_NAME = "OSGI-INF.l10n.bundle";

    public static String DepVizBundleName;
    public static String DepVizBundleVendor;
    public static String SconsCategoryName;
    public static String SconsDepVizViewName;
    public static String DepTreeVizView_SelectTargetDialogTitle;
    public static String DepTreeVizView_ChooseSConsTargetActionText;
    public static String DepTreeVizView_ChooseSConsTargetActionTooltip;
    public static String DepTreeVizView_CouldNotDetermineTargetMessage;
    public static String DepTreeVizView_CouldNotDetermineTargetTitle;
    public static String DepTreeVizView_SearchingDependenciesInProgress;

    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, DependencyVisualization18N.class);
    }

    private DependencyVisualization18N() {}
}
