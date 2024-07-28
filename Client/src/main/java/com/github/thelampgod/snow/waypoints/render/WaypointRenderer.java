package com.github.thelampgod.snow.waypoints.render;

import com.github.thelampgod.snow.Snow;
import com.github.thelampgod.snow.mixins.impl.ClientPlayNetworkHandlerAccessor;
import com.github.thelampgod.snow.users.User;
import com.github.thelampgod.snow.util.DrawUtil;
import com.github.thelampgod.snow.util.Helper;
import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.s2c.play.PlayerRemoveS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.github.thelampgod.snow.util.Helper.getDimensionFromId;
import static com.github.thelampgod.snow.util.Helper.mc;

public class WaypointRenderer {

    //todo: different color background/text per group
    private final Map<Integer, PositionData> toProcess = Maps.newConcurrentMap();
    private final Map<Integer, PositionData> toRender = Maps.newConcurrentMap();
    private final Map<Integer, Long> lastUpdateMap = Maps.newConcurrentMap();

    // Cleanup for new connection or disconnect
    public void clear() {
        for (int id : toRender.keySet()) {
            this.removePlayerRender(id);
        }
        toProcess.clear();
        toRender.clear();
    }

    public void removePoint(int userId) {
        toProcess.remove(userId);
        toRender.remove(userId);
        this.removePlayerRender(userId);
    }

    public void updatePoint(int userId, double x, double y, double z, byte dimensionId, int groupId) {
        if (Snow.instance.getUserManager().getMe() == userId) return;

        Vec3d pos = new Vec3d(x, y, z);
        if (pos.distanceTo(Vec3d.ZERO) > Snow.instance.getConfig().maxRange.get()) {
            return;
        }

        String dimension = getDimensionFromId(dimensionId);

        toProcess.put(userId, new PositionData(x, y, z, dimension));
        lastUpdateMap.put(userId, System.currentTimeMillis());
    }

    public void tick() {
        if (toProcess.isEmpty()) return;

        for (Map.Entry<Integer, PositionData> entry : toProcess.entrySet()) {
            PositionData data = toRender.computeIfAbsent(entry.getKey(), k -> entry.getValue());
            data.tick(entry.getValue());
        }
    }


    public void render(MatrixStack stack, float tickDelta, Camera camera) {
        if (mc.world == null || toRender.isEmpty()) return;

        for (Map.Entry<Integer, PositionData> entry : toRender.entrySet()) {
            if (System.currentTimeMillis() > lastUpdateMap.get(entry.getKey()) + TimeUnit.SECONDS.toMillis(3)) {
                this.removePoint(entry.getKey());
            }

            final PositionData position = transformPosition(entry.getValue());
            final User user = Snow.instance.getUserManager().get(entry.getKey());
            if (user == null) return;
            final Vec3d renderPos = camera.getPos();

            double interpolatedX = DrawUtil.interpolate(position.prevX, position.x, tickDelta);
            double interpolatedY = DrawUtil.interpolate(position.prevY, position.y, tickDelta);
            double interpolatedZ = DrawUtil.interpolate(position.prevZ, position.z, tickDelta);

            double deltaX = interpolatedX - renderPos.x;
            double deltaZ = interpolatedZ - renderPos.z;
            double deltaY = interpolatedY - renderPos.y;

            final double distanceTo = deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ;
            final double distanceToIgnoringY = deltaX * deltaX + deltaZ * deltaZ;

            int viewDistance = (mc.options.getClampedViewDistance() / 2 - 2) * 16;
            if (toRender.containsKey(user.getId())) {
                renderPlayer(user, interpolatedX, interpolatedY, interpolatedZ);
            }
            //TODO: distance setting
            if (mc.world.getDimensionKey().getValue().getPath().equals(position.dimension)) {
                if (distanceToIgnoringY < viewDistance * viewDistance) {
                    // Don't render player
                    this.removePlayerRender(user.getId());
                }

                // Don't render waypoint
                if (distanceTo < viewDistance * viewDistance) {
                    continue;
                }
            }

            double yaw = Math.atan2(deltaZ, deltaX);
            double pitch = Math.atan2(Math.sqrt(deltaZ * deltaZ + deltaX * deltaX), deltaY);

            Vec3d dir = new Vec3d(Math.sin(pitch) * Math.cos(yaw), Math.cos(pitch), Math.sin(pitch) * Math.sin(yaw));
            Vec3d pos = new Vec3d(
                    renderPos.x + 0.3 * dir.x,
                    renderPos.y + 0.3 * dir.y,
                    renderPos.z + 0.3 * dir.z).subtract(renderPos);

            renderWaypoint(user.getName(), pos.x, pos.y, pos.z, stack, camera, distanceTo, position.dimension);
        }
    }

    private void removePlayerRender(int userId) {
        if (!Snow.instance.xaeroLoaded || mc.world == null || mc.getNetworkHandler() == null) return;
        final UUID id = Helper.uuidFromId(userId);

        mc.getNetworkHandler().onPlayerRemove(new PlayerRemoveS2CPacket(List.of(id)));

        List<AbstractClientPlayerEntity> players = mc.world.getPlayers();
        Optional<AbstractClientPlayerEntity> player = players.stream()
                .filter(pl -> pl.getUuid().equals(id))
                .findAny();

        player.ifPresent(entity -> mc.world.removeEntity(entity.getId(), Entity.RemovalReason.DISCARDED));
    }

