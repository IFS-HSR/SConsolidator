package ch.hsr.ifs.sconsolidator.core;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import ch.hsr.ifs.sconsolidator.core.base.functional.FunctionalHelperTest;
import ch.hsr.ifs.sconsolidator.core.base.tuple.TupleTest;
import ch.hsr.ifs.sconsolidator.core.base.utils.CollectionUtilTest;
import ch.hsr.ifs.sconsolidator.core.base.utils.IOUtilTest;
import ch.hsr.ifs.sconsolidator.core.base.utils.NatureUtilTest;
import ch.hsr.ifs.sconsolidator.core.base.utils.NullOutputStreamTest;
import ch.hsr.ifs.sconsolidator.core.base.utils.PythonUtilTest;
import ch.hsr.ifs.sconsolidator.core.base.utils.StringUtilTest;
import ch.hsr.ifs.sconsolidator.core.base.utils.TeeInputStreamTest;
import ch.hsr.ifs.sconsolidator.core.base.utils.TeeOutputStreamTest;
import ch.hsr.ifs.sconsolidator.core.commands.BuildCommandTest;
import ch.hsr.ifs.sconsolidator.core.commands.BuildInfoCollectorCommandTest;
import ch.hsr.ifs.sconsolidator.core.commands.CleanCommandTest;
import ch.hsr.ifs.sconsolidator.core.commands.DependencyTreeCommandTest;
import ch.hsr.ifs.sconsolidator.core.commands.SConsVersionTest;
import ch.hsr.ifs.sconsolidator.core.commands.VersionCommandTest;
import ch.hsr.ifs.sconsolidator.core.depanalysis.DependencyTreeAnalyzerTest;
import ch.hsr.ifs.sconsolidator.core.existingcode.SConsExistingCodeNatureTest;
import ch.hsr.ifs.sconsolidator.core.managed.ProjectSettingsReaderTest;
import ch.hsr.ifs.sconsolidator.core.managed.ProjectSettingsWriterTest;
import ch.hsr.ifs.sconsolidator.core.managed.SConsManagedNatureTest;
import ch.hsr.ifs.sconsolidator.core.preferences.SConsOptionHandlerTest;
import ch.hsr.ifs.sconsolidator.core.targets.PersistentTargetsHandlerTest;
import ch.hsr.ifs.sconsolidator.core.targets.SConsBuildTargetEventTest;
import ch.hsr.ifs.sconsolidator.core.targets.SConsBuildTargetManagerTest;
import ch.hsr.ifs.sconsolidator.core.targets.SConsBuildTargetTest;


@RunWith(Suite.class)
@Suite.SuiteClasses({ //
                      SConsPluginTest.class, // 
                      SConsVersionTest.class, //
                      PythonUtilTest.class, //
                      StringUtilTest.class, //
                      IOUtilTest.class, //
                      TupleTest.class, //
                      SConsHelperTest.class, //
                      NatureUtilTest.class, //
                      TeeInputStreamTest.class, //
                      NullOutputStreamTest.class, //
                      TeeOutputStreamTest.class, //
                      CppManagedTestProjectTest.class, //
                      ProjectSettingsWriterTest.class, //
                      ProjectSettingsReaderTest.class, //
                      DependencyTreeAnalyzerTest.class, //
                      BuildCommandTest.class, //
                      CleanCommandTest.class, //
                      DependencyTreeCommandTest.class, //
                      BuildInfoCollectorCommandTest.class, //
                      VersionCommandTest.class, //
                      PersistentTargetsHandlerTest.class, //
                      SConsBuildTargetEventTest.class, //
                      SConsBuildTargetManagerTest.class, //
                      SConsBuildTargetTest.class, //
                      SConsTwoStepBuildTest.class, //
                      CollectionUtilTest.class, //
                      FunctionalHelperTest.class, //
                      SConsOptionHandlerTest.class, //
                      SConsManagedNatureTest.class, //
                      SConsExistingCodeNatureTest.class, //
                      SConsBuilderTest.class //
})
public class PluginUITestSuiteAll {}
