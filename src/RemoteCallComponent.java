import com.farpost.intellij.remotecall.handler.OpenFileMessageHandler;
import com.farpost.intellij.remotecall.notifier.MessageNotifier;
import com.farpost.intellij.remotecall.notifier.SocketMessageNotifier;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.diagnostic.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.ServerSocket;

public class RemoteCallComponent implements ApplicationComponent {

	private ServerSocket serverSocket;

	final Logger log = Logger.getInstance(getClass().getName());
	private Thread listenerThread;

	public RemoteCallComponent() {
	}

	public void initComponent() {

		try {
			serverSocket = new ServerSocket(8091);
			log.info("Listening 8091");
		} catch (IOException e) {
			log.error("Can't open socket", e);
			return;
		}

		MessageNotifier messageNotifier = new SocketMessageNotifier(serverSocket);
		messageNotifier.addMessageHandler(new OpenFileMessageHandler());
		listenerThread = new Thread(messageNotifier);
		listenerThread.start();
	}

	public void disposeComponent() {
		try {
			listenerThread.interrupt();
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