    private void renderPlayer(User user, double x, double y, double z) {
        if (Snow.instance.xaeroLoaded && mc.world != null && mc.getNetworkHandler() != null) {
            final String name = user.getName();
            final UUID id = Helper.uuidFromId(user.getId());

            ((ClientPlayNetworkHandlerAccessor) mc.getNetworkHandler()).getPlayerListEntries().putIfAbsent(
                    id,
                    new PlayerListEntry(new GameProfile(id, name), false)
            );

            AbstractClientPlayerEntity ent;
            List<AbstractClientPlayerEntity> players = mc.world.getPlayers();
            Optional<AbstractClientPlayerEntity> player = players.stream()
                    .filter(pl -> pl.getUuid().equals(id))
                    .findAny();

            if (!player.isPresent()) {
                ent = new OtherClientPlayerEntity(mc.world, new GameProfile(id, name));
                mc.world.addEntity(ent);
            } else {
                ent = player.get();
            }
            ent.setPosition(x, y - mc.player.getHeight(), z);
        }
    }

    private PositionData transformPosition(PositionData positionData) {
        final String myDimension = mc.world.getDimensionKey().getValue().getPath();

        if (myDimension.equals("overworld") && positionData.dimension.equals("the_nether")) {
            return positionData.toOverworld();
        }

        if (myDimension.equals("the_nether") && positionData.dimension.equals("overworld")) {
            return positionData.toNether();
        }

        return positionData;
    }

    private void renderWaypoint(String text, double x, double y, double z, MatrixStack stack, Camera camera, double distance, String dimension) {
        if (mc.options.hudHidden) return;

        final EntityRenderDispatcher dispatcher = mc.getEntityRenderDispatcher();
        final VertexConsumerProvider.Immediate consumers = mc.getBufferBuilders().getEntityVertexConsumers();

        stack.push();
        stack.translate(x, y, z);
        stack.multiply(dispatcher.getRotation());

        Vec3d pos = camera.getPos();

        double camDistance = Math.sqrt(dispatcher.getSquaredDistanceToCamera(x + pos.x, y + pos.y, z + pos.z));
        String distanceString = formatDistance(Math.sqrt(distance));

        //TODO: setting
        float scale = (float) MathHelper.clamp(camDistance * 0.03f / 10, 0.001, Double.MAX_VALUE);
        stack.scale(-scale, -scale, scale);
        Matrix4f matrix = stack.peek().getPositionMatrix();

        //draw text
        final float nameWidth = (float) mc.textRenderer.getWidth(text) / 2;
        Color color = new Color(-1);
//        DrawUtil.fill((int) (-nameWidth - 5), -5, (int) (nameWidth + 5), mc.textRenderer.fontHeight * 3 + 5, new Color(0,0,0,50).getRGB());
        mc.textRenderer.drawWithOutline(Text.literal(text).asOrderedText(), -nameWidth, 0, color.getRGB(), new Color(0, 0, 0, 0).getRGB(), matrix, consumers, 255);
        int yPos = 1;
        this.drawText(distanceString, yPos++, matrix, consumers);

        if (!mc.world.getDimensionKey().getValue().getPath().equals(dimension)) {
            this.drawText("(" + dimension + ")", yPos, matrix, consumers);
        }


        stack.pop();

    }

    public static String formatDistance(double distanceInMeters) {
        if (distanceInMeters < 1000) {
            return String.format("%.0fm", distanceInMeters);
        } else if (distanceInMeters < 1000000) {
            double distanceInKilometers = distanceInMeters / 1000;
            if (distanceInKilometers % 1 == 0) {
                return String.format("%.0fkm", distanceInKilometers);
            }
            return String.format("%.1fkm", distanceInKilometers);
        } else {
            double distanceInMillions = distanceInMeters / 1000000;
            if (distanceInMillions % 1 == 0) {
                return String.format("%.0fM", distanceInMillions);
            }
            return String.format("%.1fM", distanceInMillions);
        }
    }

    private void drawText(String text, int yPos, Matrix4f matrix, VertexConsumerProvider consumers) {
        final float distanceWidth = (float) mc.textRenderer.getWidth(text) / 2;
        mc.textRenderer.draw(text, -distanceWidth, yPos * mc.textRenderer.fontHeight,
                Color.WHITE.getRGB(), false, matrix, consumers, TextRenderer.TextLayerType.SEE_THROUGH,
                0, 255);
    }

    public static class PositionData {
        private double x;
        private double y;
        private double z;
        private double prevX;
        private double prevY;
        private double prevZ;
        private String dimension;

        public PositionData(double x, double y, double z, String dimension) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.dimension = dimension;
        }

        public PositionData(double x, double y, double z, double prevX, double prevY, double prevZ, String dimension) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.prevX = prevX;
            this.prevY = prevY;
            this.prevZ = prevZ;
            this.dimension = dimension;
        }

        public void setX(double x) {
            this.prevX = this.x;
            this.x = x;
        }

        public void setY(double y) {
            this.prevY = this.y;
            this.y = y;
        }

        public void setZ(double z) {
            this.prevZ = this.z;
            this.z = z;
        }

        public PositionData toNether() {
            double x = this.x / 8;
            double z = this.z / 8;
            double prevX = this.prevX / 8;
            double prevZ = this.prevZ / 8;
            return new PositionData(x, this.y, z, prevX, this.prevY, prevZ, this.dimension);
        }

        public PositionData toOverworld() {
            double x = this.x * 8;
            double z = this.z * 8;
            double prevX = this.prevX * 8;
            double prevZ = this.prevZ * 8;
            return new PositionData(x, this.y, z, prevX, this.prevY, prevZ, this.dimension);
        }

        public void setDimension(String dimension) {
            this.dimension = dimension;
        }

        public void tick(PositionData data) {
            this.setX(data.x);
            this.setY(data.y);
            this.setZ(data.z);
            this.setDimension(data.dimension);
        }
    }
}
