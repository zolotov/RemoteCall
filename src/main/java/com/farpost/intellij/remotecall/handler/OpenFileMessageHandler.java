package com.farpost.intellij.remotecall.handler;

import com.farpost.intellij.remotecall.utils.FileNavigator;
import com.intellij.openapi.diagnostic.Logger;

public class OpenFileMessageHandler implements MessageHandler {

	private final Logger log = Logger.getInstance(getClass().getName());
	private FileNavigator fileNavigator;

	public OpenFileMessageHandler(FileNavigator fileNavigator) {
		this.fileNavigator = fileNavigator;
	}

	public void handleMessage(String message) {
		String[] fileInfo = message.split(":", 2);
		String fileName = fileInfo[0];
		int line = 0;
		if (fileInfo.length > 1) {
			try {
				line = Integer.parseInt(fileInfo[1]);
			} catch (NumberFormatException e) {
				log.info("Bad line number: " + fileInfo[1] + ". File will be opened on the first line");
			}
		}
		if (line > 0) line--;

		fileNavigator.findAndNavigate(fileName, line);
	}
}
