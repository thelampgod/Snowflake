//package com.github.thelampgod.snow.packets.impl;
//
//import com.github.thelampgod.snowball.Snowball;
//import com.github.thelampgod.snowball.util.EncryptionUtil;
//import com.github.thelampgod.snowball.util.management.SnowflakeManager;
//import com.github.thelampgod.snow.packets.SnowflakePacket;
//import com.google.gson.GsonBuilder;
//import com.google.gson.JsonObject;
//import org.bouncycastle.openpgp.PGPPublicKeyRing;
//import org.pgpainless.PGPainless;
//
//import java.io.DataOutputStream;
//import java.io.IOException;
//import java.util.Collection;
//
//public class OutgoingEncryptedDataPacket extends SnowflakePacket {
//  private byte[] message;
//
//  public OutgoingEncryptedDataPacket(byte[] message) {
//    this.message = message;
//  }
//
//  public OutgoingEncryptedDataPacket(String message) {
//    JsonObject node = new JsonObject();
//    JsonObject object = new JsonObject();
//    object.addProperty("id", 1);
//    object.addProperty("message", message);
//    object.entrySet().forEach(entry -> node.add(entry.getKey(), entry.getValue()));
//
//    String jsonMessage = new GsonBuilder().setPrettyPrinting().create().toJson(node);
//
//
//    SnowflakeManager man = Snowball.getSnowflakeManager();
//    try {
//      Collection<String> pubKeys = man.getRecipientKeyMap().values();
//      PGPPublicKeyRing[] keys = new PGPPublicKeyRing[pubKeys.size()];
//
//      int i = 0;
//      for (String key : pubKeys) {
//        keys[i++] = PGPainless.readKeyRing().publicKeyRing(key);
//      }
//
//      this.message = EncryptionUtil.encryptMany(jsonMessage, keys);
//    } catch (IOException e) {
//      e.printStackTrace();
//    }
//  }
//
//  public OutgoingEncryptedDataPacket(double posX, double posY, double posZ, byte dimension) {
//    JsonObject node = new JsonObject();
//    JsonObject object = new JsonObject();
//    object.addProperty("id", 2);
//    object.addProperty("posX", posX);
//    object.addProperty("posY", posY);
//    object.addProperty("posZ", posZ);
//    object.addProperty("dimension", dimension);
//    object.entrySet().forEach(entry -> node.add(entry.getKey(), entry.getValue()));
//
//    String jsonMessage = new GsonBuilder().setPrettyPrinting().create().toJson(node);
//
//
//    SnowflakeManager man = Snowball.getSnowflakeManager();
//    try {
//      Collection<String> pubKeys = man.getRecipientKeyMap().values();
//      PGPPublicKeyRing[] keys = new PGPPublicKeyRing[pubKeys.size()];
//
//      int i = 0;
//      for (String key : pubKeys) {
//        keys[i++] = PGPainless.readKeyRing().publicKeyRing(key);
//      }
//
//      this.message = EncryptionUtil.encryptMany(jsonMessage, keys);
//    } catch (IOException e) {
//      e.printStackTrace();
//    }
//  }
//
//  public byte[] getMessage() {
//    return message;
//  }
//
//  @Override
//  public void writeData(DataOutputStream out) throws IOException {
//    out.writeByte(7);
//    out.writeInt(message.length);
//    out.write(message);
//  }
//
//  @Override
//  public void handle() {
//
//  }
//}
