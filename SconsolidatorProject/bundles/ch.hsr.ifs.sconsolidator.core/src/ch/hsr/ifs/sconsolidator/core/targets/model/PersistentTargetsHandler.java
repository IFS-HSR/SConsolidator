package ch.hsr.ifs.sconsolidator.core.targets.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.ICDescriptor;
import org.eclipse.cdt.core.settings.model.ICStorageElement;
import org.eclipse.cdt.core.settings.model.XmlStorageUtil;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.w3c.dom.Document;

import ch.hsr.ifs.sconsolidator.core.SConsPlugin;
import ch.hsr.ifs.sconsolidator.core.base.utils.FileUtil;


@SuppressWarnings("deprecation")
public class PersistentTargetsHandler {

    public static String                 TARGETS_EXT             = "targets";
    private static String                SCONS_TARGET_KEY        = SConsPlugin.getPluginId() + ".buildtargets";
    private static String                BUILD_TARGET_ELEMENT    = "buildTargets";
    private static String                TARGET_ELEMENT          = "target";
    private static String                TARGET_ATTR_ID          = "targetID";
    private static String                TARGET_ATTR_DESCRIPTION = "description";
    private static String                TARGET_ARGUMENTS        = "buildArguments";
    private static String                TARGET_NAME             = "buildTargetName";
    private final List<SConsBuildTarget> targets;
    private final IProject               project;

    public PersistentTargetsHandler(IProject project) {
        this.project = project;
        targets = new ArrayList<SConsBuildTarget>();
        initTargets(project);
    }

    private IPath getTargetFilePath(IProject project) {
        return SConsPlugin.getDefault().getStateLocation().append(project.getName()).addFileExtension(TARGETS_EXT);
    }

    private ICDescriptor getProjectDescription() throws CoreException {
        return CCorePlugin.getDefault().getCProjectDescription(getProject(), true);
    }

    public Collection<SConsBuildTarget> getTargets() {
        return targets;
    }

    public void add(SConsBuildTarget target) {
        if (contains(target)) return;
        targets.add(target);
    }

    public boolean contains(SConsBuildTarget target) {
        return targets.contains(target);
    }

    public boolean remove(SConsBuildTarget target) {
        if (!contains(target)) return false;
        return targets.remove(target);
    }

    public void saveTargets() throws CoreException {
        ICDescriptor descriptor = getProjectDescription();
        ICStorageElement rootElement = descriptor.getProjectStorageElement(SCONS_TARGET_KEY);
        rootElement.clear();
        rootElement = rootElement.createChild(BUILD_TARGET_ELEMENT);

        for (SConsBuildTarget target : targets) {
            persistTargetElement(rootElement, target);
        }

        descriptor.saveProjectData();
    }

    public SConsBuildTarget findTarget(String name) {
        for (SConsBuildTarget target : targets) {
            if (name.equals(target.getTargetName())) return target;
        }
        return null;
    }

    private IProject getProject() {
        return project;
    }

    private ICStorageElement persistTargetElement(ICStorageElement parent, SConsBuildTarget target) {
        ICStorageElement targetElem = parent.createChild(TARGET_ELEMENT);
        targetElem.setAttribute(TARGET_ATTR_DESCRIPTION, target.getDescription());
        targetElem.setAttribute(TARGET_ATTR_ID, target.getTargetBuilderID());
        createTargetName(target, targetElem);
        createTargetCmdLineArgs(target, targetElem);
        return targetElem;
    }

    private void createTargetCmdLineArgs(SConsBuildTarget target, ICStorageElement targetElem) {
        String targetAttr = target.getAdditionalCmdLineArgs();

        if (targetAttr == null) {
            targetAttr = "";
        }

        ICStorageElement argElem = targetElem.createChild(TARGET_ARGUMENTS);
        argElem.setValue(targetAttr);
    }

    private void createTargetName(SConsBuildTarget target, ICStorageElement targetElem) {
        String targetName = target.getTargetName();

        if (targetName == null) throw new IllegalArgumentException("Target must have a name!");

        ICStorageElement nameElem = targetElem.createChild(TARGET_NAME);
        nameElem.setValue(targetName);
    }

    private ICStorageElement translateInputStreamToDocument(InputStream input) {
        try {
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(input);
            return XmlStorageUtil.createCStorageTree(document);
        } catch (Exception e) {
            SConsPlugin.log(e);
        }
        return null;
    }

    private void extractSConsTargetsFromDocument(ICStorageElement root) {
        for (ICStorageElement node : root.getChildren()) {
            if (!node.getName().equals(BUILD_TARGET_ELEMENT)) {
                continue;
            }

            for (ICStorageElement child : node.getChildren()) {
                node = child;
                if (node.getName().equals(TARGET_ELEMENT)) {
                    initTarget(node);
                }
            }
        }
    }

    private void initTarget(ICStorageElement node) {
        String targetName = getTargetName(node);
        String additionalCommandLineArgs = getCmdLineArgs(node);
        SConsBuildTarget target = createTarget(node, targetName, additionalCommandLineArgs);
        target.setContainer(project);
        add(target);
    }

    private SConsBuildTarget createTarget(ICStorageElement node, String targetName, String additionalCommandLineArgs) {
        return new SConsBuildTarget(targetName, project, node.getAttribute(TARGET_ATTR_ID), node.getAttribute(TARGET_ATTR_DESCRIPTION),
                additionalCommandLineArgs == null ? "" : additionalCommandLineArgs);
    }

    private String getTargetName(ICStorageElement node) {
        ICStorageElement[] option = node.getChildrenByName(TARGET_NAME);
        String targetName = "";

        if (option.length > 0) {
            targetName = option[0].getValue();
        }
        return targetName;
    }

    private String getCmdLineArgs(ICStorageElement node) {
        ICStorageElement[] option = node.getChildrenByName(TARGET_ARGUMENTS);
        String additionalCmdLineArgs = "";

        if (option.length > 0) {
            additionalCmdLineArgs = option[0].getValue();
        }
        return additionalCmdLineArgs;
    }

    private void initTargets(IProject project) {
        try {
            boolean writeTargets = false;
            File targetFile = null;
            ICStorageElement root = getProjectDescription().getProjectStorageElement(SCONS_TARGET_KEY);

            if (root.getChildren().length == 0) {
                targetFile = getTargetFilePath(project).toFile();

                try {
                    root.importChild(translateInputStreamToDocument(new FileInputStream(targetFile)));
                    writeTargets = true;
                } catch (FileNotFoundException e) {
                    // Ignore
                }
            }
            extractSConsTargetsFromDocument(root);

            if (writeTargets) {
                saveTargets();
                FileUtil.safelyDeleteFile(targetFile);
            }
        } catch (Exception e) {
            SConsPlugin.log(e);
        }
    }
}
