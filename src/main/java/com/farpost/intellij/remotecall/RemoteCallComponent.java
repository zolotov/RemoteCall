package com.farpost.intellij.remotecall;

import com.farpost.intellij.remotecall.handler.OpenFileMessageHandler;
import com.farpost.intellij.remotecall.notifier.MessageNotifier;
import com.farpost.intellij.remotecall.notifier.SocketMessageNotifier;
import com.farpost.intellij.remotecall.utils.FileNavigatorImpl;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.ui.Messages;
import com.intellij.util.SystemProperties;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;

public class RemoteCallComponent implements ApplicationComponent {
  private static final Logger log = Logger.getInstance(RemoteCallComponent.class);

  private ServerSocket serverSocket;
  private Thread listenerThread;

  public void initComponent() {
    final int port = SystemProperties.getIntProperty("idea.remote.call.port", 8091);
    final boolean remoteControl = SystemProperties.getBooleanProperty("idea.remote.call.remoteControl", false);

    try {
      serverSocket = new ServerSocket();
      String host = "localhost";
      if(remoteControl){
          host = "0.0.0.0";
      }

      serverSocket.bind(new InetSocketAddress(host, port));
      log.info("Listening " + port);
    }
    catch (IOException e) {
      ApplicationManager.getApplication().invokeLater(new Runnable() {
        public void run() {
          Messages.showMessageDialog("Can't bind with " + port + " port. RemoteCall plugin won't work", "RemoteCall Plugin Error",
                                     Messages.getErrorIcon());
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
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @NotNull
  public String getComponentName() {
    return "RemoteCallComponent";
  }
}