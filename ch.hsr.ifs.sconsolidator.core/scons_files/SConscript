# SConscript
import os
import re
import glob
import sys


def _build_source_list(directory, patterns):
    """Returns a list of all source files below the given directory."""

    def glob_to_regex(pattern, dirsep=os.sep):
        dirsep = re.escape(dirsep)
        ant_regex = (re.escape(pattern)
                 .replace("\\*\\*" + dirsep, ".*")
                 .replace("\\*\\*", ".*")
                 .replace("\\*", "[^%s]*" % dirsep)
                 .replace("\\?", "[^%s]" % dirsep))
        return re.compile(ant_regex + "$")

    followlinks = True if sys.version_info[:2] >= (2, 6) else False
    source_paths_to_exclude = set()
    all_source_paths = set()
    re_c_cpp_file = re.compile('^.*\.(cpp|cxx|c\+\+|txx|C|c|cc)$')

    for dirpath, dirnames, filenames in os.walk(directory, followlinks=True):
        dirnames[:] = [dir for dir in dirnames if not dir in patterns]
        [all_source_paths.add(os.path.normpath(os.path.join(os.path.relpath(dirpath, directory), filename)))
            for filename in filenames if re_c_cpp_file.match(filename)]

    for p in patterns:
        pat = p.split(os.sep)
        ant_regex = glob_to_regex(p) if pat[0] == "**" else None

        for dirpath, dirnames, filenames in os.walk(directory, followlinks=True):
            dir = os.path.relpath(directory, dirpath)
            level = 0 if dir == '.' else len(dir.split(os.sep))
            recon_pat = os.path.normpath(os.path.join(".", os.sep.join(pat[:-1])))

            if ant_regex:
                for filename in filenames:
                    if ant_regex.match(os.path.join(dirpath, filename)):
                        source_paths_to_exclude.add(os.path.normpath(os.path.join(os.path.relpath(dirpath, directory), filename)))
            elif recon_pat == dir:
                tmp = [os.path.relpath(result, directory) for result in glob.glob(os.path.join(dirpath, pat[level]))]
                source_paths_to_exclude |= set(tmp)

    return list(all_source_paths.difference(source_paths_to_exclude))


Import('env', 'source_dir', 'excludes', 'pic')

src_files = _build_source_list(source_dir, excludes)

if pic:
    obj = env.SharedObject(src_files)
else:
    obj = env.Object(src_files)

Return('obj')
