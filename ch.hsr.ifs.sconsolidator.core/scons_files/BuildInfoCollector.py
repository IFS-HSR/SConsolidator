#!/usr/bin/env python2
# -*- coding: utf-8 -*-

"""Collects all user and system (i.e., compiler) includes and macros from
a given SCons project.

Usage:

scons_project/ $ scons -s -u -f SConstruct -f BuildInfoCollector.py
"""

from __future__ import print_function

import SCons
import os
import platform
import re
import subprocess
import sys

from contextlib import contextmanager

def collect_user_includes(environ):
    # '#/a/b/c' and '#a/b/c' both need to be 'a/b/c'
    re_sconstruct_dir = re.compile(r'(#[%s]?)?(.*)' % re.escape(os.path.sep))

    def normalize(path):
        sconstruct_dir = re_sconstruct_dir.split(path)
        return path if len(sconstruct_dir) < 3 else os.path.abspath(sconstruct_dir[2])

    def add(includes, path):
        if isinstance(path, str):
            new_path = normalize(environ.subst(path))
            if os.path.isdir(new_path):
                includes.add(new_path)
        elif isinstance(path, SCons.Node.FS.Dir):
            includes.add(normalize(path.srcnode().abspath))
        elif isinstance(path, list):
            for new_path in path:
                add(includes, new_path)

    user_includes = set()
    for path in environ['CPPPATH']:
        add(user_includes, path)
    return user_includes


def get_gcc_lang_param(lang):
    return ('-xc++' if lang == 'c++' else '-xc')


def collect_sys_includes(lang, environ):

    def write_cpp_main(f, lang):
        f.write('#include <%s>\n#ifdef __cplusplus\nextern "C"\n#endif\nvoid _exit(int status) { while(1); }\nint main(){}' % 
            (('cstdlib' if lang == 'c++' else 'stdlib.h')))
        f.flush()

    @contextmanager
    def temp_file(path, mode=None):
        if mode:
            f = open(path, mode)
        try:
            yield f if mode else path
        finally:
            if mode:
                f.close()
            if os.path.exists(path):
                os.remove(path)

    def invoke_compiler(lang, environ):
        cur_dir = SCons.Script.Dir('.').abspath
        in_path, out_path = os.path.join(cur_dir, '.in'), os.path.join(cur_dir, '.a.out')

        with temp_file(in_path, 'w') as in_file:
            with temp_file(out_path):
                write_cpp_main(in_file, lang)
                process = SCons.Action._subproc(environ,
                    [get_compiler(environ), '-v', get_gcc_lang_param(lang)] + get_compiler_flags(lang, environ) + [in_path, '-o', out_path],
                    stdin='devnull', stderr=subprocess.PIPE, stdout=subprocess.PIPE)
                (_, perr) = process.communicate()
                try:
                    return perr.decode()
                except AttributeError:
                    return perr

    def parse_includes(cc_output):
        cc_output = cc_output.replace('\r', '')
        re_incl = re.compile('#include <\.\.\.>.*:$\s((^ .*\s)*)', re.M)
        match = re_incl.search(cc_output)
        sysincludes = set()
        if match:
            for it in re.finditer('^ (.*)$', match.group(1), re.M):
                sysincludes.add(os.path.normpath(it.groups()[0]))
        return sysincludes

    cc_output = invoke_compiler(lang, environ)
    return parse_includes(cc_output)


def get_compiler(environ):

    def which(program):
        def is_exe(fpath):
            return os.path.isfile(fpath) and os.access(fpath, os.X_OK)

        fpath, fname = os.path.split(program)
        if fpath:
            if is_exe(program):
                return program
        else:
            for path in os.environ['PATH'].split(os.pathsep):
                path = path.strip('"')
                exe_file = os.path.join(path, program)
                if is_exe(exe_file):
                    return exe_file
        return None

    if environ['PLATFORM'] == 'win32' and environ['CXX'] == 'g++' and platform.system().casefold().startswith('cygwin'):
        # because gcc and g++ are symlinks in cygwin that are only usable from the cygwin
        # console, we need to take the 'real' executables here
        return which('g++-4') or which('g++-3')
    else:
        return environ['CXX'] 


