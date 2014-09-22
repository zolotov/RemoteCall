package com.farpost.intellij.remotecall.utils;

public interface FileNavigator {
  void findAndNavigate(String fileName, int line, int column);
}
