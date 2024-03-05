package com.github.thelampgod.snow.packets.impl;

import com.github.thelampgod.snow.Snow;
import com.github.thelampgod.snow.packets.SnowflakePacket;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import static com.github.thelampgod.snow.Helper.printMessage;


public class IncomingMessagePacket extends SnowflakePacket {

  private final int sender;
  private final String message;

  public IncomingMessagePacket(int sender, String message) {
    this.sender = sender;
    this.message = message;
  }

  public IncomingMessagePacket(DataInputStream in) throws IOException {
    this.sender = in.readInt();
    byte[] msg = new byte[in.readInt()];
    in.readFully(msg);
    this.message = new String(msg);
  }

  public String getMessage() {
    return message;
  }

  public int getSender() {
    return sender;
  }

  @Override
  public void writeData(DataOutputStream out) throws IOException {

  }

  @Override
  public void handle() {
    final String name = Snow.instance.getServerManager().getUser(this.getSender());
    printMessage(
        Text.literal(name + ": ").formatted(Formatting.GREEN)
            .append(Text.literal(this.getMessage()).formatted(Formatting.RESET)));
  }
}
