package ch.hsr.ifs.sconsolidator.core.commands;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;


public class SConsVersionTest {

    @Test
    public void testMissionVersionNumbers() {
        String versionMissingNumbers = "engine: 2010/08/16 23:02:40, by bdeegan on cooldog";

        try {
            new SConsVersion(versionMissingNumbers);
            fail("Should have thrown IllegalArgumentException but did not!");
        } catch (IllegalArgumentException e) {
            String expectedMsg = "Version string has wrong format";
            assertEquals(expectedMsg, e.getMessage());
        }
    }

    @Test
    public void testMissingEnginePart() {
        String versionMissingEnginePart = "SCons by Steven Knight et al.:\nscript: v2.0.1.r5134," +
                                          " 2010/08/16 23:02:40, by bdeegan on cooldog\nCopyright (c) 2001, 2002, 2003, 2004," +
                                          " 2005, 2006, 2007, 2008, 2009, 2010 The SCons Foundation";

        try {
            new SConsVersion(versionMissingEnginePart);
            fail("Should have thrown IllegalArgumentException but did not!");
        } catch (IllegalArgumentException e) {
            String expectedMsg = "Version string has wrong format";
            assertEquals(expectedMsg, e.getMessage());
        }
    }

    @Test
    public void testCorrectVersionNumbers() {
        String correctVersionNumbers = "SCons by Steven Knight et al.:\n" + "script: v2.0.1.r5134, 2010/08/16 23:02:40, by bdeegan on cooldog\n" +
                                       "engine: v2.0.1.r5134, 2010/08/16 23:02:40, by bdeegan on cooldog\n" +
                                       "Copyright (c) 2001, 2002, 2003, 2004, 2005, 2006, 2007, 2008, 2009, 2010 The SCons Foundation";

        SConsVersion version = null;
        try {
            version = new SConsVersion(correctVersionNumbers);
        } catch (IllegalArgumentException e) {
            fail("Should not have thrown IllegalArgumentException but did!");
        }

        assertNotNull(version);
        assertEquals(2, version.getMajorVersion());
        assertEquals(0, version.getMinorVersion());
        assertEquals(1, version.getRevision());
    }

    @Test
    public void testIsGreaterThan() {
        SConsVersion versionMajorOne = new SConsVersion("engine: v1.0.1.r5134, 2010/08/16 23:02:40, by bdeegan on cooldog");
        SConsVersion versionMajorTwo = new SConsVersion("engine: v2.0.1.r5134, 2010/08/16 23:02:40, by bdeegan on cooldog");
        SConsVersion versionMinorOne = new SConsVersion("engine: v2.1.1.r5134, 2010/08/16 23:02:40, by bdeegan on cooldog");
        SConsVersion versionMinorTwo = new SConsVersion("engine: v2.2.1.r5134, 2010/08/16 23:02:40, by bdeegan on cooldog");
        SConsVersion versionRevisionOne = new SConsVersion("engine: v2.2.1.r5134, 2010/08/16 23:02:40, by bdeegan on cooldog");
        SConsVersion versionRevisionTwo = new SConsVersion("engine: v2.2.2.r5134, 2010/08/16 23:02:40, by bdeegan on cooldog");

        assertTrue(versionMajorTwo.isGreaterThan(versionMajorOne));
        assertFalse(versionMajorOne.isGreaterThan(versionMajorTwo));
        assertTrue(versionMinorTwo.isGreaterThan(versionMinorOne));
        assertFalse(versionMinorOne.isGreaterThan(versionMinorTwo));
        assertTrue(versionRevisionTwo.isGreaterThan(versionRevisionOne));
        assertFalse(versionRevisionOne.isGreaterThan(versionRevisionTwo));
    }

    @Test
    public void testIsGreaterOrEqual() {
        SConsVersion versionEqualOne = new SConsVersion("engine: v2.0.1.r5134, 2010/08/16 23:02:40, by bdeegan on cooldog");
        SConsVersion versionEqualTwo = new SConsVersion("engine: v2.0.1.r5134, 2010/08/16 23:02:40, by bdeegan on cooldog");
        SConsVersion versionRevisionOne = new SConsVersion("engine: v2.2.1.r5134, 2010/08/16 23:02:40, by bdeegan on cooldog");
        SConsVersion versionRevisionTwo = new SConsVersion("engine: v2.2.2.r5134, 2010/08/16 23:02:40, by bdeegan on cooldog");

        assertTrue(versionEqualOne.isGreaterOrEqual(versionEqualTwo));
        assertTrue(versionEqualTwo.isGreaterOrEqual(versionEqualOne));
        assertFalse(versionRevisionOne.isGreaterOrEqual(versionRevisionTwo));
        assertTrue(versionRevisionTwo.isGreaterOrEqual(versionRevisionOne));
    }

    @Test
    public void testIsCompatible() {
        SConsVersion versionZero = new SConsVersion("engine: v0.9.8.r5134, 2010/08/16 23:02:40, by bdeegan on cooldog");
        SConsVersion versionTwo = new SConsVersion("engine: v2.2.1.r5134, 2010/08/16 23:02:40, by bdeegan on cooldog");

        assertFalse(versionZero.isCompatible());
        assertTrue(versionTwo.isCompatible());
    }
}
