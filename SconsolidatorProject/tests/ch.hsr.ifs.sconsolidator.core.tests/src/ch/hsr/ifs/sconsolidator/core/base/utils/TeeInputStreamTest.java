package ch.hsr.ifs.sconsolidator.core.base.utils;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

public class TeeInputStreamTest {
  private static final String TMP_FILE_NAME = "tmp.txt";
  private TeeInputStream teeStream;
  private String sentence;
  private ByteArrayInputStream iStream;
  private ByteArrayOutputStream oStream;

  @Before
  public void setUp() {
    sentence = "This is a test.";
    iStream = (ByteArrayInputStream) IOUtil.stringToStream(sentence);
    oStream = new ByteArrayOutputStream();
    teeStream = new TeeInputStream(iStream, oStream);
  }

  @After
  public void tearDown() throws IOException {
    teeStream.close();
  }

  @AfterClass
  public static void tearDownAfterClass() {
    new File(TMP_FILE_NAME).delete();
  }

  @Test
  public void testRead() throws IOException {
    @SuppressWarnings("unused")
    int c;
    while ((c = teeStream.read()) != -1) {
      ;
    }

    assertEquals(sentence, oStream.toString());
  }

  @Test
  public void testReadAllBytes() throws IOException {
    byte[] bytesRead = new byte[sentence.length()];
    teeStream.read(bytesRead);
    assertEquals(sentence, new String(bytesRead));
  }

  @Test
  public void testReadByteRange() throws IOException {
    byte[] bytesRead = new byte[sentence.length()];
    teeStream.read(bytesRead, 0, sentence.length());
    assertEquals(sentence, new String(bytesRead));
  }

  @Test(expected = IOException.class)
  public void testClose() throws IOException {
    DataInputStream iStream = new DataInputStream(new FileInputStream(TMP_FILE_NAME));
    ByteArrayOutputStream oStream = new ByteArrayOutputStream();
    TeeInputStream tee = new TeeInputStream(iStream, oStream);
    tee.close();
    iStream.read();
  }
}
