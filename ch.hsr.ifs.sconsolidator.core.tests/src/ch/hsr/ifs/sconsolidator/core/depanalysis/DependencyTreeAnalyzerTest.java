package ch.hsr.ifs.sconsolidator.core.depanalysis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.List;

import org.junit.Test;

public class DependencyTreeAnalyzerTest {
  private static final String TREE_EXAMPLE = "+-CoastCompress\n"
      + "| +-coast/compress/.build/Linux_glibc_2.9-i686-32/config_compress.os\n"
      + "| | +-coast/compress/config_compress.cpp\n"
      + "| | +-include/CoastFoundation/base/InitFinisManager.h\n"
      + "| | | +-coast/foundation/base/InitFinisManager.h\n"
      + "| | +-coast/compress/config_compress.h\n"
      + "| | +-include/CoastFoundation/base/config_foundation.h\n"
      + "| | | +-coast/foundation/base/config_foundation.h\n" + "| | +-/usr/bin/g++\n"
      + "| +-[include/CoastFoundation/base/InitFinisManager.h]\n"
      + "| +-coast/compress/.build/Linux_glibc_2.9-i686-32/config_compress_ext.os\n"
      + "| | +-coast/foundation/base/config_foundation.h\n";

  @Test
  public void testEmptyTree() {
    DependencyTreeAnalyzer treeAnalyzer = new DependencyTreeAnalyzer("");
    Collection<DependencyTreeNode> tree = treeAnalyzer.collectDependencyTree();
    assertTrue(tree.isEmpty());
  }

  @Test
  public void testExampleTree() {
    DependencyTreeAnalyzer treeAnalyzer = new DependencyTreeAnalyzer(TREE_EXAMPLE);
    Collection<DependencyTreeNode> tree = treeAnalyzer.collectDependencyTree();
    assertEquals(1, tree.size());
    DependencyTreeNode root = tree.iterator().next();
    assertNull(root.getParent());
    assertEquals("CoastCompress", root.toString());
    List<DependencyTreeNode> children = root.getChildren();
    assertEquals(3, children.size());
    assertEquals("coast/compress/.build/Linux_glibc_2.9-i686-32/config_compress.os", children
        .get(0).toString());
    assertEquals(root, children.get(0).getParent());
    assertEquals("include/CoastFoundation/base/InitFinisManager.h", children.get(1).toString());
    assertEquals(root, children.get(1).getParent());

    root = children.get(0);
    children = root.getChildren();
    assertEquals(5, children.size());
    assertEquals("coast/compress/config_compress.cpp", children.get(0).toString());
    assertEquals(root, children.get(0).getParent());
    assertEquals("include/CoastFoundation/base/InitFinisManager.h", children.get(1).toString());
    assertEquals(root, children.get(1).getParent());
    assertEquals("coast/compress/config_compress.h", children.get(2).toString());
    assertEquals(root, children.get(2).getParent());
    assertEquals("include/CoastFoundation/base/config_foundation.h", children.get(3).toString());
    assertEquals(root, children.get(2).getParent());
    assertEquals("/usr/bin/g++", children.get(4).toString());
    assertEquals(root, children.get(4).getParent());
    assertTrue(children.get(0).getChildren().isEmpty());
    assertEquals(1, children.get(1).getChildren().size());
    assertEquals("coast/foundation/base/InitFinisManager.h", children.get(1).getChildren().get(0)
        .toString());
    assertEquals(children.get(1), children.get(1).getChildren().get(0).getParent());
    assertTrue(children.get(2).getChildren().isEmpty());
    assertEquals(1, children.get(3).getChildren().size());
    assertEquals("coast/foundation/base/config_foundation.h", children.get(3).getChildren().get(0)
        .toString());
    assertEquals(children.get(3), children.get(3).getChildren().get(0).getParent());
  }

  @Test
  public void testTargetsForWithUnkownTarget() {
    DependencyTreeAnalyzer treeAnalyzer = new DependencyTreeAnalyzer(TREE_EXAMPLE);
    assertEquals(0, treeAnalyzer.collectTargets("gugus/gaga/gonzo/schiri.cpp").size());
  }

  @Test
  public void testTargetsForSourceFile() {
    DependencyTreeAnalyzer treeAnalyzer = new DependencyTreeAnalyzer(TREE_EXAMPLE);
    Collection<String> targetsForSource =
        treeAnalyzer.collectTargets("coast/compress/config_compress.cpp");
    assertEquals(1, targetsForSource.size());
    assertEquals("coast/compress/.build/Linux_glibc_2.9-i686-32/config_compress.os",
        targetsForSource.iterator().next());
  }

  @Test
  public void testTargetsForHeaderFile() {
    DependencyTreeAnalyzer treeAnalyzer = new DependencyTreeAnalyzer(TREE_EXAMPLE);
    Collection<String> targetsForHeader =
        treeAnalyzer.collectTargets("coast/foundation/base/config_foundation.h");
    assertEquals(2, targetsForHeader.size());
    assertTrue(targetsForHeader
        .contains("coast/compress/.build/Linux_glibc_2.9-i686-32/config_compress.os"));
    assertTrue(targetsForHeader
        .contains("coast/compress/.build/Linux_glibc_2.9-i686-32/config_compress_ext.os"));
  }
}
