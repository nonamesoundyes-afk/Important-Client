package dev.important;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public final class ImportantHudEditor extends Screen {
    private boolean draggingMain;
    private boolean draggingKeys;
    private int dragOffsetX, dragOffsetY;

    public ImportantHudEditor() { super(Text.literal("Important HUD Editor")); }

    @Override
    protected void init() {
        addDrawableChild(ButtonWidget.builder(Text.literal("Scale: " + ImportantConfig.hudScale() + "%"),
                b -> { ImportantConfig.cycleHudScale(); b.setMessage(Text.literal("Scale: " + ImportantConfig.hudScale() + "%")); })
                .dimensions(width / 2 - 104, height - 36, 100, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Done"), b -> close())
                .dimensions(width / 2 + 4, height - 36, 100, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(0, 0, width, height, 0xB9080A10);
        context.drawCenteredTextWithShadow(textRenderer, Text.literal("IMPORTANT  /  HUD EDITOR"), width / 2, 18, 0xFFF4F6FC);
        context.drawCenteredTextWithShadow(textRenderer, "Drag the preview cards to move them. Changes save automatically.",
                width / 2, 34, ImportantClient.MUTED);

        int x = ImportantConfig.hudX(), y = ImportantConfig.hudY();
        int mainW = Math.max(142, textRenderer.getWidth("XYZ  -123.4  64.0  123.4") + 24);
        int mainH = 78;
        drawCard(context, x, y, mainW, mainH, draggingMain);
        context.drawTextWithShadow(textRenderer, "IMPORTANT", x + 10, y + 8, 0xFFF4F6FC);
        context.drawTextWithShadow(textRenderer, "FPS  144", x + 10, y + 24, ImportantClient.ACCENT);
        context.drawTextWithShadow(textRenderer, "XYZ  -123.4  64.0  123.4", x + 10, y + 38, ImportantClient.TEXT);
        context.drawTextWithShadow(textRenderer, "FACING  NORTH", x + 10, y + 52, ImportantClient.MUTED);

        int kx = width + ImportantConfig.keysX();
        int ky = height + ImportantConfig.keysY();
        drawCard(context, kx, ky, 82, 66, draggingKeys);
        context.fill(kx + 29, ky + 5, kx + 51, ky + 23, 0xFF222831);
        context.fill(kx + 7, ky + 27, kx + 29, ky + 45, 0xFF222831);
        context.fill(kx + 29, ky + 27, kx + 51, ky + 45, 0xFF222831);
        context.fill(kx + 51, ky + 27, kx + 73, ky + 45, 0xFF222831);
        context.drawCenteredTextWithShadow(textRenderer, Text.literal("W"), kx + 40, ky + 10, ImportantClient.TEXT);
        context.drawCenteredTextWithShadow(textRenderer, Text.literal("A"), kx + 18, ky + 32, ImportantClient.TEXT);
        context.drawCenteredTextWithShadow(textRenderer, Text.literal("S"), kx + 40, ky + 32, ImportantClient.TEXT);
        context.drawCenteredTextWithShadow(textRenderer, Text.literal("D"), kx + 62, ky + 32, ImportantClient.TEXT);
        context.fill(kx + 7, ky + 49, kx + 73, ky + 60, 0xFF222831);
        context.drawCenteredTextWithShadow(textRenderer, Text.literal("SPACE"), kx + 40, ky + 50, ImportantClient.TEXT);
        super.render(context, mouseX, mouseY, delta);
    }

    private void drawCard(DrawContext c, int x, int y, int w, int h, boolean selected) {
        c.fill(x, y, x + w, y + h, 0xF0181B24);
        c.fill(x, y, x + w, y + 2, selected ? 0xFFB18CFF : ImportantClient.ACCENT);
        c.fill(x, y + h - 1, x + w, y + h, 0xFF384150);
        c.fill(x, y, x + 1, y + h, 0xFF384150);
        c.fill(x + w - 1, y, x + w, y + h, 0xFF384150);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            int x = ImportantConfig.hudX(), y = ImportantConfig.hudY();
            int mainW = Math.max(142, textRenderer.getWidth("XYZ  -123.4  64.0  123.4") + 24);
            if (mouseX >= x && mouseX <= x + mainW && mouseY >= y && mouseY <= y + 78) {
                draggingMain = true; draggingKeys = false; dragOffsetX = (int) mouseX - x; dragOffsetY = (int) mouseY - y; return true;
            }
            int kx = width + ImportantConfig.keysX(), ky = height + ImportantConfig.keysY();
            if (mouseX >= kx && mouseX <= kx + 82 && mouseY >= ky && mouseY <= ky + 66) {
                draggingKeys = true; draggingMain = false; dragOffsetX = (int) mouseX - kx; dragOffsetY = (int) mouseY - ky; return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (button == 0 && draggingMain) {
            ImportantConfig.setHudPosition((int) mouseX - dragOffsetX, (int) mouseY - dragOffsetY);
            return true;
        }
        if (button == 0 && draggingKeys) {
            ImportantConfig.setKeysPosition((int) mouseX - dragOffsetX - width, (int) mouseY - dragOffsetY - height);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) { draggingMain = false; draggingKeys = false; }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override public boolean shouldPause() { return false; }
}
