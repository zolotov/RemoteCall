package com.farpost.intellij.remotecall.notifier;

import com.farpost.intellij.remotecall.handler.MessageHandler;
import com.intellij.openapi.diagnostic.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.HashSet;

public class SocketMessageNotifier implements MessageNotifier {

	private final Logger log = Logger.getInstance(getClass().getName());
	private Collection<MessageHandler> messageHandlers = new HashSet<MessageHandler>();
	private ServerSocket serverSocket;

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
			} catch (IOException e) {
				log.error("Error while accepting", e);
				continue;
			}
			try {
				BufferedReader in = new BufferedReader(
					new InputStreamReader(clientSocket.getInputStream())
				);

				String inputLine, message = "";


				while ((inputLine = in.readLine()) != null) {
					message += inputLine;
				}

				log.info("Received message " + message);
				handleMessage(message);
			} catch (IOException e) {
				log.error("Error", e);
			}
		}
	}


	private void handleMessage(String message) {
		for (MessageHandler handler : messageHandlers) {
			handler.handleMessage(message);
		}
	}
}
