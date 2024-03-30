package com.github.thelampgod.snow.packets.impl.outgoing;

import com.github.thelampgod.snow.packets.SnowflakePacket;

import java.io.DataOutputStream;
import java.io.IOException;

public class OutgoingMessagePacket extends SnowflakePacket {
  private final String message;

  public OutgoingMessagePacket(String message) {
    this.message = message;
  }

  public String getMessage() {
    return message;
  }

  @Override
  public void writeData(DataOutputStream out) throws IOException {
    byte[] messageBytes = message.getBytes();
    out.writeByte(1);
    out.writeInt(messageBytes.length);
    out.write(messageBytes);
  }

  @Override
  public void handle() {

  }
}
