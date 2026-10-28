package dev.axiom.gui;

import dev.axiom.client.Ui;
import dev.axiom.module.Module;
import dev.axiom.module.ModuleManager;
import dev.axiom.modules.KnifeCosmetics;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ClickGui extends Screen {
    private static final int PANEL = 0xF012151C;
    private static final int CARD = 0xFF1A1F2A;
    private static final int CARD_ON = 0xFF252B3A;
    private static final int TEXT = 0xFFE8EAF0;
    private static final int MUTED = 0xFF8B93A5;
    private static final int ACCENT = 0xFFA06CFF;

    private final Map<String, List<Module>> cats = new LinkedHashMap<>();

    public ClickGui() {
        super(Text.literal("Axiom"));
        for (Module m : ModuleManager.all()) {
            cats.computeIfAbsent(m.category(), k -> new ArrayList<>()).add(m);
        }
    }

    @Override
    public void render(DrawContext g, int mouseX, int mouseY, float delta) {
        g.fill(0, 0, width, height, 0x99000000);

        int colW = 168;
        int gap = 14;
        int cols = Math.max(1, cats.size());
        int totalW = cols * colW + (cols - 1) * gap;
        int startX = (width - totalW) / 2;
        int top = 52;

        String title = ModuleManager.german() ? "AXIOM  ·  cosmetics" : "AXIOM  ·  cosmetics";
        Ui.textBold(g, textRenderer, title, startX, 16, TEXT);
        String sub = ModuleManager.german()
            ? "Right Shift · Inspect: I · Sprache umschalten unten"
            : "Right Shift · Inspect: I · language toggle below";
        Ui.text(g, textRenderer, sub, startX, 30, MUTED);

        int ci = 0;
        for (var e : cats.entrySet()) {
            int x = startX + ci * (colW + gap);
            int y = top;
            int h = 32 + e.getValue().size() * 38 + 16;
            Ui.rrect(g, x, y, colW, h, 12, PANEL);
            Ui.rrect(g, x + 10, y + 12, 5, 5, 2, ACCENT);
            Ui.textBold(g, textRenderer, e.getKey(), x + 22, y + 12, TEXT);

            int my = y + 34;
            for (Module m : e.getValue()) {
                boolean on = m.enabled();
                boolean hover = mouseX >= x + 8 && mouseX < x + colW - 8 && mouseY >= my && mouseY < my + 34;
                int bg = on ? CARD_ON : CARD;
                if (hover) bg = on ? 0xFF2C3344 : 0xFF222836;
                Ui.rrect(g, x + 8, my, colW - 16, 34, 9, bg);
                if (on) Ui.rrect(g, x + 8, my, 3, 34, 2, ACCENT);
                Ui.text(g, textRenderer, m.name(), x + 18, my + 7, on ? TEXT : MUTED);
                Ui.text(g, textRenderer, m.desc(), x + 18, my + 19, 0xFF5B6377);
                int px = x + colW - 42, py = my + 11;
                Ui.rrect(g, px, py, 24, 13, 6, on ? ACCENT : 0xFF3A4150);
                Ui.rrect(g, on ? px + 13 : px + 3, py + 2, 9, 9, 4, 0xFFFFFFFF);
                my += 38;
            }
            ci++;
        }

        // knife strip
        int kx = startX;
        int ky = height - 78;
        int kw = Math.min(totalW, 520);
        Ui.rrect(g, kx, ky, kw, 52, 12, PANEL);
        String kTitle = ModuleManager.german() ? "Messer" : "Knife";
        Ui.textBold(g, textRenderer, kTitle, kx + 14, ky + 10, TEXT);
        String kName = KnifeCosmetics.current().label;
        Ui.text(g, textRenderer, kName, kx + 14, ky + 28, ACCENT);

        // prev / next
        Ui.rrect(g, kx + kw - 100, ky + 12, 36, 28, 8, CARD);
        Ui.text(g, textRenderer, "<", kx + kw - 86, ky + 20, TEXT);
        Ui.rrect(g, kx + kw - 56, ky + 12, 36, 28, 8, CARD);
        Ui.text(g, textRenderer, ">", kx + kw - 42, ky + 20, TEXT);

        // lang toggle
        String lang = ModuleManager.german() ? "DE | EN" : "EN | DE";
        int lw = Ui.width(textRenderer, lang) + 20;
        Ui.rrect(g, width - lw - 16, 14, lw, 22, 8, CARD);
        Ui.text(g, textRenderer, lang, width - lw - 6, 20, MUTED);

        super.render(g, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        double mx = click.x(), my = click.y();
        if (click.button() != 0) return super.mouseClicked(click, doubled);

        // language
        String lang = ModuleManager.german() ? "DE | EN" : "EN | DE";
        int lw = Ui.width(textRenderer, lang) + 20;
        if (mx >= width - lw - 16 && mx <= width - 16 && my >= 14 && my <= 36) {
            ModuleManager.setGerman(!ModuleManager.german());
            client.setScreen(new ClickGui());
            return true;
        }

        int colW = 168, gap = 14;
        int cols = Math.max(1, cats.size());
        int totalW = cols * colW + (cols - 1) * gap;
        int startX = (width - totalW) / 2;
        int top = 52;

        int ci = 0;
        for (var e : cats.entrySet()) {
            int x = startX + ci * (colW + gap);
            int y = top;
            int row = y + 34;
            for (Module m : e.getValue()) {
                if (mx >= x + 8 && mx < x + colW - 8 && my >= row && my < row + 34) {
                    m.toggle();
                    return true;
                }
                row += 38;
            }
            ci++;
        }

        // knife buttons
        int kx = startX;
        int ky = height - 78;
        int kw = Math.min(totalW, 520);
        if (my >= ky + 12 && my <= ky + 40) {
            if (mx >= kx + kw - 100 && mx <= kx + kw - 64) { KnifeCosmetics.prev(); return true; }
            if (mx >= kx + kw - 56 && mx <= kx + kw - 20) { KnifeCosmetics.next(); return true; }
        }
        return super.mouseClicked(click, doubled);
    }

    @Override public boolean shouldPause() { return false; }
}
