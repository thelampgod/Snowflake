package com.github.thelampgod.snow.packets.impl.incoming;

import com.github.thelampgod.snow.packets.SnowflakePacket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MultiPacketPacket extends SnowflakePacket {
  private final List<SnowflakePacket> packets = new ArrayList<>();

  public MultiPacketPacket(DataInputStream in) throws Exception {
    while (!in.readBoolean()) {
      packets.add(readPacket(in));
    }
  }

  private SnowflakePacket readPacket(DataInputStream in) throws Exception {
    return SnowflakePacket.fromId(in.readByte(), in);
  }

  @Override
  public void writeData(DataOutputStream out) throws IOException {

  }

  @Override
  public void handle() {
    for (SnowflakePacket packet : packets) {
      packet.handle();
    }

  }
}
