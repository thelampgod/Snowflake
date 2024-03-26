package com.github.thelampgod.snow.packets;

import com.github.thelampgod.snow.EncryptionUtil;
import com.github.thelampgod.snow.packets.impl.*;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.security.PrivateKey;

import static com.github.thelampgod.snow.Helper.mc;

public abstract class SnowflakePacket {
  public static SnowflakePacket fromId(byte id, DataInputStream in) throws Exception {
    switch (id) {
      case 1:
        return new IncomingMessagePacket(in);
      case 2:
        return new IncomingLocationPacket(in);
      case 3:
        return parseEncryptedPacket(in);
      case 4:
        return new KeepAlivePacket(in);
      case 5:
        return new IncomingConnectionPacket.Connect(in);
      case 6:
        return new IncomingConnectionPacket.Disconnect(in);
      case 7:
        return new DisconnectPacket(in);
      case 8:
        return new IncomingHandshakePacket(in);
      case 9:
        return new IncomingPlainMessagePacket(in);
      case 10:
        return new KeyResponsePacket(in);
      case 11:
        return new IncomingRecipientsPacket(in);
      case 12:
        return new IncomingUsersPacket(in);
      case 13:
        return new AuthSuccessPacket();
      case 14:
        return new MultiPacketPacket(in);
//      case 15:
//        return new ChunkPacket(in);
      case 16:
        return new GroupInfoPacket(in);
      case 17:
        return new GroupPasswordPacket(in);
      case 18:
        return new GroupConnectionPacket(in);
      default:
        throw new RuntimeException("Unknown packet type " + id);
    }
  }

  private static SnowflakePacket parseEncryptedPacket(DataInputStream in) throws Exception {
    final int sender = in.readInt();
    byte[] encryptedData = new byte[in.readInt()];
    in.readFully(encryptedData);

    PrivateKey key = mc.getProfileKeys().fetchKeyPair().get().get().privateKey();

    final JsonObject decryptedData = (JsonObject) JsonParser.parseString(new String(EncryptionUtil.decrypt(encryptedData, key)));
    byte id = decryptedData.get("id").getAsByte();

    switch (id) {
      case 1:
        return new IncomingMessagePacket(sender,
            decryptedData.get("message").getAsString());
      case 2:
        return new IncomingLocationPacket(sender,
            decryptedData.get("posX").getAsDouble(),
            decryptedData.get("posY").getAsDouble(),
            decryptedData.get("posZ").getAsDouble(),
            decryptedData.get("dimension").getAsByte());
      default:
        throw new RuntimeException("Unknown packet type " + id);
    }
  }

  public abstract void writeData(DataOutputStream out) throws IOException;

  public abstract void handle();
}
