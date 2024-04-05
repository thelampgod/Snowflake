package com.github.thelampgod.snow.render;

import com.github.thelampgod.snow.groups.Group;
import com.github.thelampgod.snow.users.User;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

import java.awt.*;

import static com.github.thelampgod.snow.Helper.mc;

public class WaypointRenderer {
    //todo: different color background/text per group
    public static void updatePoint(User user, double x, double y, double z, byte dimension, Group group) {

    }

    public static void tick() {

    }


    public static void render(MatrixStack stack, float tickDelta, Camera camera) {
        Vec3d renderPos = camera.getPos();
        Vec3d pos = new Vec3d(0,0,0).subtract(renderPos);


        renderWaypoint("text", pos.x, pos.y, pos.z, stack, camera);
    }

    private static void renderWaypoint(String text, double x, double y, double z, MatrixStack stack, Camera camera) {
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
}
