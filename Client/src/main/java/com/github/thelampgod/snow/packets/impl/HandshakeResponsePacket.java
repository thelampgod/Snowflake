package com.github.thelampgod.snow.packets.impl;

import com.github.thelampgod.snow.packets.SnowflakePacket;

import java.io.DataOutputStream;
import java.io.IOException;

public class HandshakeResponsePacket extends SnowflakePacket {
  private final String secret;

  public HandshakeResponsePacket(String secret) {
    this.secret = secret;
  }

  @Override
  public void writeData(DataOutputStream out) throws IOException {
    out.writeByte(10);
    out.writeUTF(secret);
  }

  @Override
  public void handle() {

  }
}
