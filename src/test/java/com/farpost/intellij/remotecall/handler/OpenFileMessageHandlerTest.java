package com.farpost.intellij.remotecall.handler;

import com.farpost.intellij.remotecall.utils.FileNavigator;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class OpenFileMessageHandlerTest {

  private static OpenFileMessageHandler handler;
  private static StubFileNavigator fileNavigator;

  @BeforeClass
  public static void setUp() {
    fileNavigator = new StubFileNavigator();
    handler = new OpenFileMessageHandler(fileNavigator);
  }

  @Test
  public void handlerShouldExtractFilenameAndLineFromMessage() {
    handler.handleMessage("FileName.java:80");
    assertEquals("FileName.java", fileNavigator.getFileName());
    assertEquals(79, fileNavigator.getLine());
    assertEquals(0, fileNavigator.getColumn());

    handler.handleMessage("FileName.java");
    assertEquals("FileName.java", fileNavigator.getFileName());
    assertEquals(0, fileNavigator.getLine());
    assertEquals(0, fileNavigator.getColumn());

    handler.handleMessage("FileName.java:error");
    assertEquals("FileName.java:error", fileNavigator.getFileName());
    assertEquals(0, fileNavigator.getLine());
    assertEquals(0, fileNavigator.getColumn());
  }

  @Test
  public void handlerShouldExtractFileNameFromFullWindowsPath() {
    handler.handleMessage("c:\\FileName.java");
    assertEquals("c:\\FileName.java", fileNavigator.getFileName());
    assertEquals(0, fileNavigator.getLine());
    assertEquals(0, fileNavigator.getColumn());

    handler.handleMessage("c:\\FileName.java:80");
    assertEquals("c:\\FileName.java", fileNavigator.getFileName());
    assertEquals(79, fileNavigator.getLine());
    assertEquals(0, fileNavigator.getColumn());

    handler.handleMessage("c:\\FileName.java:80:20");
    assertEquals("c:\\FileName.java", fileNavigator.getFileName());
    assertEquals(79, fileNavigator.getLine());
    assertEquals(19, fileNavigator.getColumn());
  }

  @Test
  public void handlerShouldExtractLineNumberAfterHashCharacter() {
    handler.handleMessage("FileName.java#80");
    assertEquals("FileName.java", fileNavigator.getFileName());
    assertEquals(79, fileNavigator.getLine());
    assertEquals(0, fileNavigator.getColumn());
  }

  @Test
  public void handlerShouldExtractLineAndColumnNumberAfterColon() {
    handler.handleMessage("FileName.java#80#20");
    assertEquals("FileName.java", fileNavigator.getFileName());
    assertEquals(79, fileNavigator.getLine());
    assertEquals(19, fileNavigator.getColumn());
  }

  @Test
  public void handlerShouldExtractLineAndColumnNumberAfterHashCharacter() {
    handler.handleMessage("FileName.java:80:20");
    assertEquals("FileName.java", fileNavigator.getFileName());
    assertEquals(79, fileNavigator.getLine());
    assertEquals(19, fileNavigator.getColumn());
  }
}

class StubFileNavigator implements FileNavigator {

  private String fileName;
  private int line;
  private int column;

  @Override
  public void findAndNavigate(String fileName, int line, int column) {
    this.fileName = fileName;
    this.line = line;
    this.column = column;
  }

  public String getFileName() {
    return fileName;
  }

  public int getLine() {
    return line;
  }

  public int getColumn() {
    return column;
  }
}
