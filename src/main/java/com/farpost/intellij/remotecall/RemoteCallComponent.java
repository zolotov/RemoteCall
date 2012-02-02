package com.farpost.intellij.remotecall;

import com.farpost.intellij.remotecall.handler.OpenFileMessageHandler;
import com.farpost.intellij.remotecall.notifier.MessageNotifier;
import com.farpost.intellij.remotecall.notifier.SocketMessageNotifier;
import com.farpost.intellij.remotecall.utils.FileNavigatorImpl;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;

public class RemoteCallComponent implements ApplicationComponent {

	private ServerSocket serverSocket;

	private static final Logger log = Logger.getInstance(RemoteCallComponent.class);
	private Thread listenerThread;

	public RemoteCallComponent() {
	}

	public void initComponent() {

		try {
			serverSocket = new ServerSocket();
			serverSocket.bind(new InetSocketAddress("localhost", 8091));
			log.info("Listening 8091");
		} catch (IOException e) {
			ApplicationManager.getApplication().invokeLater(new Runnable() {
				public void run() {
					Messages.showMessageDialog("Can't bind with 8091 port. RemoteCall plugin won't work",
						"RemoteCall Plugin Error", Messages.getErrorIcon());
				}
			});
			return;
		}

		MessageNotifier messageNotifier = new SocketMessageNotifier(serverSocket);
		messageNotifier.addMessageHandler(new OpenFileMessageHandler(new FileNavigatorImpl()));
		listenerThread = new Thread(messageNotifier);
		listenerThread.start();
	}

	public void disposeComponent() {
		try {
			if (listenerThread != null) {
				listenerThread.interrupt();
			}
			serverSocket.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@NotNull
	public String getComponentName() {
		return "RemoteCallComponent";
	}
}