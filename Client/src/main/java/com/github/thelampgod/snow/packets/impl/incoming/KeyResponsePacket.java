package com.github.thelampgod.snow.packets.impl.incoming;

import com.github.thelampgod.snow.Snow;
import com.github.thelampgod.snow.packets.SnowflakePacket;
import com.github.thelampgod.snow.users.User;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class KeyResponsePacket extends SnowflakePacket {
  private final int id;
  private final String key;

  public KeyResponsePacket(DataInputStream in) throws IOException {
    this.id = in.readInt();
    this.key = in.readUTF();
  }

  @Override
  public void writeData(DataOutputStream out) throws IOException {
  }

  @Override
  public void handle() {
    Snow.instance.getUserManager().get(id).setKey(key);
  }
}
