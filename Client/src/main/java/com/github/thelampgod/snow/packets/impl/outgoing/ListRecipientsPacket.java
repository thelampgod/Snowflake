package com.github.thelampgod.snow.packets.impl.outgoing;

import com.github.thelampgod.snow.packets.SnowflakePacket;

import java.io.DataOutputStream;
import java.io.IOException;

public class ListRecipientsPacket extends SnowflakePacket {

  @Override
  public void writeData(DataOutputStream out) throws IOException {
    out.writeByte(5);
  }

  @Override
  public void handle() {

  }
}
