package com.github.thelampgod.snow.gui.elements;

import com.github.thelampgod.snow.Snow;
import com.google.common.collect.Maps;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

import java.awt.*;
import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class DropdownListElement {

    private final int x;
    private int y;
    private final int width;
    private final int height;
    private final TextRenderer textRenderer;

    private final Map<String, OptionButton> options = Maps.newHashMap();

    private boolean dropdownOpened = false;

    public DropdownListElement(TextRenderer textRenderer, int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.textRenderer = textRenderer;
    }

    public void addOption(String name, Runnable onSelect) {
        options.put(name, new OptionButton(name, onSelect));
    }

    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        String selectedIdentityName = Snow.instance.getIdentityManager().getSelectedIdentity().getName();
        if (!dropdownOpened) {
            options.get(selectedIdentityName)
                    .render(ctx, mouseX, mouseY, 0);
            return;
        }

        AtomicInteger yIndex = new AtomicInteger();
        options.values().stream()
                .sorted(Comparator.comparing(
                        OptionButton::getName,
                        Comparator.comparing(s -> Objects.equals(s, selectedIdentityName)))
                        .reversed())
                        .forEach(button -> button.render(ctx, mouseX, mouseY, yIndex.getAndIncrement()));
    }


    public void mouseClicked(double mouseX, double mouseY, int button) {
        if (dropdownOpened) {
            // select mode
            String selectedIdentityName = Snow.instance.getIdentityManager().getSelectedIdentity().getName();
            AtomicInteger y = new AtomicInteger();
            options.values().stream()
                    .sorted(Comparator.comparing(
                                    OptionButton::getName,
                                    Comparator.comparing(s -> Objects.equals(s, selectedIdentityName)))
                            .reversed())
                    .forEach(b -> b.mouseClicked(mouseX, mouseY, y.getAndIncrement()));
            dropdownOpened = false;
            return;
        }
        if (!cursorInElement(mouseX, mouseY)) return;

        //open dropdown mode
        dropdownOpened = true;
    }

    private boolean cursorInElement(double mouseX, double mouseY) {
        return mouseX - x > 0 && mouseX - x < width && mouseY - y > 0 && mouseY - y < height;
    }


    private class OptionButton {
        private final String name;
        private final Runnable onSelect;
        public OptionButton(String name, Runnable onSelect) {
            this.name = name;
            this.onSelect = onSelect;
        }

        public void render(DrawContext ctx, int mouseX, int mouseY, int yIndex) {
            int tempY = y;
            y += yIndex * height;
            boolean hovered = mouseHover(mouseX, mouseY, y);

            // Border
            ctx.fill(x,y,x+width,y+height, Color.LIGHT_GRAY.getRGB());
            // Background
            ctx.fill(x+1,y+1, x + width - 1, y + height - 1, hovered ? Color.LIGHT_GRAY.getRGB() : Color.BLACK.getRGB());
            // Option name
            ctx.drawText(textRenderer, name, x + 3, y + (height - textRenderer.fontHeight) / 2, Color.WHITE.getRGB(), true);
            y = tempY;
        }

        private boolean mouseHover(double mouseX, double mouseY, int by) {
            return mouseX - x > 0 && mouseX - x < width && mouseY - by > 0 && mouseY - by < height;
        }

        public void mouseClicked(double mouseX, double mouseY, int yIndex) {
            int tempY = y;
            y += yIndex * height;
            if (mouseHover(mouseX, mouseY, y)) {
                onSelect.run();
            }
            y = tempY;
        }

        public String getName() {
            return name;
        }
    }
}
