package com.github.thelampgod.snow.packets.impl.incoming;

import com.github.thelampgod.snow.ServerManager;
import com.github.thelampgod.snow.Snow;
import com.github.thelampgod.snow.packets.SnowflakePacket;
import com.github.thelampgod.snow.packets.impl.outgoing.ListRecipientsPacket;
import com.github.thelampgod.snow.packets.impl.outgoing.ListUsersPacket;

import java.io.DataOutputStream;
import java.io.IOException;

public class AuthSuccessPacket extends SnowflakePacket {


  @Override
  public void writeData(DataOutputStream out) throws IOException {

  }

  @Override
  public void handle() {
    final ServerManager man = Snow.instance.getServerManager();

    man.sendPacket(new ListUsersPacket());
    man.sendPacket(new ListRecipientsPacket());
  }
}
