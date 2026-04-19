package com.snorbitzz.astralskies.screen;

import com.snorbitzz.astralskies.menu.AstralPowersMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

/**
 * Astral Powers Screen — a stunning dark-themed ability selection UI.
 *
 * Layout: 3×2 grid of ability cards on a dark starry background.
 * Each card shows an icon, name, description, and cooldown indicator.
 *
 * Clicking a card sends a vanilla container button click packet,
 * handled by {@link AstralPowersMenu#clickMenuButton}.
 */
public class AstralPowersScreen extends AbstractContainerScreen<AstralPowersMenu> {

    // ─── Ability definitions ──────────────────────────────────────────────────

    private static final String[] ABILITY_ICONS = {"*", "~", "!", "o", "+", "#"};
    private static final String[] ABILITY_NAMES = {
            "Star Shield", "Lunar Grace", "Astral Rush",
            "Celestial Vision", "Stellar Regen", "Nova Burst"
    };
    private static final String[] ABILITY_DESCS = {
            "Resistance II + Absorption",
            "Slow Fall + Jump Boost III",
            "Speed III + Haste II",
            "Night Vision (3 min)",
            "Regeneration III + Saturation",
            "Fire Resist + Strength II"
    };
    private static final int[] ABILITY_COLORS = {
            0xFF44BBFF, // Star Shield — cyan
            0xFFCC88FF, // Lunar Grace — lavender
            0xFFFFDD44, // Astral Rush — gold
            0xFF44FF88, // Celestial Vision — green
            0xFFFF5555, // Stellar Regen — red
            0xFFFF8844, // Nova Burst — orange
    };
    private static final int[] COOLDOWN_SECS = {120, 180, 120, 60, 180, 120};

    // ─── Layout constants ────────────────────────────────────────────────────

    private static final int PANEL_W = 280;
    private static final int PANEL_H = 200;
    private static final int CARD_W = 82;
    private static final int CARD_H = 76;
    private static final int CARD_GAP = 6;
    private static final int COLS = 3;
    private static final int HEADER_H = 30;

    public AstralPowersScreen(AstralPowersMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = PANEL_W;
        this.imageHeight = PANEL_H;
        this.inventoryLabelY = Integer.MAX_VALUE; // hide "Inventory" label
        this.titleLabelY = Integer.MAX_VALUE;     // hide default title
    }

    // ─── Rendering ───────────────────────────────────────────────────────────

    @Override
    protected void renderBg(GuiGraphics g, float partialTick, int mouseX, int mouseY) {
        // Full-screen dark overlay
        g.fill(0, 0, this.width, this.height, 0xCC000010);

        int cx = (this.width - PANEL_W) / 2;
        int cy = (this.height - PANEL_H) / 2;

        // Panel background with border
        g.fill(cx - 2, cy - 2, cx + PANEL_W + 2, cy + PANEL_H + 2, 0xFF1A1A3A); // outer border
        g.fill(cx, cy, cx + PANEL_W, cy + PANEL_H, 0xE8080818);                   // inner bg

        // Gradient accent line at top
        g.fill(cx, cy, cx + PANEL_W, cy + 2, 0xFF44BBFF);

        // Title
        drawCenteredString(g, "✦ Astral Powers ✦", cx + PANEL_W / 2, cy + 8, 0xFF88DDFF);

        // Subtitle
        drawCenteredString(g, "§7Select an ability to activate", cx + PANEL_W / 2, cy + 20, 0xFF888888);

        // Draw ability cards
        int startX = cx + (PANEL_W - (COLS * CARD_W + (COLS - 1) * CARD_GAP)) / 2;
        int startY = cy + HEADER_H + 4;

        for (int i = 0; i < AstralPowersMenu.ABILITY_COUNT; i++) {
            int col = i % COLS;
            int row = i / COLS;
            int cardX = startX + col * (CARD_W + CARD_GAP);
            int cardY = startY + row * (CARD_H + CARD_GAP);

            boolean hovered = mouseX >= cardX && mouseX < cardX + CARD_W
                           && mouseY >= cardY && mouseY < cardY + CARD_H;

            drawAbilityCard(g, i, cardX, cardY, hovered);
        }
    }

