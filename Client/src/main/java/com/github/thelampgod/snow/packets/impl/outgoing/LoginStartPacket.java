package com.github.thelampgod.snow.packets.impl.outgoing;

import com.github.thelampgod.snow.packets.SnowflakePacket;

import java.io.DataOutputStream;
import java.io.IOException;

public class LoginStartPacket extends SnowflakePacket {
  private final String key;

  public LoginStartPacket(String key) {
    this.key = key;
  }

  @Override
  public void writeData(DataOutputStream out) throws IOException {
    out.writeByte(0);
    out.writeUTF(key);
  }

  @Override
  public void handle() {

  }
}
