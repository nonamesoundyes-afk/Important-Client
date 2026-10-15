package dev.important;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public final class ImportantClient implements ClientModInitializer {
    public static final String MOD_ID = "important";
    public static final int ACCENT = 0xFF55B8FF;
    public static final int PANEL = 0xF0181B20;
    public static final int PANEL_LIGHT = 0xFF222831;
    public static final int TEXT = 0xFFF1F5F9;
    public static final int MUTED = 0xFF9AA7B5;
    private static KeyBinding openMenuKey;

    @Override
    public void onInitializeClient() {
        ImportantConfig.load();
        openMenuKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.important.open_gui", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_SHIFT,
                new KeyBinding.Category(Identifier.of(MOD_ID, "main"))));
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openMenuKey.wasPressed()) {
                if (client.currentScreen == null) client.setScreen(new ImportantScreen());
            }
        });
        HudElementRegistry.attachElementBefore(VanillaHudElements.CHAT, id("visual_hud"),
                (drawContext, tickCounter) -> renderHud(MinecraftClient.getInstance(), drawContext));
    }

    private static void renderHud(MinecraftClient client, net.minecraft.client.gui.DrawContext context) {
        if (client.player == null || client.options.hudHidden || client.currentScreen instanceof ImportantScreen) return;

        int scale = ImportantConfig.hudScale();
        int x = ImportantConfig.hudX();
        int y = ImportantConfig.hudY();

        if (ImportantConfig.enabled("Watermark")) {
            String label = "IMPORTANT";
            int w = client.textRenderer.getWidth(label);
            context.fill(x - 4, y - 4, x + w + 9, y + 15, PANEL);
            context.fill(x - 4, y - 4, x - 1, y + 15, ACCENT);
            context.drawTextWithShadow(client.textRenderer, label, x + 3, y, TEXT);
            y += 23 * scale / 100;
        }
        if (ImportantConfig.enabled("FPS")) {
            context.drawTextWithShadow(client.textRenderer, "FPS  " + client.getCurrentFps(), x, y, ACCENT);
            y += 12 * scale / 100;
        }
        if (ImportantConfig.enabled("Coordinates")) {
            String coords = String.format(java.util.Locale.ROOT, "XYZ  %.1f  %.1f  %.1f",
                    client.player.getX(), client.player.getY(), client.player.getZ());
            context.drawTextWithShadow(client.textRenderer, coords, x, y, TEXT);
            y += 12 * scale / 100;
        }
        if (ImportantConfig.enabled("Direction")) {
            context.drawTextWithShadow(client.textRenderer,
                    "FACING  " + client.player.getHorizontalFacing().asString().toUpperCase(java.util.Locale.ROOT),
                    x, y, MUTED);
            y += 12 * scale / 100;
        }
        if (ImportantConfig.enabled("Armor HUD")) {
            StringBuilder armor = new StringBuilder("ARMOR  ");
            for (net.minecraft.item.ItemStack stack : client.player.getArmorItems()) {
                if (!stack.isEmpty()) armor.append(stack.getDamage() >= 0 && stack.isDamageable()
                        ? Math.max(0, stack.getMaxDamage() - stack.getDamage()) * 100 / Math.max(1, stack.getMaxDamage()) + "% " : "■ ");
                else armor.append("— ");
            }
            context.drawTextWithShadow(client.textRenderer, armor.toString(), x, y, TEXT);
            y += 12 * scale / 100;
        }
        if (ImportantConfig.enabled("Potion HUD") && !client.player.getStatusEffects().isEmpty()) {
            int shown = 0;
            for (net.minecraft.entity.effect.StatusEffectInstance effect : client.player.getStatusEffects()) {
                if (shown++ >= 3) break;
                String label = effect.getEffectType().value().getName().getString();
                int ticks = effect.getDuration();
                String time = String.format(java.util.Locale.ROOT, "%d:%02d", ticks / 1200, (ticks / 20) % 60);
                context.drawTextWithShadow(client.textRenderer, label + "  " + time, x, y, MUTED);
                y += 12 * scale / 100;
            }
        }
        if (ImportantConfig.enabled("Keystrokes")) {
            int kx = client.getWindow().getScaledWidth() + ImportantConfig.keysX();
            int ky = client.getWindow().getScaledHeight() + ImportantConfig.keysY();
            drawKey(context, client, "W", kx + 25, ky, client.options.forwardKey.isPressed());
            drawKey(context, client, "A", kx, ky + 22, client.options.leftKey.isPressed());
            drawKey(context, client, "S", kx + 25, ky + 22, client.options.backKey.isPressed());
            drawKey(context, client, "D", kx + 50, ky + 22, client.options.rightKey.isPressed());
            drawKey(context, client, "SPACE", kx, ky + 44, client.options.jumpKey.isPressed());
        }
    }

    private static void drawKey(net.minecraft.client.gui.DrawContext context, MinecraftClient client,
                                String label, int x, int y, boolean pressed) {
        int w = label.equals("SPACE") ? 75 : 23;
        context.fill(x, y, x + w, y + 19, pressed ? ACCENT : PANEL);
        context.drawBorder(x, y, w, 19, 0xFF394454);
        int textW = client.textRenderer.getWidth(label);
        context.drawTextWithShadow(client.textRenderer, label, x + (w - textW) / 2, y + 6,
                pressed ? 0xFFFFFFFF : TEXT);
    }

    public static Identifier id(String path) {
        return Identifier.of(MOD_ID, path);
    }
}