    private void drawAbilityCard(GuiGraphics g, int index, int x, int y, boolean hovered) {
        int color = ABILITY_COLORS[index];
        int bgColor = hovered ? 0xE0182838 : 0xD0101828;
        int borderColor = hovered ? color : (color & 0x80FFFFFF);

        // Card border
        g.fill(x - 1, y - 1, x + CARD_W + 1, y + CARD_H + 1, borderColor);
        // Card background
        g.fill(x, y, x + CARD_W, y + CARD_H, bgColor);

        // Accent line at top of card
        g.fill(x, y, x + CARD_W, y + 2, color);

        // Icon (rendered as text)
        drawCenteredString(g, ABILITY_ICONS[index], x + CARD_W / 2, y + 8, color);

        // Name
        drawCenteredString(g, ABILITY_NAMES[index], x + CARD_W / 2, y + 24, 0xFFFFFFFF);

        // Description (small, grey)
        drawCenteredStringSmall(g, ABILITY_DESCS[index], x + CARD_W / 2, y + 38, 0xFFAAAAAA);

        // Cooldown info
        int cd = AstralPowersMenu.getRemainingCooldown(
                this.minecraft.player, index);
        if (cd > 0) {
            // Darken overlay
            g.fill(x, y, x + CARD_W, y + CARD_H, 0xA0000000);
            // Cooldown text
            String cdText = cd + "s";
            drawCenteredString(g, "§c" + cdText, x + CARD_W / 2, y + CARD_H / 2 - 4, 0xFFFF4444);

            // Cooldown bar
            float progress = (float) cd / COOLDOWN_SECS[index];
            int barW = (int) (CARD_W * progress);
            g.fill(x, y + CARD_H - 3, x + barW, y + CARD_H, color & 0x88FFFFFF);
        } else if (hovered) {
            // "Click to activate" hint
            drawCenteredStringSmall(g, "§aClick to activate", x + CARD_W / 2, y + CARD_H - 14, 0xFF44FF44);
        }
    }

    private void drawCenteredString(GuiGraphics g, String text, int x, int y, int color) {
        Component comp = Component.literal(text);
        g.drawCenteredString(this.font, comp, x, y, color);
    }

    private void drawCenteredStringSmall(GuiGraphics g, String text, int x, int y, int color) {
        // Use scaled rendering for smaller text
        Component comp = Component.literal(text);
        int w = this.font.width(comp);
        g.drawString(this.font, comp, x - w / 2, y, color);
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        super.render(g, mouseX, mouseY, partialTick);
        // No tooltip rendering needed (custom tooltips are in the cards)
    }

    // ─── Click handling ──────────────────────────────────────────────────────

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) { // Left click
            int cx = (this.width - PANEL_W) / 2;
            int cy = (this.height - PANEL_H) / 2;
            int startX = cx + (PANEL_W - (COLS * CARD_W + (COLS - 1) * CARD_GAP)) / 2;
            int startY = cy + HEADER_H + 4;

            for (int i = 0; i < AstralPowersMenu.ABILITY_COUNT; i++) {
                int col = i % COLS;
                int row = i / COLS;
                int cardX = startX + col * (CARD_W + CARD_GAP);
                int cardY = startY + row * (CARD_H + CARD_GAP);

                if (mouseX >= cardX && mouseX < cardX + CARD_W
                 && mouseY >= cardY && mouseY < cardY + CARD_H) {
                    // Send the button click to the server via vanilla container button packet
                    this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, i);

                    // Play a click sound
                    this.minecraft.player.playSound(
                            net.minecraft.sounds.SoundEvents.UI_BUTTON_CLICK.value(),
                            0.4f, 1.0f
                    );
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
