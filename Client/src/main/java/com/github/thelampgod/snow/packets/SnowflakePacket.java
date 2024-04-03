package com.github.thelampgod.snow.packets;

import com.github.thelampgod.snow.packets.impl.*;
import com.github.thelampgod.snow.packets.impl.incoming.*;
import com.github.thelampgod.snow.packets.impl.GroupRemovePacket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;

public abstract class SnowflakePacket implements Serializable {
  public static SnowflakePacket fromId(byte id, DataInputStream in) throws Exception {
    switch (id) {
      case 4:
        return new KeepAlivePacket(in);
      case 5:
        return new ConnectionPacket.Connect(in);
      case 6:
        return new ConnectionPacket.Disconnect(in);
      case 7:
        return new DisconnectPacket(in);
      case 8:
        return new HandshakePacket(in);
      case 9:
        return new PlainMessagePacket(in);
      case 10:
        return new KeyResponsePacket(in);
      case 12:
        return new UsersPacket(in);
      case 13:
        return new AuthSuccessPacket();
      case 14:
        return new MultiPacketPacket(in);
      case 16:
        return new GroupInfoPacket(in);
      case 17:
        return new GroupPasswordPacket(in);
      case 18:
        switch (in.readByte()) {
          case 0:
            return new GroupConnectionPacket.Added(in);
          case 1:
            return new GroupConnectionPacket.Removed(in);
          case 2:
            return new GroupConnectionPacket.Joined(in);
          case 3:
            return new GroupConnectionPacket.Left(in);
        }
      case 19:
        return new GroupPasswordUpdatePacket(in);
      case 20:
        return new GroupRemovePacket(in);
      case 21:
        if (in.readBoolean()) {
          return new EncryptedDataPacket.Group(in);
        }
        return new EncryptedDataPacket.User(in);
      default:
        throw new RuntimeException("Unknown packet type " + id);
    }
  }

  public abstract void writeData(DataOutputStream out) throws IOException;

  public abstract void handle();

}
