package com.snorbitzz.astralskies.registry;

import com.snorbitzz.astralskies.Constants;
import com.snorbitzz.astralskies.item.KurumiEggItem;
import com.snorbitzz.astralskies.item.ZafkielItem;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.function.Supplier;

/**
 * Central registry for all Astral Skies items.
 */
public class ModItems {

    // ─── Raw Materials ───────────────────────────────────────────────────────

    public static final RegistryObject<Item> ASTRALITE_SCRAP = register("astralite_scrap",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> ASTRALITE_SHARD = register("astralite_shard",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> RAW_ASTRALITE = register("raw_astralite",
            () -> new Item(new Item.Properties()));

    // ─── Crafting Components ─────────────────────────────────────────────────

    public static final RegistryObject<Item> ASTRALITE_UPGRADE_TEMPLATE = register("astralite_upgrade_template",
            () -> new Item(new Item.Properties()));

    // ─── Tools & Utility ─────────────────────────────────────────────────────

    public static final RegistryObject<Item> ASTRAL_COMPASS = register("astral_compass",
            () -> new Item(new Item.Properties().stacksTo(1)));

    // ─── Boss Drops & Special Items ───────────────────────────────────────────

    /**
     * Zafkiel — Kurumi Tokisaki's clock weapon (secret boss drop).
     * Right-click: fires Aleph bullet — Slowness IV (20b radius) + 6 HP heal.
     */
    public static final RegistryObject<ZafkielItem> ZAFKIEL = register("zafkiel",
            () -> new ZafkielItem(new Item.Properties().stacksTo(1)));

    /**
     * Kurumi Egg — hatch your own Kurumi companion (secret boss drop, single-use).
     */
    public static final RegistryObject<KurumiEggItem> KURUMI_EGG = register("kurumi_egg",
            () -> new KurumiEggItem(new Item.Properties()));

    // ─── Registration ─────────────────────────────────────────────────────────

    public static void init() {
        Constants.LOG.info("Registering Astral Skies Items");
    }

    @SuppressWarnings("unchecked")
    private static <T extends Item> RegistryObject<T> register(String name, Supplier<T> supplier) {
        T item = supplier.get();
        Registry.register(BuiltInRegistries.ITEM,
                ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, name), item);
        return new RegistryObject<>(item);
    }

    /** Minimal typed holder. */
    public static class RegistryObject<T> {
        private final T value;
        public RegistryObject(T value) { this.value = value; }
        public T get() { return value; }
    }
}
