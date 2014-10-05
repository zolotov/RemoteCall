package com.farpost.intellij.remotecall.settings;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.components.StoragePathMacros;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.Nullable;

@State(
  name = "RemoteCall",
  storages = {@Storage(
    file = StoragePathMacros.APP_CONFIG + "/remotecall.xml")})
public class RemoteCallSettings implements PersistentStateComponent<RemoteCallSettings> {
  private int myPortNumber = 8091;
  private boolean myAllowRequestsFromLocalhostOnly = true;

  public int getPortNumber() {
    return myPortNumber;
  }

  public void setPortNumber(int portNumber) {
    myPortNumber = portNumber;
  }

  public boolean isAllowRequestsFromLocalhostOnly() {
    return myAllowRequestsFromLocalhostOnly;
  }

  public void setAllowRequestsFromLocalhostOnly(boolean allowRequestsFromLocalhostOnly) {
    myAllowRequestsFromLocalhostOnly = allowRequestsFromLocalhostOnly;
  }

  @Nullable
  @Override
  public RemoteCallSettings getState() {
    return this;
  }

  @Override
  public void loadState(RemoteCallSettings remoteCallSettings) {
    XmlSerializerUtil.copyBean(remoteCallSettings, this);
  }
}
