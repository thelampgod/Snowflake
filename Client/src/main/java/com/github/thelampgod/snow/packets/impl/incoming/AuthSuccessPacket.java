package com.github.thelampgod.snow.packets.impl.incoming;

import com.github.thelampgod.snow.ServerManager;
import com.github.thelampgod.snow.Snow;
import com.github.thelampgod.snow.packets.SnowflakePacket;
import com.github.thelampgod.snow.packets.impl.outgoing.ListUsersPacket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import static com.github.thelampgod.snow.Helper.printModMessage;

public class AuthSuccessPacket extends SnowflakePacket {

  private final int id;

  public AuthSuccessPacket(DataInputStream in) throws IOException {
    this.id = in.readInt();
  }

  @Override
  public void writeData(DataOutputStream out) throws IOException {

  }

  @Override
  public void handle() {
    Snow.instance.getUserManager().setMe(id);

    final ServerManager man = Snow.getServerManager();

    man.sendPacket(new ListUsersPacket());
    try {
      Snow.instance.getGroupManager().load(man.address);
    } catch (IOException e) {
      printModMessage("Couldn't load group passwords");
      e.printStackTrace();
    }
  }
}
