package com.farpost.intellij.remotecall.notifier;

import com.farpost.intellij.remotecall.handler.MessageHandler;

public interface MessageNotifier extends Runnable {

  void addMessageHandler(MessageHandler handler);

}
