package com.github.thelampgod.snow.packets.impl.incoming;

import com.github.thelampgod.snow.Snow;
import com.github.thelampgod.snow.packets.SnowflakePacket;
import com.github.thelampgod.snow.users.User;
import com.github.thelampgod.snow.users.UserManager;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UsersPacket extends SnowflakePacket {
  private final Map<Integer, String> idToNameMap = new HashMap<>();

  public UsersPacket(DataInputStream in) throws IOException {
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
    final UserManager man = Snow.instance.getUserManager();
    idToNameMap.forEach((id, name) -> man.add(new User(name, id, "")));
  }
}
