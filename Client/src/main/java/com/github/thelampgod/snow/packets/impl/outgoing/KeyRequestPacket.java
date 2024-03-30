package com.github.thelampgod.snow.packets.impl.outgoing;

import com.github.thelampgod.snow.packets.SnowflakePacket;

import java.io.DataOutputStream;
import java.io.IOException;

public class KeyRequestPacket extends SnowflakePacket {
  private final int id;

  public KeyRequestPacket(int id) {
    this.id = id;
  }

  @Override
  public void writeData(DataOutputStream out) throws IOException {
    out.writeByte(6);
    out.writeInt(id);
  }

  @Override
  public void handle() {

  }
}
