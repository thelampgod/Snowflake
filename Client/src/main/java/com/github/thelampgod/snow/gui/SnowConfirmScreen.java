package com.github.thelampgod.snow.gui;

import com.github.thelampgod.snow.Snow;
import com.google.common.collect.Lists;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.List;

import static com.github.thelampgod.snow.Helper.mc;

public class SnowConfirmScreen extends Screen {
    private final Runnable onAccept;
    private final List<ButtonWidget> buttons;
    public SnowConfirmScreen(String title, Runnable onAccept) {
        super(Text.literal(title));
        this.onAccept = onAccept;
        this.buttons = Lists.newArrayList();
    }

    protected void init() {
        super.init();
        this.buttons.clear();
        this.addButtons();
    }


    protected void addButtons() {
        this.addButton(ButtonWidget.builder(Text.literal("Yes"), (button) -> {
            onAccept.run();
        }).dimensions(this.width / 2 - 155, (this.height - 10) / 2, 150, 20).build());
        this.addButton(ButtonWidget.builder(Text.literal("Cancel"), (button) -> {
            mc.setScreen(Snow.instance.getOrCreateSnowScreen());
        }).dimensions(this.width / 2 - 155 + 160, (this.height - 10) / 2, 150, 20).build());
    }

    protected void addButton(ButtonWidget button) {
        this.buttons.add((ButtonWidget)this.addDrawableChild(button));
    }

    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, (this.height - 40) / 2, 16777215);
    }
}
