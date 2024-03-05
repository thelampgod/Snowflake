package com.github.thelampgod.snow.packets.impl;

import com.github.thelampgod.snow.packets.SnowflakePacket;

import java.io.DataOutputStream;
import java.io.IOException;

public class LoginStartPacket extends SnowflakePacket {
  private final byte[] key;

  public LoginStartPacket(byte[] key) {
    this.key = key;
  }

  @Override
  public void writeData(DataOutputStream out) throws IOException {
    out.writeByte(0);
    out.writeInt(key.length);
    out.write(key);
  }

  @Override
  public void handle() {

  }
}
