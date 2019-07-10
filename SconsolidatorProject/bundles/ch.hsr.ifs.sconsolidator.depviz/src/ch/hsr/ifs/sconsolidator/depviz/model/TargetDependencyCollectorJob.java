package ch.hsr.ifs.sconsolidator.depviz.model;

import static ch.hsr.ifs.sconsolidator.core.base.tuple.Tuple._1;
import static ch.hsr.ifs.sconsolidator.core.base.tuple.Tuple._2;
import static ch.hsr.ifs.sconsolidator.core.base.tuple.Tuple._3;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import ch.hsr.ifs.sconsolidator.core.EmptySConsPathException;
import ch.hsr.ifs.sconsolidator.core.SConsPlugin;
import ch.hsr.ifs.sconsolidator.core.base.functional.VoidFunction;
import ch.hsr.ifs.sconsolidator.core.base.tuple.Triple;
import ch.hsr.ifs.sconsolidator.core.commands.DependencyTreeCommand;
import ch.hsr.ifs.sconsolidator.core.console.NullConsole;
import ch.hsr.ifs.sconsolidator.core.depanalysis.DependencyTreeAnalyzer;
import ch.hsr.ifs.sconsolidator.core.depanalysis.DependencyTreeNode;
import ch.hsr.ifs.sconsolidator.depviz.DependencyVisualization18N;


public class TargetDependencyCollectorJob extends Job {

    private final Triple<String, String, IProject>             targetInfos;
    private final VoidFunction<Collection<DependencyTreeNode>> callback;

    public TargetDependencyCollectorJob(Triple<String, String, IProject> targetInfos, VoidFunction<Collection<DependencyTreeNode>> callback) {
        super(DependencyVisualization18N.DepTreeVizView_SearchingDependenciesInProgress);
        this.targetInfos = targetInfos;
        this.callback = callback;
    }

    @Override
    protected IStatus run(IProgressMonitor pm) {
        pm.beginTask(DependencyVisualization18N.DepTreeVizView_SearchingDependenciesInProgress, 3);

        try {
            String asciiTree = collectDependencies(targetInfos);

            if (pm.isCanceled()) return Status.CANCEL_STATUS;

            pm.worked(1);
            Collection<DependencyTreeNode> tree = analyze(asciiTree);
            pm.worked(1);
            callback.apply(tree);
        } catch (Exception e) {
            SConsPlugin.showExceptionInDisplayThread(e);
        } finally {
            pm.done();
        }
        return Status.OK_STATUS;
    }

    private Collection<DependencyTreeNode> analyze(String asciiTree) {
        DependencyTreeAnalyzer analyzer = new DependencyTreeAnalyzer(asciiTree);
        return analyzer.collectDependencyTree();
    }

    private String collectDependencies(Triple<String, String, IProject> targetInfos) throws EmptySConsPathException, IOException,
            InterruptedException {
        DependencyTreeCommand command = new DependencyTreeCommand(new NullConsole(), _3(targetInfos), _2(targetInfos));
        return command.run(new File(_1(targetInfos)));
    }
}
