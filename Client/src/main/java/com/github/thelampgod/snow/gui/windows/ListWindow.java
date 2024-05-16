package com.github.thelampgod.snow.gui.windows;

import com.github.thelampgod.snow.gui.elements.ButtonListElement;
import net.minecraft.client.gui.DrawContext;

public abstract class ListWindow extends SnowWindow {
    protected ButtonListElement buttonListElement;

    public ListWindow(String title, int width, int height, boolean closeable) {
        super(title, width, height, closeable);
    }

    @Override
    public void updateDimensions() {
        super.updateDimensions();
        buttonListElement.setWidth(this.width);
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        super.render(ctx, mouseX, mouseY, delta);

        buttonListElement.preRender(ctx, (int) (mouseX - x), (int) (mouseY - y), delta);
    }

    @Override
    public void mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        buttonListElement.mouseScrolled(mouseX - x, mouseY - y, horizontalAmount, verticalAmount);
    }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int buttonId) {
        super.mouseClicked(mouseX, mouseY, buttonId);
        buttonListElement.mouseClicked(mouseX - x, mouseY - y, buttonId);
    }
}
