package com.github.thelampgod.snow.packets.impl.incoming;

import com.github.thelampgod.snow.Snow;
import com.github.thelampgod.snow.packets.SnowflakePacket;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import static com.github.thelampgod.snow.Helper.printModMessage;

public class IncomingConnectionPacket extends SnowflakePacket {
  private final boolean connect;
  private final int id;
  private final String user;


  public IncomingConnectionPacket(boolean connect, int id, String user) {
    this.connect = connect;
    this.id = id;
    this.user = user;
  }

  public boolean isConnecting() {
    return connect;
  }

  public int getId() {
    return id;
  }

  public String getUser() {
    return user;
  }

  @Override
  public void writeData(DataOutputStream out) {
  }

  @Override
  public void handle() {
  }


  public static class Connect extends IncomingConnectionPacket {

    public Connect(DataInputStream in) throws IOException {
      this(in.readInt(), in.readUTF());
    }

    public Connect(int id, String user) {
      super(true, id, user);
    }

    @Override
    public void handle() {
      printModMessage(
          Text.literal(this.getUser()).formatted(Formatting.ITALIC)
              .append(Text.literal(" connected").formatted(Formatting.GREEN)
              ));

      Snow.instance.getServerManager().addUser(this.getId(), this.getUser());
    }

  }

  public static class Disconnect extends IncomingConnectionPacket {

    public Disconnect(DataInputStream in) throws IOException {
      this(in.readInt(), in.readUTF());
    }

    public Disconnect(int id, String user) {
      super(false, id, user);
    }

    @Override
    public void handle() {
      printModMessage(
              Text.literal(this.getUser()).formatted(Formatting.ITALIC)
                      .append(Text.literal(" connected").formatted(Formatting.RED)
                      ));

      Snow.instance.getServerManager().removeUser(this.getId());
    }
  }
}
