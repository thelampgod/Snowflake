package com.github.thelampgod.snow.packets.impl.outgoing;

import com.github.thelampgod.snow.packets.SnowflakePacket;

import java.io.DataOutputStream;
import java.io.IOException;

public class HandshakeResponsePacket extends SnowflakePacket {
  private final String secret;
  private final String name;

  public HandshakeResponsePacket(String secret, String name) {
    this.secret = secret;
    this.name = name;
  }

  @Override
  public void writeData(DataOutputStream out) throws IOException {
    out.writeByte(10);
    out.writeUTF(secret);
    out.writeUTF(name);
  }

  @Override
  public void handle() {

  }
}
