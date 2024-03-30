package com.github.thelampgod.snow.packets.impl.incoming;

import com.github.thelampgod.snow.ServerManager;
import com.github.thelampgod.snow.Snow;
import com.github.thelampgod.snow.packets.SnowflakePacket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class IncomingUsersPacket extends SnowflakePacket {
  private final Map<Integer, String> idToNameMap = new HashMap<>();

  public IncomingUsersPacket(DataInputStream in) throws IOException {
    byte numUsers = in.readByte();
    for (int i = 0; i < numUsers; ++i) {
      idToNameMap.put(in.readInt(), in.readUTF());
    }
  }

  @Override
  public void writeData(DataOutputStream out) throws IOException {

  }

  @Override
  public void handle() {
    final ServerManager man = Snow.instance.getServerManager();
    idToNameMap.forEach(man::addUser);
  }
}
