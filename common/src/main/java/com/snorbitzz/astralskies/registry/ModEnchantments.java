package com.snorbitzz.astralskies.registry;

import com.snorbitzz.astralskies.Constants;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;

/**
 * ResourceKeys for all Astral Skies enchantments.
 *
 * All enchantments are data-driven (JSON in data/astral_skies/enchantment/).
 * These keys are used to look up enchantments from the registry at runtime,
 * for example when applying or checking enchantments on items in code.
 *
 * IMPORTANT — Life Steal is the secret enchantment reserved for the
 * Snorbitzz_Jnr account. It is not obtainable through normal gameplay.
 */
public class ModEnchantments {

    // ─── Sword enchantments ───────────────────────────────────────────────────

    /** Sky Slayer — high damage bonus vs all mobs, max L5. */
    public static final ResourceKey<Enchantment> SKY_SLAYER = key("sky_slayer");

    /** Astral Edge — damage bonus + Slowness on hit, max L3. */
    public static final ResourceKey<Enchantment> ASTRAL_EDGE = key("astral_edge");

    /** Starfall — big damage + ignite on hit, very rare, max L3. */
    public static final ResourceKey<Enchantment> STARFALL = key("starfall");

    /**
     * Life Steal (SECRET) — heals the attacker on every hit.
     * Not findable in enchanting table or loot — given only to Snorbitzz_Jnr
     * via the hidden secret system (Phase 4).
     */
    public static final ResourceKey<Enchantment> LIFE_STEAL = key("life_steal");

    // ─── Armor enchantments ───────────────────────────────────────────────────

    /** Astral Protection — stronger version of Protection, max L4. */
    public static final ResourceKey<Enchantment> ASTRAL_PROTECTION = key("astral_protection");

    /** Void Shield — +armor and +toughness bonus stacking, max L4. */
    public static final ResourceKey<Enchantment> VOID_SHIELD = key("void_shield");

    // ─── Boot enchantments ────────────────────────────────────────────────────

    /** Sky Walker — movement speed bonus per level, max L3. */
    public static final ResourceKey<Enchantment> SKY_WALKER = key("sky_walker");

    /** Cloud Step — +50% jump height and +8 safe fall distance. L1 only, very rare. */
    public static final ResourceKey<Enchantment> CLOUD_STEP = key("cloud_step");

    // ─── Tool enchantments ────────────────────────────────────────────────────

    /** Sky Miner — bonus mining speed on sky island stone, max L4. */
    public static final ResourceKey<Enchantment> SKY_MINER = key("sky_miner");

    // ─── Ranged enchantments ─────────────────────────────────────────────────

    /** Gale Force — faster, harder arrows from bows, max L4. */
    public static final ResourceKey<Enchantment> GALE_FORCE = key("gale_force");

    // ─── Init ─────────────────────────────────────────────────────────────────

    public static void init() {
        Constants.LOG.info("Astral Skies enchantment keys loaded ({} enchantments)", 10);
    }

    private static ResourceKey<Enchantment> key(String path) {
        return ResourceKey.create(Registries.ENCHANTMENT,
                ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, path));
    }
}
