package com.github.thelampgod.snow.packets.impl;

import com.github.thelampgod.snow.packets.SnowflakePacket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class IncomingLocationPacket extends SnowflakePacket {

  private final int sender;
  private final double posX;
  private final double posY;
  private final double posZ;
  private final byte dimensionId;

  public IncomingLocationPacket(int sender, double posX, double posY, double posZ, byte dimension) {
    this.sender = sender;
    this.posX = posX;
    this.posY = posY;
    this.posZ = posZ;
    this.dimensionId = dimension;
  }

  public IncomingLocationPacket(DataInputStream in) throws IOException {
    this(in.readInt(), in.readDouble(), in.readDouble(), in.readDouble(), in.readByte());
  }

  public int getSender() {
    return sender;
  }

  public double getPosX() {
    return posX;
  }

  public double getPosY() {
    return posY;
  }

  public double getPosZ() {
    return posZ;
  }

  public byte getDimension() {
    return dimensionId;
  }

  @Override
  public void writeData(DataOutputStream out) {

  }

  @Override
  public void handle() {

  }
}
