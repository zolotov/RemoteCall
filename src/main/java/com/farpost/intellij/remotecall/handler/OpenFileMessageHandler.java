package com.farpost.intellij.remotecall.handler;

import com.farpost.intellij.remotecall.utils.FileNavigator;
import com.intellij.openapi.diagnostic.Logger;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Integer.parseInt;
import static java.util.regex.Pattern.compile;

public class OpenFileMessageHandler implements MessageHandler {

	private static final Logger log = Logger.getInstance(OpenFileMessageHandler.class);
	private static final Pattern COLUMN_PATTERN = compile("[:#](\\d+)[:#]?(\\d*)$");
	private final FileNavigator fileNavigator;


	public OpenFileMessageHandler(FileNavigator fileNavigator) {
		this.fileNavigator = fileNavigator;
	}

	public void handleMessage(String message) {
		Matcher matcher = COLUMN_PATTERN.matcher(message);
		int line = 0;
		int column = 0;

		if (matcher.find()) {
			try {
				line = parseInt(matcher.group(1)) - 1;
				if (!matcher.group(2).isEmpty()) {
					column = parseInt(matcher.group(2)) - 1;
				}
			} catch (NumberFormatException e) {
				log.error("Impossible situation, but who knows... RemoteCall extracting line/column number error", e);
			}
		}

		fileNavigator.findAndNavigate(matcher.replaceAll(""), line, column);
	}
}
