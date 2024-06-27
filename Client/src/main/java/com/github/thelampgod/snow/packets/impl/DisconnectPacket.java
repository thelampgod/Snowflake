package com.github.thelampgod.snow.packets.impl;

import com.github.thelampgod.snow.Snow;
import com.github.thelampgod.snow.packets.SnowflakePacket;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import static com.github.thelampgod.snow.Helper.printModMessage;

public class DisconnectPacket extends SnowflakePacket {
  private final String reason;

  public DisconnectPacket(String reason) {
    this.reason = reason;
  }

  public DisconnectPacket(DataInputStream in) throws IOException {
    this(in.readUTF());
  }
  public DisconnectPacket() {
    this.reason = "Disconnected";
  }

  @Override
  public void writeData(DataOutputStream out) throws IOException {
    out.writeByte(-1);
  }

  @Override
  public void handle() {
    printModMessage(
        Text.literal("You got disconnected. Reason: ").formatted(Formatting.WHITE)
            .append(Text.literal(this.reason).formatted(Formatting.ITALIC)
            ));
    Snow.getServerManager().close();
  }
}
