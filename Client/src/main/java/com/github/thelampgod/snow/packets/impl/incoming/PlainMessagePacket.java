package com.github.thelampgod.snow.packets.impl.incoming;

import com.github.thelampgod.snow.packets.SnowflakePacket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import static com.github.thelampgod.snow.util.Helper.printModMessage;

public class PlainMessagePacket extends SnowflakePacket {
  private final String message;
  public PlainMessagePacket(DataInputStream in) throws IOException {
    this.message = in.readUTF();
  }

  @Override
  public void writeData(DataOutputStream out) throws IOException {

  }

  @Override
  public void handle() {
    printModMessage(message);
  }
}
