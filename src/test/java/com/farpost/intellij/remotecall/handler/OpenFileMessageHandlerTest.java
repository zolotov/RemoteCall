package com.farpost.intellij.remotecall.handler;

import com.farpost.intellij.remotecall.utils.FileNavigator;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class OpenFileMessageHandlerTest {

	private OpenFileMessageHandler handler;
	private StubFileNavigator fileNavigator;

	@BeforeClass
	private void setUp() {
		fileNavigator = new StubFileNavigator();
		handler = new OpenFileMessageHandler(fileNavigator);
	}

	@Test
	public void handlerShouldExtractFilenameAndLineFromMessage() {
		handler.handleMessage("FileName.java:80");
		assertEquals(fileNavigator.getFileName(), "FileName.java");
		assertEquals(fileNavigator.getLine(), 79);
		assertEquals(fileNavigator.getColumn(), 0);

		handler.handleMessage("FileName.java");
		assertEquals(fileNavigator.getFileName(), "FileName.java");
		assertEquals(fileNavigator.getLine(), 0);
		assertEquals(fileNavigator.getColumn(), 0);

		handler.handleMessage("FileName.java:error");
		assertEquals(fileNavigator.getFileName(), "FileName.java:error");
		assertEquals(fileNavigator.getLine(), 0);
		assertEquals(fileNavigator.getColumn(), 0);
	}

	@Test
	public void handlerShouldExtractFileNameFromFullWindowsPath() {
		handler.handleMessage("c:\\FileName.java");
		assertEquals(fileNavigator.getFileName(), "c:\\FileName.java");
		assertEquals(fileNavigator.getLine(), 0);
		assertEquals(fileNavigator.getColumn(), 0);

		handler.handleMessage("c:\\FileName.java:80");
		assertEquals(fileNavigator.getFileName(), "c:\\FileName.java");
		assertEquals(fileNavigator.getLine(), 79);
		assertEquals(fileNavigator.getColumn(), 0);

		handler.handleMessage("c:\\FileName.java:80:20");
		assertEquals(fileNavigator.getFileName(), "c:\\FileName.java");
		assertEquals(fileNavigator.getLine(), 79);
		assertEquals(fileNavigator.getColumn(), 19);
	}

	@Test
	public void handlerShouldExtractLineNumberAfterHashCharacter() {
		handler.handleMessage("FileName.java#80");
		assertEquals(fileNavigator.getFileName(), "FileName.java");
		assertEquals(fileNavigator.getLine(), 79);
		assertEquals(fileNavigator.getColumn(), 0);
	}

	@Test
	public void handlerShouldExtractLineAndColumnNumberAfterColon() {
		handler.handleMessage("FileName.java#80#20");
		assertEquals(fileNavigator.getFileName(), "FileName.java");
		assertEquals(fileNavigator.getLine(), 79);
		assertEquals(fileNavigator.getColumn(), 19);
	}

	@Test
	public void handlerShouldExtractLineAndColumnNumberAfterHashCharacter() {
		handler.handleMessage("FileName.java:80:20");
		assertEquals(fileNavigator.getFileName(), "FileName.java");
		assertEquals(fileNavigator.getLine(), 79);
		assertEquals(fileNavigator.getColumn(), 19);
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
