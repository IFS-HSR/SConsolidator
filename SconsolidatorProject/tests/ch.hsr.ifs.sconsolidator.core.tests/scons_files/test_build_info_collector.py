import unittest
import os
import subprocess
import shutil


def collect_infos(project_dir):
    
    def parse(output):
        d = {}
        for line in output:
            try:
                key, value = line.split('=', 1)
                d[key.strip()] = value.strip()
            except (KeyError, ValueError):
                pass # SCons output
        return d

    class cd:
        def __init__(self, new_path):
            self.new_path = new_path

        def __enter__(self):
            self.old_path = os.getcwd()
            os.chdir(self.new_path)

        def __exit__(self, etype, value, traceback):
            os.chdir(self.old_path)

    BUILD_INFO_COLLECTOR = 'BuildInfoCollector.py'
    extractor_path = os.path.abspath(os.path.join('../../ch.hsr.ifs.sconsolidator.core/scons_files', BUILD_INFO_COLLECTOR))

    with cd(project_dir):
        shutil.copy2(extractor_path, '.')
        p = (subprocess.Popen(['scons', '-u', '-s', '-f', 'SConstruct', '-f', BUILD_INFO_COLLECTOR], 
            stdout=subprocess.PIPE,
            stderr=subprocess.STDOUT))
        stdout, _ = p.communicate()
        os.remove(BUILD_INFO_COLLECTOR)
        return parse(stdout.splitlines())

 
class TestGartenBauBuild(unittest.TestCase):

    def checkEqual(self, l1, l2):
        return len(l1) == len(l2) and sorted(l1) == sorted(l2)

    def setUp(self):
        self.build_infos = collect_infos('test_projects/gartenbau')

    def test_includes_of_gartenbau_project(self):
        gartenbau = os.path.join(os.path.dirname(os.path.abspath(__file__)), 'test_projects', 'gartenbau')
        cute, shapes = os.path.join(gartenbau, 'cute'), os.path.join(gartenbau, 'src', 'shapes')
        self.checkEqual("['{cute}','{shapes}','/usr/include/xorg']".format(**vars()), self.build_infos['USER_INCLUDES'])

    def test_macros_of_gartenbau_project(self):
        self.checkEqual("['Foo=42','Bar=YES']", self.build_infos['MACROS'])


if __name__ == '__main__':
    unittest.main()

