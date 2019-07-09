package ch.hsr.ifs.sconsolidator.depviz.model;

import java.util.ArrayDeque;
import java.util.Deque;

import ch.hsr.ifs.sconsolidator.core.depanalysis.DependencyTreeNode;


// java.util.Stack is seriously flawed, so I use a java.util.Deque here for my purposes
class DependencyNodeStack {

    private final Deque<DependencyTreeNode> stack = new ArrayDeque<DependencyTreeNode>();

    public void push(final DependencyTreeNode node) {
        stack.addFirst(node);
    }

    public DependencyTreeNode pop() {
        return stack.removeFirst();
    }

    public void clear() {
        stack.clear();
    }

    public boolean isEmpty() {
        return stack.isEmpty();
    }
}
