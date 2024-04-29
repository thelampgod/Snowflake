package com.github.thelampgod.snow.packets.impl.incoming;

import com.github.thelampgod.snow.Helper;
import com.github.thelampgod.snow.Snow;
import com.github.thelampgod.snow.packets.SnowflakePacket;
import com.github.thelampgod.snow.packets.impl.outgoing.KeyRequestPacket;
import com.github.thelampgod.snow.users.User;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import static com.github.thelampgod.snow.Helper.printModMessage;

public class ConnectionPacket extends SnowflakePacket {
  private final boolean connect;
  private final int id;
  private final String user;


  public ConnectionPacket(boolean connect, int id, String user) {
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


  public static class Connect extends ConnectionPacket {

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
      Helper.addToast("User connected!", this.getUser() + " joined.");

      if (Snow.instance.getUserManager().add(new User(this.getUser(), this.getId(), null))) {
        Snow.getServerManager().sendPacket(new KeyRequestPacket(this.getId()));
      }
    }

  }

  public static class Disconnect extends ConnectionPacket {

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
                      .append(Text.literal(" disconnected").formatted(Formatting.RED)
                      ));
      Helper.addToast("User disconnected!", this.getUser() + " left.");

      Snow.instance.getUserManager().remove(this.getId());
    }
  }
}
