package com.github.thelampgod.snow.packets.impl.outgoing;

import com.github.thelampgod.snow.packets.SnowflakePacket;

import java.io.DataOutputStream;
import java.io.IOException;

public class ListUsersPacket extends SnowflakePacket {
  @Override
  public void writeData(DataOutputStream out) throws IOException {
    out.writeByte(4);
  }

  @Override
  public void handle() {

  }
}
