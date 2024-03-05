package com.github.thelampgod.snow.packets.impl;

import com.github.thelampgod.snow.ServerManager;
import com.github.thelampgod.snow.Snow;
import com.github.thelampgod.snow.packets.SnowflakePacket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class IncomingRecipientsPacket extends SnowflakePacket {
  private final Set<Integer> recipients = new HashSet<>();

  public IncomingRecipientsPacket(DataInputStream in) throws IOException {
    byte numUsers = in.readByte();
    for (int i = 0; i < numUsers; ++i) {
      recipients.add(in.readInt());
    }
  }

  @Override
  public void writeData(DataOutputStream out) throws IOException {

  }

  @Override
  public void handle() {
    final ServerManager man = Snow.instance.getServerManager();
    for (int id : recipients) {
      man.sendPacket(new KeyRequestPacket(id));
    }
  }
}
