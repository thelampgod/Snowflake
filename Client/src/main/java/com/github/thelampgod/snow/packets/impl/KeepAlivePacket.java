package com.github.thelampgod.snow.packets.impl;

import com.github.thelampgod.snow.ServerManager;
import com.github.thelampgod.snow.Snow;
import com.github.thelampgod.snow.packets.SnowflakePacket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class KeepAlivePacket extends SnowflakePacket {

  private final long timestamp;

  public KeepAlivePacket(DataInputStream in) throws IOException {
    this(in.readLong());
  }

  public KeepAlivePacket(long timestamp) {
    this.timestamp = timestamp;
  }

  @Override
  public void writeData(DataOutputStream out) throws IOException {
    out.writeByte(9);
    out.writeLong(this.timestamp);
  }

  @Override
  public void handle() {
    final ServerManager man = Snow.instance.getServerManager();
    man.sendPacket(this);
  }

  public long getTimestamp() {
    return timestamp;
  }
}
