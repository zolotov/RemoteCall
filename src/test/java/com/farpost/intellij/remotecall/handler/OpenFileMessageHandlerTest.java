package com.farpost.intellij.remotecall.handler;

import com.farpost.intellij.remotecall.utils.FileNavigator;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

@Test
public class OpenFileMessageHandlerTest {

	private OpenFileMessageHandler handler;
	private StubFileNavigator fileNavigator;

	@BeforeMethod
	private void setUp() {
		fileNavigator = new StubFileNavigator();
		handler = new OpenFileMessageHandler(fileNavigator);
	}

	@Test
	public void handlerShouldExtractFilenameAndLineFromMessage() {
		handler.handleMessage("FileName.java:80");
		assertEquals(fileNavigator.getFileName(), "FileName.java");
		assertEquals(fileNavigator.getLine(), 79);

		handler.handleMessage("FileName.java:");
		assertEquals(fileNavigator.getFileName(), "FileName.java");
		assertEquals(fileNavigator.getLine(), 0);

		handler.handleMessage("FileName.java");
		assertEquals(fileNavigator.getFileName(), "FileName.java");
		assertEquals(fileNavigator.getLine(), 0);

		handler.handleMessage("FileName.java:error");
		assertEquals(fileNavigator.getFileName(), "FileName.java");
		assertEquals(fileNavigator.getLine(), 0);
	}
}

class StubFileNavigator implements FileNavigator {

	private String fileName;
	private int line;

	@Override
	public void findAndNavigate(String fileName, int line) {
		this.fileName = fileName;
		this.line = line;
	}

	public String getFileName() {
		return fileName;
	}

	public int getLine() {
		return line;
	}
}
