package ch.hsr.ifs.sconsolidator.core.base.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.Bundle;

import ch.hsr.ifs.sconsolidator.core.SConsPlugin;


public final class FileUtil {

    private FileUtil() {}

    public static void copyAndReplaceDerivedFile(IPath src, IFile dstPath) throws CoreException {
        InputStream is = null;
        try {
            is = FileLocator.openStream(getBundle(), src, false);
            copyAndReplaceDerivedFile(is, dstPath);
        } catch (IOException e) {
            throw new CoreException(new Status(IStatus.ERROR, SConsPlugin.PLUGIN_ID, e.getMessage()));
        } finally {
            IOUtil.safeClose(is);
        }
    }

    private static Bundle getBundle() {
        return SConsPlugin.getDefault().getBundle();
    }

    public static void copyAndReplaceDerivedFile(InputStream is, IFile dstPath) throws CoreException {
        NullProgressMonitor npm = new NullProgressMonitor();

        if (dstPath.exists()) {
            dstPath.setContents(is, true, true, npm);
        } else {
            dstPath.create(is, true, npm);
        }
        // all added SCons files can be fully regenerated, so I set them to 'derived'
        dstPath.setDerived(true, npm);
    }

    public static void copyBundleFile(IPath projectRelativePath, String outputPath) throws CoreException {
        InputStream is = null;
        FileOutputStream to = null;
        try {
            is = FileLocator.openStream(getBundle(), projectRelativePath, false);
            to = new FileOutputStream(outputPath);
            byte[] buffer = new byte[4096];
            int bytesRead;

            while ((bytesRead = is.read(buffer)) != -1) {
                to.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            throw new CoreException(new Status(IStatus.ERROR, SConsPlugin.PLUGIN_ID, e.getMessage()));
        } finally {
            IOUtil.safeClose(is);
            IOUtil.safeClose(to);
        }
    }

    public static void safelyDeleteFile(String filePath) {
        safelyDeleteFile(new File(filePath));
    }

    public static void safelyDeleteFile(File filePath) {
        if (filePath != null && filePath.exists()) {
            filePath.delete();
        }
    }
}
