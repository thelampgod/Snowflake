package com.github.thelampgod.snow.packets.impl.outgoing;

import com.github.thelampgod.snow.packets.SnowflakePacket;

import java.io.DataOutputStream;
import java.io.IOException;

public class RemoveRecipientPacket extends SnowflakePacket {
  private final int id;
  public RemoveRecipientPacket(int id) {
    this.id = id;
  }

  @Override
  public void writeData(DataOutputStream out) throws IOException {
    out.writeByte(3);
    out.writeByte(0);
    out.writeInt(id);
  }

  @Override
  public void handle() {

  }
}
