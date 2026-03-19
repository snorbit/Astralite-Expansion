package com.snorbitzz.astralskies.registry;

import com.snorbitzz.astralskies.Constants;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.function.Supplier;

/**
 * Central registry for all Astral Skies items.
 * Uses a simple supplier-based lazy registration pattern compatible with both loaders.
 */
public class ModItems {

    // ─── Raw Materials ───────────────────────────────────────────────────────

    /** Primary drop from Astralite Ore. The backbone crafting material of the mod. */
    public static final RegistryObject<Item> ASTRALITE_SCRAP = register("astralite_scrap",
            () -> new Item(new Item.Properties()));

    /** Rare secondary drop — used for high-tier upgrades and the Upgrade Template. */
    public static final RegistryObject<Item> ASTRALITE_SHARD = register("astralite_shard",
            () -> new Item(new Item.Properties()));

    /** Raw ore form dropped when mining without Silk Touch. */
    public static final RegistryObject<Item> RAW_ASTRALITE = register("raw_astralite",
            () -> new Item(new Item.Properties()));

    // ─── Crafting Components ─────────────────────────────────────────────────

    /**
     * Smith upgrade template — consumed when upgrading Astralite gear at the smithing table.
     * Found in Astral Temples and sky island chests.
     */
    public static final RegistryObject<Item> ASTRALITE_UPGRADE_TEMPLATE = register("astralite_upgrade_template",
            () -> new Item(new Item.Properties()));

    // ─── Tools & Utility ─────────────────────────────────────────────────────

    /**
     * Astral Compass — points toward the nearest Astral Temple.
     * Functional behaviour implemented in a later phase.
     */
    public static final RegistryObject<Item> ASTRAL_COMPASS = register("astral_compass",
            () -> new Item(new Item.Properties().stacksTo(1)));

    // ─── Registration Helpers ─────────────────────────────────────────────────

    public static void init() {
        Constants.LOG.info("Registering Astral Skies Items");
    }

    private static <T extends Item> RegistryObject<T> register(String name, Supplier<T> supplier) {
        T item = supplier.get();
        Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, name), item);
        return new RegistryObject<>(item);
    }

    /** Minimal holder so callers can use .get() consistently. */
    public static class RegistryObject<T> {
        private final T value;
        public RegistryObject(T value) { this.value = value; }
        public T get() { return value; }
    }
}
