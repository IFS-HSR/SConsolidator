package ch.hsr.ifs.sconsolidator.core.depanalysis;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ch.hsr.ifs.sconsolidator.core.PlatformSpecifics;
import ch.hsr.ifs.sconsolidator.core.base.utils.IOUtil;

// Example of a dependency tree SCons emits:
// +-.
// +-SConstruct
// +-libgartenbau.a
// | +-src/shapes/Circle.o
// | | +-src/shapes/Circle.cpp
// | | +-src/shapes/Circle.h
// | | +-src/shapes/Shape.h
// | | +-/usr/bin/g++
// | +-src/shapes/Diamond.o
// | | +-src/shapes/Diamond.cpp
// | | +-src/shapes/Diamond.h
// | | +-[src/shapes/Shape.h]
// | | +-[/usr/bin/g++]
// | +-src/shapes/Ellipse.o
// | | +-src/shapes/Ellipse.cpp
// | | +-src/shapes/Ellipse.h
// | | +-[src/shapes/Shape.h]
// | | +-[/usr/bin/g++]
// | +-src/shapes/Rectangle.o
// | | +-src/shapes/Rectangle.cpp
// | | +-src/shapes/Rectangle.h
// | | +-[src/shapes/Shape.h]
// | | +-[/usr/bin/g++]
// | +-src/shapes/Shape.o
// | | +-src/shapes/Shape.cpp
// | | +-[src/shapes/Shape.h]
// | | +-[/usr/bin/g++]
// | +-src/shapes/Square.o
// | | +-src/shapes/Square.cpp
// | | +-src/shapes/Square.h
// | | +-[src/shapes/Shape.h]
// | | +-[/usr/bin/g++]
// | +-src/shapes/Triangle.o
// | | +-src/shapes/Triangle.cpp
// | | +-src/shapes/Triangle.h
// | | +-[src/shapes/Shape.h]
// | | +-[/usr/bin/g++]
// | +-/usr/bin/ar
// | +-/usr/bin/ranlib
public class DependencyTreeAnalyzer {
  private static final Pattern L1_RE = Pattern.compile("^\\+-[\\[]?(\\S+?)[\\]]?");
  private static final Pattern LN_RE = Pattern.compile("^(\\s*(\\| +)*)\\+-[\\[]?(\\S+?)[\\]]?");
  private final String asciiTree;

  public DependencyTreeAnalyzer(String asciiTree) {
    this.asciiTree = asciiTree;
  }

  public Collection<String> collectTargets(String sourcePath) {
    List<DependencyTreeNode> dependencyTree = createDependencyTree();

    if (dependencyTree.isEmpty())
      return Collections.emptyList();

    Collection<DependencyTreeNode> nodes = search(sourcePath, dependencyTree);
    return Collections.unmodifiableCollection(nodesToValues(nodes));
  }

  public Collection<DependencyTreeNode> collectDependencyTree() {
    return Collections.unmodifiableCollection(createDependencyTree());
  }

  private Collection<DependencyTreeNode> search(String sourcePath,
      List<DependencyTreeNode> dependencyTree) {
    Queue<DependencyTreeNode> queue = new LinkedList<DependencyTreeNode>();
    List<DependencyTreeNode> foundSrcNodes = new LinkedList<DependencyTreeNode>();
    queue.add(dependencyTree.get(0));

    while (!queue.isEmpty()) {
      DependencyTreeNode n = queue.remove();

      for (DependencyTreeNode child : n.getChildren()) {
        queue.add(child);

        if (child.getValue().equals(sourcePath)) {
          foundSrcNodes.add(child);
        }
      }
    }

    return findRelatedObjectFiles(foundSrcNodes);
  }

  private Collection<DependencyTreeNode> findRelatedObjectFiles(
      List<DependencyTreeNode> foundSrcNodes) {
    List<DependencyTreeNode> relatedObjectNodes = new LinkedList<DependencyTreeNode>();

    for (DependencyTreeNode node : foundSrcNodes) {
      while (node != null) {
        if (PlatformSpecifics.isObjectFile(node.getValue())) {
          relatedObjectNodes.add(node);
          break;
        }
        node = node.getParent();
      }
    }

    return relatedObjectNodes;
  }

  private Collection<String> nodesToValues(Collection<DependencyTreeNode> nodes) {
    List<String> relatedObjectNodes = new LinkedList<String>();

    for (DependencyTreeNode n : nodes) {
      relatedObjectNodes.add(n.getValue());
    }

    return relatedObjectNodes;
  }

  private List<DependencyTreeNode> createDependencyTree() {
    List<DependencyTreeNode> topLevel = new ArrayList<DependencyTreeNode>();
    List<DependencyTreeNode> currentStack = new ArrayList<DependencyTreeNode>();
    BufferedReader reader = new BufferedReader(new StringReader(asciiTree));

    try {
      String line;
      while ((line = reader.readLine()) != null) {
        Matcher l1m = L1_RE.matcher(line);
        Matcher lnm = LN_RE.matcher(line);

        if (l1m.matches()) {
          DependencyTreeNode node = makeNode(l1m.group(1));
          currentStack.clear();
          currentStack.add(node);
          topLevel.add(node);
        } else if (lnm.matches()) {
          int level = (lnm.group(1).length() / 2) + 1;
          DependencyTreeNode node = makeNode(lnm.group(3));

          if (currentStack.size() > level - 1) {
            for (int i = currentStack.size() - 1; i > level - 2; i--) {
              currentStack.remove(i);
            }
          }

          currentStack.add(node);
          DependencyTreeNode parent = currentStack.get(level - 2);
          parent.getChildren().add(node);
          node.setParent(parent);
        }
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    } finally {
      IOUtil.safeClose(reader);
    }

    return topLevel;
  }

  private DependencyTreeNode makeNode(String name) {
    return new DependencyTreeNode(name);
  }
}
