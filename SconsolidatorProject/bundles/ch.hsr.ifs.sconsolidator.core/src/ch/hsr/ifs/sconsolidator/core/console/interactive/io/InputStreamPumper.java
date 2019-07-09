package ch.hsr.ifs.sconsolidator.core.console.interactive.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import ch.hsr.ifs.sconsolidator.core.SConsPlugin;


class InputStreamPumper implements Runnable {

    private final InputStream  is;
    private final OutputStream os;
    private volatile boolean   stop;

    public InputStreamPumper(InputStream is, OutputStream os) {
        this.is = is;
        this.os = os;
        this.stop = false;
    }

    public void stopProcessing() {
        stop = true;
    }

    @Override
    public void run() {
        try {
            byte[] data = new byte[1];

            while (!stop && is.read(data) != -1) {
                switch (data[0]) {
                case '\n':
                    os.write(data);
                    os.flush();
                    break;
                case '\r':
                    // ignore
                    break;
                default:
                    os.write(data);
                    break;
                }
            }
        } catch (IOException e) {
            SConsPlugin.log(e);
        }
    }
}
