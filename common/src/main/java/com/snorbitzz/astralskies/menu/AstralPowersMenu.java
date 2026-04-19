package com.snorbitzz.astralskies.menu;

import com.snorbitzz.astralskies.registry.ModMenuTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Astral Powers Menu — the server-side logic for the Astral Tome UI.
 *
 * Uses vanilla's {@link #clickMenuButton} system (same as enchanting table / stonecutter)
 * so it works across all mod loaders without custom packets.
 *
 * Six abilities, each with individual cooldowns:
 *   0 = Star Shield        — Resistance II + Absorption I (60s)      cd: 2 min
 *   1 = Lunar Grace         — Slow Falling + Jump Boost III (90s)    cd: 3 min
 *   2 = Astral Rush         — Speed III + Haste II (45s)             cd: 2 min
 *   3 = Celestial Vision    — Night Vision (180s)                    cd: 1 min
 *   4 = Stellar Regeneration— Regeneration III + Saturation (30s)    cd: 3 min
 *   5 = Nova Burst          — Fire Resistance + Strength II (60s)    cd: 2 min
 */
public class AstralPowersMenu extends AbstractContainerMenu {

    /** Per-player cooldown timestamps (game ticks). */
    private static final Map<UUID, long[]> COOLDOWNS = new HashMap<>();

    /** Cooldown duration for each ability (in ticks: 20 = 1 sec). */
    private static final int[] COOLDOWN_TICKS = {
            2400, // Star Shield       — 2 min
            3600, // Lunar Grace        — 3 min
            2400, // Astral Rush        — 2 min
            1200, // Celestial Vision   — 1 min
            3600, // Stellar Regen      — 3 min
            2400, // Nova Burst         — 2 min
    };

    public static final int ABILITY_COUNT = 6;

    public AstralPowersMenu(int containerId, Inventory playerInventory) {
        super(ModMenuTypes.ASTRAL_POWERS, containerId);
    }

    // ─── Ability activation ──────────────────────────────────────────────────

    @Override
    public boolean clickMenuButton(Player player, int buttonId) {
        if (buttonId < 0 || buttonId >= ABILITY_COUNT) return false;

        long[] cds = COOLDOWNS.computeIfAbsent(player.getUUID(), k -> new long[ABILITY_COUNT]);
        long now = player.level().getGameTime();

        if (now - cds[buttonId] < COOLDOWN_TICKS[buttonId]) {
            int remaining = (int) ((COOLDOWN_TICKS[buttonId] - (now - cds[buttonId])) / 20);
            player.sendSystemMessage(Component.literal("§c✦ On cooldown! §7" + remaining + "s remaining."));
            return false;
        }

        cds[buttonId] = now;
        applyAbility(player, buttonId);
        return true;
    }

    private void applyAbility(Player player, int id) {
        switch (id) {
            case 0 -> { // Star Shield
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 1200, 1, false, true));
                player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 1200, 0, false, true));
                player.sendSystemMessage(Component.literal("\u00a7b[Star Shield] \u00a77You are cloaked in astral energy."));
            }
            case 1 -> { // Lunar Grace
                player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 1800, 0, false, true));
                player.addEffect(new MobEffectInstance(MobEffects.JUMP, 1800, 2, false, true));
                player.sendSystemMessage(Component.literal("\u00a7d[Lunar Grace] \u00a77Gravity loosens its grip."));
            }
            case 2 -> { // Astral Rush
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 900, 2, false, true));
                player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 900, 1, false, true));
                player.sendSystemMessage(Component.literal("\u00a7e[Astral Rush] \u00a77Light speed isn't fast enough."));
            }
            case 3 -> { // Celestial Vision
                player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 3600, 0, false, true));
                player.sendSystemMessage(Component.literal("\u00a7a[Celestial Vision] \u00a77The darkness reveals its secrets."));
            }
            case 4 -> { // Stellar Regeneration
                player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 600, 2, false, true));
                player.addEffect(new MobEffectInstance(MobEffects.SATURATION, 600, 0, false, true));
                player.sendSystemMessage(Component.literal("\u00a7c[Stellar Regen] \u00a77Starlight heals your wounds."));
            }
            case 5 -> { // Nova Burst
                player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 1200, 0, false, true));
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 1200, 1, false, true));
                player.sendSystemMessage(Component.literal("\u00a76[Nova Burst] \u00a77Burn brighter than the sun."));
            }
        }
    }

    /** Returns remaining cooldown in seconds, or 0 if ready. */
    public static int getRemainingCooldown(Player player, int abilityId) {
        long[] cds = COOLDOWNS.get(player.getUUID());
        if (cds == null) return 0;
        long now = player.level().getGameTime();
        int remaining = (int) ((COOLDOWN_TICKS[abilityId] - (now - cds[abilityId])) / 20);
        return Math.max(0, remaining);
    }

    // ─── Required overrides ──────────────────────────────────────────────────

    @Override
    public ItemStack quickMoveStack(Player player, int slot) {
        return ItemStack.EMPTY; // No slots
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}
