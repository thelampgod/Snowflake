//package com.github.thelampgod.snow.packets.impl;
//
//import com.github.thelampgod.snowball.module.impl.LocationShareMod;
//import com.github.thelampgod.snow.packets.SnowflakePacket;
//import io.netty.buffer.ByteBuf;
//import io.netty.buffer.Unpooled;
//import net.minecraft.client.multiplayer.WorldClient;
//import net.minecraft.entity.Entity;
//import net.minecraft.network.PacketBuffer;
//import net.minecraft.world.chunk.Chunk;
//import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
//import net.minecraftforge.fml.relauncher.Side;
//import net.minecraftforge.fml.relauncher.SideOnly;
//
//import java.io.DataInputStream;
//import java.io.DataOutputStream;
//import java.io.IOException;
//
//import static com.github.thelampgod.snowball.util.Helper.getModManager;
//import static com.github.thelampgod.snowball.util.Helper.mc;
//
//public class ChunkPacket extends SnowflakePacket {
//  private final int chunkX;
//  private final int chunkZ;
//  private final int availableSections;
//  private final byte[] buffer;
//
//  public ChunkPacket(Chunk chunk) {
//    this.chunkX = chunk.x;
//    this.chunkZ = chunk.z;
//    boolean flag = chunk.getWorld().provider.hasSkyLight();
//    this.buffer = new byte[this.calculateChunkSize(chunk, flag)];
//    int changedSectionFilter = 65535;
//    this.availableSections = this.extractChunkData(new PacketBuffer(this.getWriteBuffer()), chunk, flag, changedSectionFilter);
//  }
//
//  public ChunkPacket(DataInputStream in) throws IOException {
//    this.chunkX = in.readInt();
//    this.chunkZ = in.readInt();
//    this.availableSections = in.readInt();
//    this.buffer = new byte[in.readInt()];
//    in.readFully(this.buffer);
//  }
//
//  @Override
//  public void writeData(DataOutputStream out) throws IOException {
//    out.writeByte(11);
//    out.writeInt(this.chunkX);
//    out.writeInt(this.chunkZ);
//    out.writeInt(this.availableSections);
//    out.writeInt(this.buffer.length);
//    out.write(this.buffer);
//  }
//
//  @Override
//  public void handle() {
//    if (!getModManager().getMod(LocationShareMod.class).isToggled() || mc.world == null || !mc.world.getChunk(this.chunkX, this.chunkZ).isEmpty()) return;
//    if (mc.getRenderViewEntity() == null) return;
//    final Entity camera = mc.getRenderViewEntity();
//    if (camera.getDistance(chunkX << 4, camera.posY, chunkZ << 4) > 1000.0D) return;
//
//    handleChunkData(mc.world);
//  }
//
//  /**
//   * From NetHandlerPlayClient::handleChunkData
//   */
//  public void handleChunkData(WorldClient world) {
//    if (this.isFullChunk()) {
//      world.doPreChunk(this.chunkX, this.chunkZ, true);
//    }
//
//    world.invalidateBlockReceiveRegion(this.chunkX << 4, 0, this.chunkZ << 4, (this.chunkX << 4) + 15, 256, (this.chunkZ << 4) + 15);
//    Chunk chunk = world.getChunk(this.chunkX, this.chunkZ);
//    chunk.read(this.getReadBuffer(), this.availableSections, this.isFullChunk());
//    world.markBlockRangeForRenderUpdate(this.chunkX << 4, 0, this.chunkZ << 4, (this.chunkX << 4) + 15, 256, (this.chunkZ << 4) + 15);
//
//    if (!this.isFullChunk() || world.provider.shouldClientCheckLighting()) {
//      chunk.resetRelightChecks();
//    }
//  }
//
//  private boolean isFullChunk() {
//    return true;
//  }
//
//  @SideOnly(Side.CLIENT)
//  public PacketBuffer getReadBuffer() {
//    return new PacketBuffer(Unpooled.wrappedBuffer(this.buffer));
//  }
//
//  private ByteBuf getWriteBuffer() {
//    ByteBuf bytebuf = Unpooled.wrappedBuffer(this.buffer);
//    bytebuf.writerIndex(0);
//    return bytebuf;
//  }
//
//  public int extractChunkData(PacketBuffer buf, Chunk chunkIn, boolean writeSkylight, int changedSectionFilter) {
//    int i = 0;
//    ExtendedBlockStorage[] aextendedblockstorage = chunkIn.getBlockStorageArray();
//    int j = 0;
//
//    for (int k = aextendedblockstorage.length; j < k; ++j) {
//      ExtendedBlockStorage extendedblockstorage = aextendedblockstorage[j];
//
//      if (extendedblockstorage != Chunk.NULL_BLOCK_STORAGE && (!this.isFullChunk() || !extendedblockstorage.isEmpty()) && (changedSectionFilter & 1 << j) != 0) {
//        i |= 1 << j;
//        extendedblockstorage.getData().write(buf);
//        buf.writeBytes(extendedblockstorage.getBlockLight().getData());
//
//        if (writeSkylight) {
//          buf.writeBytes(extendedblockstorage.getSkyLight().getData());
//        }
//      }
//    }
//
//    if (this.isFullChunk()) {
//      buf.writeBytes(chunkIn.getBiomeArray());
//    }
//
//    return i;
//  }
//
//  protected int calculateChunkSize(Chunk chunkIn, boolean p_189556_2_) {
//    int i = 0;
//    ExtendedBlockStorage[] aextendedblockstorage = chunkIn.getBlockStorageArray();
//    int j = 0;
//
//    for (int k = aextendedblockstorage.length; j < k; ++j) {
//      ExtendedBlockStorage extendedblockstorage = aextendedblockstorage[j];
//
//      if (extendedblockstorage != Chunk.NULL_BLOCK_STORAGE && (!this.isFullChunk() || !extendedblockstorage.isEmpty()) && (65535 & 1 << j) != 0) {
//        i = i + extendedblockstorage.getData().getSerializedSize();
//        i = i + extendedblockstorage.getBlockLight().getData().length;
//
//        if (p_189556_2_) {
//          i += extendedblockstorage.getSkyLight().getData().length;
//        }
//      }
//    }
//
//    if (this.isFullChunk()) {
//      i += chunkIn.getBiomeArray().length;
//    }
//
//    return i;
//  }
//}
