package ch.hsr.ifs.sconsolidator.core.targets.model;

import java.util.EventObject;


public class SConsBuildTargetEvent extends EventObject {

    private static final long serialVersionUID = -9010438339167161385L;

    public enum SConsBuildTargetEventType {
        TARGET_ADDED, TARGET_CHANGED, TARGET_REMOVED, PROJECT_ADDED, PROJECT_REMOVED;
    }

    private final SConsBuildTargetEventType type;
    private transient SConsBuildTarget      target;

    public SConsBuildTargetEvent(Object source, SConsBuildTargetEventType type, SConsBuildTarget target) {
        super(source);
        this.type = type;
        this.target = target;
    }

    public SConsBuildTargetEvent(Object source, SConsBuildTargetEventType type) {
        this(source, type, null);
    }

    public SConsBuildTargetEventType getType() {
        return type;
    }

    public SConsBuildTarget getTarget() {
        return target;
    }
}
