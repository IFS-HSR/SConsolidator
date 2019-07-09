package ch.hsr.ifs.sconsolidator.core.targets;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import ch.hsr.ifs.sconsolidator.core.targets.model.SConsBuildTarget;
import ch.hsr.ifs.sconsolidator.core.targets.model.SConsBuildTargetEvent;
import ch.hsr.ifs.sconsolidator.core.targets.model.SConsBuildTargetEvent.SConsBuildTargetEventType;


public class SConsBuildTargetEventTest {

    private SConsBuildTarget target;

    @Before
    public void before() {
        target = new SConsBuildTarget("CoastFoundationTest", null, "", "foundation tests", "--run-force");
    }

    @Test
    public void testCreate() {
        SConsBuildTargetEvent targetEvent = new SConsBuildTargetEvent(this, SConsBuildTargetEventType.PROJECT_ADDED, target);
        assertEquals(target, targetEvent.getTarget());
        assertEquals(SConsBuildTargetEventType.PROJECT_ADDED, targetEvent.getType());
    }
}
