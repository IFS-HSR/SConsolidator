#!/usr/bin/env python2
# -*- coding: utf-8 -*-

"""Emits the SCons dependency tree.

Usage:

scons_project/ $ scons -s -u -f SConstruct -f DepdendencyAnalyzer.py
"""

import SCons


def create_tree():
    cur_dir = SCons.Script.Dir('.')
    variant_dirs = cur_dir.variant_dirs
    node = variant_dirs[0] if len(variant_dirs) > 0 else cur_dir
    ascii_tree = SCons.Util.render_tree(node, lambda node: \
            node.all_children(), prune=1)
    return ascii_tree


def write_to_file(tree):
    with open('tree.txt', 'w') as f:
        f.write(tree)
        f.write('\n***FINISHED***')  # end marker


ascii_tree = create_tree()
write_to_file(ascii_tree)
