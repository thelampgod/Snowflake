package com.github.thelampgod.snow.waypoints.share;

import com.github.thelampgod.snow.Snow;
import com.github.thelampgod.snow.groups.Group;
import com.github.thelampgod.snow.packets.impl.EncryptedDataPacket;
import com.github.thelampgod.snow.packets.impl.LocationPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.TimeUnit;

import static com.github.thelampgod.snow.util.Helper.getDimensionId;
import static com.github.thelampgod.snow.util.Helper.mc;

public class WaypointSharer {
    private Group selectedGroup;

    private Vec3d lastPos;
    private long lastPacketSent;

    public void onLocationSend(PlayerMoveC2SPacket packet) {
        if (mc.world == null) return;
        if (selectedGroup == null) return;
        float height = mc.player.getHeight();

        double x = packet.getX(0);
        double y = packet.getY(0) + height;
        double z = packet.getZ(0);
        byte dimension = getDimensionId(mc.world.getDimensionKey().getValue().getPath());

        Vec3d pos = new Vec3d(x, y, z);
        if (pos.equals(lastPos) && System.currentTimeMillis() < lastPacketSent + TimeUnit.SECONDS.toMillis(1)) {
            return;
        }

        if (pos.distanceTo(Vec3d.ZERO) > Snow.instance.getConfigManager().maxRange.get()) {
            return;
        }

        try {
            Snow.getServerManager().sendPacket(
                    new EncryptedDataPacket.Group(selectedGroup.getId(),
                            new LocationPacket(selectedGroup.getId(), x, y, z, dimension)));
            lastPos = pos;
            lastPacketSent = System.currentTimeMillis();
        } catch (Exception e) {
            Snow.instance.getLog().error("Error encrypting location: " + e.getMessage(), e);
        }
    }

    public void onWorldUnload() {
        this.clear();
        Snow.instance.getOrCreateSnowScreen().clearShareStatus();
    }

    public void select(Group group) {
        selectedGroup = group;
    }

    public void clear() {
        selectedGroup = null;
    }

    @Nullable
    public Group getSelectedGroup() {
        return selectedGroup;
    }
}
