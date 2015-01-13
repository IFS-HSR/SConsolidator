package ch.hsr.ifs.sconsolidator.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.regex.Matcher;

import org.junit.Test;

public class PlatformSpecificsTest {

  @Test
  public void compilerErrorPatternMatchWithLinuxPath() {
    Matcher m = PlatformSpecifics.CPP_RE.matcher("src/main.cpp(23)");
    assertTrue(m.find());
    assertEquals("src/main.cpp", m.group(1));
    assertEquals("23", m.group(2));
  }
  
  @Test
  public void gccErrorPatternMatchWithLinuxPath() {
    Matcher m = PlatformSpecifics.CPP_RE.matcher("src/shapes/Circle.cpp:6:28: error: ISO C++ forbids declaration of 'Circleasdf' with no type [-fpermissive]");
    assertTrue(m.find());
    assertEquals("src/shapes/Circle.cpp", m.group(1));
    assertEquals("6", m.group(2));
  }
  
  @Test
  public void gccErrorPatternMatchWithLinuxPathAndWhitespace() {
    Matcher m = PlatformSpecifics.CPP_RE.matcher("src/shapes/test with whitespace.h:3:1: error: 'vosdfid' does not name a type");
    assertTrue(m.find());
    assertEquals("src/shapes/test with whitespace.h", m.group(1));
    assertEquals("3", m.group(2));
  }
  
  @Test
  public void compilerErrorPatternMatchWithWindowsPath() {
    Matcher m = PlatformSpecifics.CPP_RE.matcher("platform\\windows\\bcSupport.cpp(48): error C2065: 'ipDes' : undeclared identifier");
    assertTrue(m.find());
    assertEquals("platform\\windows\\bcSupport.cpp", m.group(1));
    assertEquals("48", m.group(2));
  }
  
  @Test
  public void bug24HyperlinkingIssueWithGreedyRegexQuatifier() {
    Matcher m = PlatformSpecifics.CPP_RE.matcher("ioengine\\DiskManager\\dmDisk.cpp(50) : fatal error C1083: Cannot open include file: 'config/access/caAttrBool.h2");
    assertTrue(m.find());
    assertEquals("ioengine\\DiskManager\\dmDisk.cpp", m.group(1));
    assertEquals("50", m.group(2));
  }
  
}
