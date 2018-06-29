package ch.hsr.ifs.sconsolidator.core.base.utils;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

public class TeeOutputStreamTest {
  private static final String TMP_FILE_NAME = "tmp.txt";
  private TeeOutputStream teeStream;
  private String sentence;
  private ByteArrayOutputStream ostream1;
  private ByteArrayOutputStream ostream2;

  @Before
  public void setUp() {
    sentence = "This is a test.";
    ostream1 = new ByteArrayOutputStream();
    ostream2 = new ByteArrayOutputStream();
    teeStream = new TeeOutputStream(ostream1, ostream2);
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
  public void testWrite() throws IOException {
    StringReader input = new StringReader(sentence);
    int c;
    while ((c = input.read()) != -1) {
      teeStream.write(c);
    }

    assertEquals(sentence, ostream1.toString());
    assertEquals(sentence, ostream2.toString());
  }

  @Test
  public void testWriteOffset() throws IOException {
    teeStream.write(sentence.getBytes(), 0, sentence.length());
    assertEquals(sentence, ostream1.toString());
    assertEquals(sentence, ostream2.toString());
  }

  @Test(expected = IOException.class)
  public void testClose() throws IOException {
    DataOutputStream oStream1 = new DataOutputStream(new FileOutputStream(TMP_FILE_NAME));
    DataOutputStream oStream2 = new DataOutputStream(new FileOutputStream(TMP_FILE_NAME));
    TeeOutputStream teeStream = new TeeOutputStream(oStream1, oStream2);
    teeStream.flush();
    teeStream.close();
    teeStream.write(0);
  }
}
