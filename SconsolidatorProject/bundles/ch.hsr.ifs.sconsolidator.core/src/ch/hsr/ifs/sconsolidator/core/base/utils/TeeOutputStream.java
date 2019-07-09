package ch.hsr.ifs.sconsolidator.core.base.utils;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;


public class TeeOutputStream extends FilterOutputStream {

    private final OutputStream stream1;
    private final OutputStream stream2;

    public TeeOutputStream(OutputStream stream1, OutputStream stream2) {
        super(stream1);
        this.stream1 = stream1;
        this.stream2 = stream2;
    }

    @Override
    public void write(int b) throws IOException {
        stream1.write(b);
        stream2.write(b);
    }

    @Override
    public void write(byte[] data, int offset, int length) throws IOException {
        stream1.write(data, offset, length);
        stream2.write(data, offset, length);
    }

    @Override
    public void flush() throws IOException {
        stream1.flush();
        stream2.flush();
    }

    @Override
    public void close() throws IOException {
        stream1.close();
        stream2.close();
    }
}
