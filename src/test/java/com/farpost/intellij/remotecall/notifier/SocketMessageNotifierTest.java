package com.farpost.intellij.remotecall.notifier;

import org.testng.annotations.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;

@Test
public class SocketMessageNotifierTest extends MessageNotifierTestCase {

	private ServerSocket socket;

	@Override
	protected MessageNotifier createNotifier() {
		try {
			socket = new ServerSocket();
			socket.bind(new InetSocketAddress("localhost", 62775));
			return new SocketMessageNotifier(socket);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected void disposeNotifier() {
		try {
			socket.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
