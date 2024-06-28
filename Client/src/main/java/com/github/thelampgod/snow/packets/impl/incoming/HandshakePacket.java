package com.github.thelampgod.snow.packets.impl.incoming;

import com.github.thelampgod.snow.util.EncryptionUtil;
import com.github.thelampgod.snow.ServerManager;
import com.github.thelampgod.snow.Snow;
import com.github.thelampgod.snow.identities.Identity;
import com.github.thelampgod.snow.packets.SnowflakePacket;
import com.github.thelampgod.snow.packets.impl.outgoing.HandshakeResponsePacket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class HandshakePacket extends SnowflakePacket {
  private final byte[] encryptedSecret;

  public HandshakePacket(DataInputStream in) throws IOException {
    encryptedSecret = new byte[in.readInt()];
    in.readFully(encryptedSecret);
  }

  @Override
  public void writeData(DataOutputStream out) throws IOException {

  }

  @Override
  public void handle() {
    final ServerManager man = Snow.getServerManager();
    final Identity identity = Snow.instance.getIdentityManager().getSelectedIdentity();
    if (identity == null) {
      Snow.instance.getLog().error("No identity selected");
      man.close();
      return;
    }

    try {
      final String decrypted = new String(EncryptionUtil.decrypt(encryptedSecret, identity.getPrivateKey()));

      System.out.println("Secret is " + decrypted);
      man.sendPacket(new HandshakeResponsePacket(decrypted, identity.getName()));
    } catch (Exception e) {
        throw new RuntimeException(e);
    }

  }
}
