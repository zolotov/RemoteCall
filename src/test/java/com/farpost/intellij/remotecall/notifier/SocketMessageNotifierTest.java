package com.farpost.intellij.remotecall.notifier;

import com.farpost.intellij.remotecall.handler.MessageHandler;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

@Test
public class SocketMessageNotifierTest {

	private static final int PORT = 62775;
	private Thread notifierThread;
	private final StubMessageHandler messageHandler = new StubMessageHandler();
	private ServerSocket socket;

	@BeforeMethod
	public void setUp() throws IOException {
		MessageNotifier notifier = createNotifier();
		notifier.addMessageHandler(messageHandler);

		notifierThread = new Thread(notifier);
		notifierThread.start();
	}

	@AfterMethod
	public void tearDown() throws IOException {
		messageHandler.clear();
		notifierThread.interrupt();
		disposeNotifier();
	}

	@Test
	public void notifierShouldCallHandlerOnMessageReceived() throws IOException {
		sendMessage("GET /?message=HelloFile.java");
		assertEquals(messageHandler.getLastMessage(), "HelloFile.java");
	}

	@Test
	public void notifierShouldSkipEmptyMessages() throws IOException {
		sendMessage("");
		assertNull(messageHandler.getLastMessage(),
			"Received " + messageHandler.getLastMessage() + ". Null expected");
		messageHandler.clear();
	}

	@Test
	public void notifierShouldReceiveOnlyGetRequests() throws IOException {
		sendMessage("GET /?message=foo");
		assertEquals(messageHandler.getLastMessage(), "foo");
		messageHandler.clear();

		sendMessage("POST /\r\n\r\nmessage=bar");
		assertNull(messageHandler.getLastMessage(),
			"Received " + messageHandler.getLastMessage() + ". Null expected");
		messageHandler.clear();

		sendMessage("DELETE /?message=bar");
		assertNull(messageHandler.getLastMessage(),
			"Received " + messageHandler.getLastMessage() + ". Null expected");
		messageHandler.clear();
	}

	private MessageNotifier createNotifier() throws IOException {
		socket = new ServerSocket();
		socket.bind(new InetSocketAddress("localhost", PORT));
		return new SocketMessageNotifier(socket);
	}

	private static void sendMessage(String message) throws IOException {
		Socket client = new Socket("localhost", PORT);
    try {
      client.getOutputStream().write(message.getBytes());
    }
    finally {
      client.close();
    }
  }

	private void disposeNotifier() throws IOException {
		socket.close();
	}
}

class StubMessageHandler implements MessageHandler {

	private final BlockingQueue<String> messages = new LinkedBlockingQueue<String>();

	@Override
	public void handleMessage(String message) {
		messages.add(message);
	}

	public String getLastMessage() {
		try {
			return messages.poll(1, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	public void clear() {
		messages.clear();
	}

}

