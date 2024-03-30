package com.github.thelampgod.snow.packets.impl.incoming;

import com.github.thelampgod.snow.ServerManager;
import com.github.thelampgod.snow.Snow;
import com.github.thelampgod.snow.packets.SnowflakePacket;
import com.github.thelampgod.snow.packets.impl.outgoing.HandshakeResponsePacket;
import net.minecraft.network.encryption.NetworkEncryptionUtils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import static com.github.thelampgod.snow.Helper.mc;

public class IncomingHandshakePacket extends SnowflakePacket {
  private final byte[] encryptedSecret;

  public IncomingHandshakePacket(DataInputStream in) throws IOException {
    encryptedSecret = new byte[in.readInt()];
    in.readFully(encryptedSecret);
  }

  @Override
  public void writeData(DataOutputStream out) throws IOException {

  }

  @Override
  public void handle() {
    final ServerManager man = Snow.instance.getServerManager();
    try {
      final String decrypted = new String(NetworkEncryptionUtils.decrypt(mc.getProfileKeys().fetchKeyPair().get().get().privateKey(), encryptedSecret));

      System.out.println("Secret is " + decrypted);
      man.sendPacket(new HandshakeResponsePacket(decrypted, mc.getSession().getUsername()));
    } catch (Exception e) {
        throw new RuntimeException(e);
    }

  }
}
