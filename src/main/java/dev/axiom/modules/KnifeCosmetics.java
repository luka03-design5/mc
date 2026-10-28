package dev.axiom.modules;

import dev.axiom.module.Module;
import dev.axiom.module.ModuleManager;
import net.minecraft.util.Identifier;

/** CS-Style Messer als Visual (Model-Swap + Inspect). Kein Gameplay-Vorteil. */
public final class KnifeCosmetics extends Module {
    public enum Knife {
        KARAMBIT("Karambit", "karambit"),
        BAYONET("Bayonet", "bayonet"),
        M9("M9 Bayonet", "m9"),
        SKELETON("Skeleton Knife", "skeleton"),
        BUTTERFLY("Butterfly", "butterfly"),
        SWORD_3D("3D Sword", "sword3d");

        public final String label;
        public final String model;

        Knife(String label, String model) {
            this.label = label;
            this.model = model;
        }
    }

    private static int index = 0;
    /** remaining inspect ticks */
    public static int inspectTicks = 0;

    public KnifeCosmetics() {
        super("Messer", "Knives", "Cosmetics", "Cosmetics",
            "3D CS-Messer + Inspect", "3D CS knives + inspect");
        setEnabled(true);
    }

    public static int index() { return index; }

    public static void setIndex(int i) {
        Knife[] all = Knife.values();
        index = Math.floorMod(i, all.length);
        ModuleManager.save();
    }

    public static void next() { setIndex(index + 1); }
    public static void prev() { setIndex(index - 1); }

    public static Knife current() {
        Knife[] all = Knife.values();
        return all[Math.floorMod(index, all.length)];
    }

    public static Identifier modelId() {
        return Identifier.of("spotifylyrics", "item/" + current().model);
    }

    public static void startInspect() {
        inspectTicks = 36; // ~1.8s at 20 tps
    }

    @Override
    public void onTick() {
        if (inspectTicks > 0) inspectTicks--;
    }

    @Override
    public void onHud(net.minecraft.client.gui.DrawContext g, float td) {
        // small label bottom-left so user sees active knife
        var tr = net.minecraft.client.MinecraftClient.getInstance().textRenderer;
        String label = "✦ " + current().label;
        int w = tr.getWidth(label);
        int x = 8, y = g.getScaledWindowHeight() - 20;
        g.fill(x - 4, y - 3, x + w + 6, y + 11, 0x990B0D12);
        g.drawText(tr, label, x, y, 0xFFC4B5FD, false);
    }
}