def collect_macros_from_cpp_defines(environ):
    
    def macro_binding(macro, value):
        return '{macro}={value}'.format(**locals())

    def handle_dict(d):
        try:
            return set(macro_binding(k, v) for (k, v) in d.iteritems())
        except AttributeError:
            return set(macro_binding(k, v) for (k, v) in d.items())

    cpp_defines = environ['CPPDEFINES']

    if isinstance(cpp_defines, (list, tuple)):
        macros = set()
        for m in cpp_defines:
            if isinstance(m, dict):
                macros.update(handle_dict(m))
            elif isinstance(m, (list, tuple)) and len(m) > 0:
                macros.add(macro_binding(m[0], m[1]) if len(m) > 1 else m[0])
            else:
                macros.add(m)
        return macros
    elif isinstance(cpp_defines, dict):
        return handle_dict(cpp_defines)


def collect_macros_from_cc_flags(environ):
    return set(flag[2:] for flag in environ['CCFLAGS'] 
            if flag is not None and isinstance(flag, str) and flag.startswith('-D'))


def get_compiler_flags(lang, environ):
    return (environ['CXXFLAGS'] if lang == 'c++' else environ['CCFLAGS'])


def collect_sys_macros(lang, environ):
    command = [get_compiler(environ), '-E', '-dM', get_gcc_lang_param(lang)] + get_compiler_flags(lang, environ) + ['-']
    process = SCons.Action._subproc(environ, command, stdin='devnull', stderr=subprocess.PIPE, stdout=subprocess.PIPE)
    (pout, _) = process.communicate()
    sysmacros = set()

    try:
        pout = pout.decode()
    except AttributeError:
        pass
    for it in re.finditer('^#define (.*) (.*)$', pout, re.M):
        sysmacros.add('%s=%s' % (it.groups()[0], it.groups()[1].strip()))
    return sysmacros


def has_build_targets():
    return len(SCons.Script.BUILD_TARGETS) > 0


def get_start_nodes():
    return (SCons.Script.Alias(SCons.Script.BUILD_TARGETS)
            if has_build_targets() else [SCons.Script.Dir('.')])


def write_build_infos(includes, macros, environ):

    def to_string(objects, environ):
        strings = []
        for obj in objects:
            if isinstance(obj, tuple):
                strings.append("'%s'" % '='.join(obj))
            else:
                strings.append("'%s'" % obj)
        return ','.join([environ.subst(string) for string in strings])

    print('USER_INCLUDES = [%s]' % to_string(includes, environ))
    print('SYS_C_INCLUDES = [%s]' % to_string(collect_sys_includes('cc', environ), environ))
    print('SYS_CPP_INCLUDES = [%s]' % to_string(collect_sys_includes('c++', environ), environ))
    print('MACROS = [%s]' % to_string(macros, environ))
    print('SYS_C_MACROS = [%s]' % to_string(collect_sys_macros('cc', environ), environ))
    print('SYS_CPP_MACROS = [%s]' % to_string(collect_sys_macros('c++', environ), environ))


def collect_build_infos(super_nodes):

    def no_scan_fun(node, _):
        return node.children(scan=0)

    children_fun = (no_scan_fun if has_build_targets() else SCons.Node.get_children)
    includes, macros = set(), set()
    compiler_env = None

    for snode in super_nodes:
        walker = SCons.Node.Walker(snode, kids_func=children_fun)
        node = walker.get_next()
        while node:
            if node.has_builder():
                environ = node.get_build_env()

                if 'CPPPATH' in environ:
                    includes.update(collect_user_includes(environ))
                if 'CPPDEFINES' in environ:
                    macros.update(collect_macros_from_cpp_defines(environ))
                if 'CCFLAGS' in environ:
                    macros.update(collect_macros_from_cc_flags(environ))
                if not compiler_env and ('CC' in environ or 'CXX' in environ):
                    compiler_env = environ
            node = walker.get_next()

    return (includes, macros, compiler_env)


(includes, macros, env) = collect_build_infos(get_start_nodes())
write_build_infos(includes, macros, env)
sys.exit()

