package ch.hsr.ifs.sconsolidator.core.base.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.AfterClass;
import org.junit.Test;


public class IOUtilTest {

    private static final String TMP_FILE_NAME = "tmp.txt";

    @AfterClass
    public static void tearDownAfterClass() {
        new File(TMP_FILE_NAME).delete();
    }

    @Test
    public void testSafeCloseWithNull() {
        try {
            IOUtil.safeClose(null);
        } catch (Exception e) {
            fail("Should not throw exception for null");
        }

        assertTrue(true);
    }

    @Test(expected = IOException.class)
    public void testSafeCloseWithReadAfterClose() throws IOException {
        DataOutputStream os = new DataOutputStream(new FileOutputStream(TMP_FILE_NAME));
        IOUtil.safeClose(os);
        os.write(0);
    }

    @Test
    public void testStringToStream() throws IOException {
        String text = "SCONS";
        InputStream stringToStream = IOUtil.stringToStream(text);
        StringBuilder sb = new StringBuilder();

        int c;
        while ((c = stringToStream.read()) != -1) {
            sb.append((char) c);
        }

        assertEquals(text, sb.toString());
    }
}
