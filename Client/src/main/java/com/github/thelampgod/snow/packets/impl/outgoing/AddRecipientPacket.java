package com.github.thelampgod.snow.packets.impl.outgoing;

import com.github.thelampgod.snow.packets.SnowflakePacket;

import java.io.DataOutputStream;
import java.io.IOException;


public class AddRecipientPacket extends SnowflakePacket {
  private final int id;

  public AddRecipientPacket(int id) {
    this.id = id;
  }

  @Override
  public void writeData(DataOutputStream out) throws IOException {
    out.writeByte(2);
    //0 for adding via id
    out.writeByte(0);
    //write recipient's id
    out.writeInt(id);
  }

  @Override
  public void handle() {

  }
}
