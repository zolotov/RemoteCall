package com.farpost.intellij.remotecall.notifier;

import com.farpost.intellij.remotecall.handler.MessageHandler;
import com.intellij.openapi.diagnostic.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class SocketMessageNotifier implements MessageNotifier {

	private final Logger log = Logger.getInstance(getClass().getName());
	private Collection<MessageHandler> messageHandlers = new HashSet<MessageHandler>();
	private ServerSocket serverSocket;
	private static final String CRLF = "\r\n";
	private static final String NL = "\n";

	public SocketMessageNotifier(ServerSocket serverSocket) {
		this.serverSocket = serverSocket;
	}

	public void addMessageHandler(MessageHandler handler) {
		messageHandlers.add(handler);
	}

	public void run() {
		while (true) {
			Socket clientSocket;
			try {
				clientSocket = serverSocket.accept();
				if (!clientSocket.getInetAddress().getHostAddress().equals("127.0.0.1")) {
					log.warn("Not allowed requests from " + clientSocket.getInetAddress().getHostAddress());
					continue;
				}

			} catch (IOException e) {
				log.error("Error while accepting", e);
				continue;
			}

			try {
				BufferedReader in = new BufferedReader(
					new InputStreamReader(clientSocket.getInputStream())
				);

				String inputLine, requestString = "";

				while ((inputLine = in.readLine()) != null && !inputLine.equals(CRLF) && !inputLine.equals(NL) && !inputLine.isEmpty()) {
					requestString += inputLine;
				}
				clientSocket.getOutputStream().write(("HTTP/1.1 200 OK" + CRLF + CRLF).getBytes());
				clientSocket.close();

				StringTokenizer tokenizer = new StringTokenizer(requestString);
				String method = tokenizer.nextToken();
				if (!method.equals("GET")) {
					log.warn("Only GET requests allowed");
					continue;
				}

				log.info("Received request " + requestString);
				Map<String, String> parameters = getParametersFromUrl(tokenizer.nextToken());

				String message = parameters.get("message") != null ? parameters.get("message") : "";

				log.info("Received message " + message);
				handleMessage(message);
			} catch (IOException e) {
				log.error("Error", e);
			}
		}
	}

	private Map<String, String> getParametersFromUrl(String url) {
		String parametersString = url.substring(url.indexOf('?') + 1);
		Map<String, String> parameters = new HashMap<String, String>();
		StringTokenizer tokenizer = new StringTokenizer(parametersString, "&");
		while (tokenizer.hasMoreElements()) {
			String[] parametersPair = tokenizer.nextToken().split("=", 2);
			if (parametersPair.length > 1) {
				parameters.put(parametersPair[0], parametersPair[1]);
			}
		}

		return parameters;
	}


	private void handleMessage(String message) {
		for (MessageHandler handler : messageHandlers) {
			handler.handleMessage(message);
		}
	}
}
