package com.farpost.intellij.remotecall.handler;

import com.farpost.intellij.remotecall.utils.FileNavigator;
import com.intellij.openapi.diagnostic.Logger;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Integer.parseInt;
import static java.util.regex.Pattern.compile;

public class OpenFileMessageHandler implements MessageHandler {

	private static final Logger log = Logger.getInstance(OpenFileMessageHandler.class);
	private static final Pattern LINE_PATTERN = compile("[:#](\\d+)$");
	private FileNavigator fileNavigator;


	public OpenFileMessageHandler(FileNavigator fileNavigator) {
		this.fileNavigator = fileNavigator;
	}

	public void handleMessage(String message) {
		Matcher matcher = LINE_PATTERN.matcher(message);
		int line = 0;

		if (matcher.find()) {
			try {
				line = parseInt(matcher.group(1)) - 1;
			} catch (NumberFormatException e) {
				log.error("Impossible situation, but who knows... RemoteCall extracting line number error", e);
			}
		}

		fileNavigator.findAndNavigate(matcher.replaceAll(""), line);
	}
}
