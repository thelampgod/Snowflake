package com.github.thelampgod.snow.render;

import com.github.thelampgod.snow.Snow;
import com.github.thelampgod.snow.users.User;
import com.google.common.collect.Maps;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

import java.awt.*;
import java.util.Map;

import static com.github.thelampgod.snow.Helper.mc;

public class WaypointRenderer {

    //todo: different color background/text per group
    private final Map<Integer, PositionData> toProcess = Maps.newConcurrentMap();
    private final Map<Integer, PositionData> toRender = Maps.newConcurrentMap();

    // Cleanup for new connection or disconnect
    public void close() {
        toProcess.clear();
        toRender.clear();
    }
    public void updatePoint(int userId, double x, double y, double z, byte dimension, int groupId) {
        toProcess.put(userId, new PositionData(x,y,z,dimension));
    }

    public void tick() {
        if (toProcess.isEmpty()) return;

        for (Map.Entry<Integer, PositionData> entry : toProcess.entrySet()) {
            toRender.computeIfAbsent(entry.getKey(), k -> entry.getValue());
            PositionData data = toRender.get(entry.getKey());
            data.tick(entry.getValue());
        }
    }


    public void render(MatrixStack stack, float tickDelta, Camera camera) {
        if (mc.world == null || toRender.isEmpty()) return;

        for (Map.Entry<Integer, PositionData> entry : toRender.entrySet()) {
            final PositionData position = transformPosition(entry.getValue());
            final User user = Snow.instance.getUserManager().get(entry.getKey());
            final Vec3d renderPos = camera.getPos();
            final Vec3d pos = new Vec3d(position.x,position.y,position.z).subtract(renderPos);

            renderWaypoint(user.getName(), pos.x, pos.y, pos.z, stack, camera, tickDelta);
        }
    }

    private PositionData transformPosition(PositionData positionData) {
        final boolean isNether = mc.world.getDimension().coordinateScale() == 8.0d;
        if (positionData.dimension != -1 && !isNether) {
            return positionData;
        }

        if (!isNether) {
            return positionData.toOverworld();
        }

        return positionData.toNether();
    }

    private static void renderWaypoint(String text, double x, double y, double z, MatrixStack stack, Camera camera, float tickDelta) {
        final EntityRenderDispatcher dispatcher = mc.getEntityRenderDispatcher();
        final VertexConsumerProvider consumers = mc.getBufferBuilders().getEntityVertexConsumers();

        stack.push();
        stack.translate(x, y, z);
        stack.multiply(dispatcher.getRotation());

        Vec3d pos = camera.getPos();

        double distance = Math.sqrt(dispatcher.getSquaredDistanceToCamera(x + pos.x, y + pos.y, z + pos.z));

        float scale = (float) MathHelper.clamp(distance * 0.03f / 10, 0.03, Double.MAX_VALUE);
        stack.scale(-scale, -scale, scale);
        Matrix4f matrix = stack.peek().getPositionMatrix();

        //draw text

        final float nameWidth = (float) mc.textRenderer.getWidth(text) / 2;
        Color color = new Color(-1);
        mc.textRenderer.drawWithOutline(Text.literal(text).asOrderedText(), -nameWidth, 0, color.getRGB(), new Color(0,0,0,0).getRGB(), matrix, consumers, 255);
        stack.pop();

    }

    public static class PositionData {
        private double x;
        private double y;
        private double z;
        private double prevX;
        private double prevY;
        private double prevZ;
        private byte dimension;

        public PositionData(double x, double y, double z, byte dimension) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.dimension = dimension;
        }

        public PositionData(double x, double y, double z, double prevX, double prevY, double prevZ, byte dimension) {
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

        public void setDimension(byte dimension) {
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
