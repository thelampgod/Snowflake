package com.github.thelampgod.snow.packets.impl;

import com.github.thelampgod.snow.EncryptionUtil;
import com.github.thelampgod.snow.ServerManager;
import com.github.thelampgod.snow.Snow;
import com.github.thelampgod.snow.packets.SnowflakePacket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

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
      String decrypted = new String(EncryptionUtil.decrypt(encryptedSecret, mc.getProfileKeys().fetchKeyPair().get().get().privateKey()));

      System.out.println("Secret is " + decrypted);
      man.sendPacket(new HandshakeResponsePacket(decrypted));
    } catch (IOException e) {
      e.printStackTrace();
    } catch (Exception e) {
        throw new RuntimeException(e);
    }

  }
}
