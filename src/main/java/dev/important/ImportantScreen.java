package dev.important;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class ImportantScreen extends Screen {
    private static final String[] CATEGORIES = {"All", "HUD", "Particles", "Render", "Elements"};
    private String category = "All";
    private int scroll = 0;
    private int panelX, panelY, panelW, panelH, sideW;
    private final List<ButtonWidget> moduleButtons = new ArrayList<>();

    public ImportantScreen() {
        super(Text.literal("Important"));
    }

    @Override
    protected void init() {
        rebuildButtons();
    }

    private void rebuildButtons() {
        clearChildren();
        moduleButtons.clear();

        panelW = Math.min(780, width - 32);
        panelH = Math.min(500, height - 32);
        panelX = (width - panelW) / 2;
        panelY = (height - panelH) / 2;
        sideW = Math.min(164, Math.max(128, panelW / 4));

        int navY = panelY + 104;
        for (int i = 0; i < CATEGORIES.length; i++) {
            String cat = CATEGORIES[i];
            int y = navY + i * 31;
            addDrawableChild(ButtonWidget.builder(Text.literal((category.equals(cat) ? "●  " : "   ") + cat),
                    b -> { category = cat; scroll = 0; rebuildButtons(); })
                    .dimensions(panelX + 14, y, sideW - 28, 25).build());
        }

        addDrawableChild(ButtonWidget.builder(Text.literal("Accent  " + ImportantConfig.accent()),
                b -> { ImportantConfig.cycleAccent(); rebuildButtons(); })
                .dimensions(panelX + 14, panelY + panelH - 104, sideW - 28, 23).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Edit HUD  ·  " + ImportantConfig.hudScale() + "%"),
                b -> client.setScreen(new ImportantHudEditor()))
                .dimensions(panelX + 14, panelY + panelH - 76, sideW - 28, 23).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Reset"),
                b -> { ImportantConfig.reset(); rebuildButtons(); })
                .dimensions(panelX + 14, panelY + panelH - 47, 62, 23).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Done"), b -> close())
                .dimensions(panelX + sideW - 76, panelY + panelH - 47, 62, 23).build());

        Map<String, Boolean> all = ImportantConfig.all();
        List<String> visible = all.keySet().stream().filter(this::matches).toList();
        int contentX = panelX + sideW + 20;
        int contentW = panelW - sideW - 38;
        int startY = panelY + 128;
        int colGap = 12;
        int colW = Math.max(100, (contentW - colGap) / 2);
        int rows = Math.max(1, (panelY + panelH - 91 - startY) / 58);
        int maxScroll = Math.max(0, (int)Math.ceil(visible.size() / 2.0) - rows);
        scroll = Math.min(scroll, maxScroll);

        for (int i = scroll * 2; i < visible.size() && i < (scroll + rows) * 2; i++) {
            String name = visible.get(i);
            int row = (i / 2) - scroll;
            int col = i % 2;
            int x = contentX + col * (colW + colGap);
            int y = startY + row * 58;
            String state = ImportantConfig.enabled(name) ? "ON" : "OFF";
            ButtonWidget button = ButtonWidget.builder(Text.literal(name + "   ·   " + state),
                    b -> { ImportantConfig.toggle(name); rebuildButtons(); })
                    .dimensions(x, y, colW, 48).build();
            moduleButtons.add(button);
            addDrawableChild(button);
        }
    }

    private boolean matches(String name) {
        if (category.equals("All")) return true;
        return switch (category) {
            case "HUD" -> List.of("Watermark", "FPS", "Coordinates", "Direction", "Keystrokes",
                    "CPS Counter", "Armor HUD", "Potion HUD", "Target HUD").contains(name);
            case "Particles" -> List.of("Hit Particles", "Particle Trail", "Enchantment Glow").contains(name);
            case "Render" -> List.of("Smooth Zoom", "No Hurt Cam", "Block Overlay", "Custom Sky", "Low Fire").contains(name);
            case "Elements" -> List.of("Clean Chat", "Custom Crosshair", "Minimal Scoreboard").contains(name);
            default -> false;
        };
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Intentionally do not call renderBackground(): on some 1.21.11 setups it can
        // trigger the vanilla blur pass twice and crash with "Can only blur once per frame".
        context.fill(0, 0, width, height, 0xE9080A10);
        context.fill(panelX, panelY, panelX + panelW, panelY + panelH, 0xF20D1017);
        context.fill(panelX, panelY, panelX + sideW, panelY + panelH, 0xFF0A0C12);
        context.fill(panelX, panelY, panelX + panelW, panelY + 2, colorAccent());
        context.fill(panelX + sideW, panelY + 58, panelX + sideW + 1, panelY + panelH - 16, 0xFF252936);

        context.drawTextWithShadow(textRenderer, "Important", panelX + 18, panelY + 20, 0xFFF4F6FC);
        context.drawTextWithShadow(textRenderer, "VISUAL CLIENT", panelX + 18, panelY + 37, ImportantClient.MUTED);
        context.drawTextWithShadow(textRenderer, "VISUALS", panelX + 18, panelY + 82, ImportantClient.MUTED);

        int contentX = panelX + sideW + 20;
        context.drawTextWithShadow(textRenderer, category.equals("All") ? "Client modules" : category,
                contentX, panelY + 25, ImportantClient.TEXT);
        context.drawTextWithShadow(textRenderer, "Search modules  ·  visual-only settings",
                contentX, panelY + 43, ImportantClient.MUTED);
        context.fill(contentX, panelY + 66, panelX + panelW - 18, panelY + 94, 0xFF141821);
        context.drawTextWithShadow(textRenderer, "⌕   Browse " + category.toLowerCase(java.util.Locale.ROOT) + " modules",
                contentX + 10, panelY + 76, ImportantClient.MUTED);

        context.drawTextWithShadow(textRenderer, "MODULES", contentX, panelY + 111, ImportantClient.MUTED);
        context.drawTextWithShadow(textRenderer, "Settings save automatically",
                contentX, panelY + panelH - 27, ImportantClient.MUTED);
        super.render(context, mouseX, mouseY, delta);
    }

    private int colorAccent() {
        return switch (ImportantConfig.accent()) {
            case "#B18CFF" -> 0xFFB18CFF;
            case "#70E0A0" -> 0xFF70E0A0;
            default -> ImportantClient.ACCENT;
        };
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        scroll = Math.max(0, scroll - (int)Math.signum(verticalAmount));
        rebuildButtons();
        return true;
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
