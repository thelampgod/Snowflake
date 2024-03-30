package com.github.thelampgod.snow.packets.impl.outgoing;

import com.github.thelampgod.snow.packets.SnowflakePacket;

import java.io.DataOutputStream;
import java.io.IOException;

public class OutgoingLocationPacket extends SnowflakePacket {

  private final double posX;
  private final double posY;
  private final double posZ;
  private final byte dimensionId;

  public OutgoingLocationPacket(double posX, double posY, double posZ, byte dimension) {
    this.posX = posX;
    this.posY = posY;
    this.posZ = posZ;
    this.dimensionId = dimension;
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

  @Override
  public void writeData(DataOutputStream out) throws IOException {
    out.writeByte(8);
    out.writeDouble(posX);
    out.writeDouble(posY);
    out.writeDouble(posZ);
    out.writeByte(dimensionId);
  }

  @Override
  public void handle() {

  }
}
