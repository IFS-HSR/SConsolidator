package ch.hsr.ifs.sconsolidator.core.base.utils;

import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Test;


public class NullOutputStreamTest {

    private static final String SAMPLE_STRING = "allowed";

    @Test
    public void testWrite() throws IOException {
        NullOutputStream nos = new NullOutputStream();
        nos.write(SAMPLE_STRING.getBytes());
        nos.flush();
        nos.close();

        // a real OutputStream would throw an exception after close()
        try {
            nos.write(SAMPLE_STRING.getBytes());
            nos.write(0);
            nos.write(SAMPLE_STRING.getBytes(), 0, SAMPLE_STRING.length());
        } catch (IOException e) {
            fail("NullOutputStream should not throw exception even when writing after closing the stream");
        }
    }
}
