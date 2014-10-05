package com.farpost.intellij.remotecall.settings;

import com.farpost.intellij.remotecall.RemoteCallComponent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class RemoteCallConfigurable implements SearchableConfigurable {
  @NotNull private final RemoteCallSettings mySettings;
  private JCheckBox myAllowFromLocalhostOnlyCheckBox;
  private JSpinner myListeningPortSpinner;
  private JPanel myPanel;

  public RemoteCallConfigurable(@NotNull RemoteCallSettings settings) {
    mySettings = settings;
    myListeningPortSpinner.setModel(new SpinnerNumberModel(settings.getPortNumber(), 0, 65535, 1));
  }

  @NotNull
  @Override
  public String getId() {
    return "remote.call";
  }

  @Nullable
  @Override
  public Runnable enableSearch(String s) {
    return null;
  }

  @Nls
  @Override
  public String getDisplayName() {
    return "Remote Call";
  }

  @Nullable
  @Override
  public String getHelpTopic() {
    return null;
  }

  @Nullable
  @Override
  public JComponent createComponent() {
    return myPanel;
  }

  @Override
  public boolean isModified() {
    return !myListeningPortSpinner.getValue().equals(mySettings.getPortNumber()) ||
           myAllowFromLocalhostOnlyCheckBox.isSelected() != mySettings.isAllowRequestsFromLocalhostOnly();
  }

  @Override
  public void apply() throws ConfigurationException {
    mySettings.setAllowRequestsFromLocalhostOnly(myAllowFromLocalhostOnlyCheckBox.isSelected());
    mySettings.setPortNumber((Integer)myListeningPortSpinner.getValue());

    final RemoteCallComponent remoteCallComponent = ApplicationManager.getApplication().getComponent(RemoteCallComponent.class);
    remoteCallComponent.disposeComponent();
    remoteCallComponent.initComponent();
  }

  @Override
  public void reset() {
    myListeningPortSpinner.setValue(mySettings.getPortNumber());
    myAllowFromLocalhostOnlyCheckBox.setSelected(mySettings.isAllowRequestsFromLocalhostOnly());
  }

  @Override
  public void disposeUIResources() {
  }
}
