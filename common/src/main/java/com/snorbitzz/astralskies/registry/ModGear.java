package com.snorbitzz.astralskies.registry;

import com.snorbitzz.astralskies.Constants;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.*;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Astralite gear: armor set and sword.
 *
 * Armor stats (per piece, vs Netherite):
 *   Helmet:     +8 defense, +4 toughness  (Netherite = 3, 3)
 *   Chestplate: +8 defense, +4 toughness  (Netherite = 8, 3)
 *   Leggings:   +8 defense, +4 toughness  (Netherite = 6, 3)
 *   Boots:      +8 defense, +4 toughness  (Netherite = 3, 3)
 *
 * Full Set Bonus (implemented via ArmorItem attribute modifiers in a later phase).
 * Sword: 11 base damage, scales on consecutive hits.
 */
public class ModGear {

    // ─── Armor Material ───────────────────────────────────────────────────────

    /**
     * Astralite ArmorMaterial — registered into the built-in registry so it can
     * be referenced by the ArmorItem constructors.
     */
    public static final Holder<ArmorMaterial> ASTRALITE_ARMOR_MATERIAL = registerArmorMaterial(
            "astralite",
            new ArmorMaterial(
                    // Defense values per armor type
                    new EnumMap<>(Map.of(
                            ArmorItem.Type.HELMET,     8,
                            ArmorItem.Type.CHESTPLATE, 8,
                            ArmorItem.Type.LEGGINGS,   8,
                            ArmorItem.Type.BOOTS,      8
                    )),
                    22,                                   // enchantability (high, like gold-tier)
                    SoundEvents.ARMOR_EQUIP_NETHERITE,    // equip sound
                    () -> net.minecraft.world.item.crafting.Ingredient.of(ModItems.ASTRALITE_SCRAP.get()),
                    List.of(),                            // overlays (none -- solid material)
                    4.0f,                                 // toughness per piece
                    0.2f                                  // knockback resistance per piece
            )
    );

    // ─── Armor Items ──────────────────────────────────────────────────────────

    public static final ModItems.RegistryObject<ArmorItem> ASTRALITE_HELMET = registerArmor(
            "astralite_helmet", ArmorItem.Type.HELMET);

    public static final ModItems.RegistryObject<ArmorItem> ASTRALITE_CHESTPLATE = registerArmor(
            "astralite_chestplate", ArmorItem.Type.CHESTPLATE);

    public static final ModItems.RegistryObject<ArmorItem> ASTRALITE_LEGGINGS = registerArmor(
            "astralite_leggings", ArmorItem.Type.LEGGINGS);

    public static final ModItems.RegistryObject<ArmorItem> ASTRALITE_BOOTS = registerArmor(
            "astralite_boots", ArmorItem.Type.BOOTS);

    // ─── Weapons ──────────────────────────────────────────────────────────────

    /**
     * Astralite Sword — 11 base damage (4 + 7 bonus), fast attack speed.
     * Damage-scaling on consecutive hits will be added via an item attribute modifier in Phase 3.
     */
    public static final ModItems.RegistryObject<SwordItem> ASTRALITE_SWORD = registerItem(
            "astralite_sword",
            () -> new SwordItem(
                    ModToolTiers.ASTRALITE,
                    new Item.Properties()
                            .attributes(SwordItem.createAttributes(ModToolTiers.ASTRALITE, 7, -2.4f))
            ));

    /**
     * Astralite Pickaxe — fastest pickaxe in the mod, essential for sky mining.
     */
    public static final ModItems.RegistryObject<PickaxeItem> ASTRALITE_PICKAXE = registerItem(
            "astralite_pickaxe",
            () -> new PickaxeItem(
                    ModToolTiers.ASTRALITE,
                    new Item.Properties()
                            .attributes(PickaxeItem.createAttributes(ModToolTiers.ASTRALITE, 3, -2.8f))
            ));

    /**
     * Astralite Axe — powerful woodchopping + combat hybrid.
     */
    public static final ModItems.RegistryObject<AxeItem> ASTRALITE_AXE = registerItem(
            "astralite_axe",
            () -> new AxeItem(
                    ModToolTiers.ASTRALITE,
                    new Item.Properties()
                            .attributes(AxeItem.createAttributes(ModToolTiers.ASTRALITE, 8, -3.0f))
            ));

    /**
     * Astralite Shovel — fast digging for sky island dirt/gravel.
     */
    public static final ModItems.RegistryObject<ShovelItem> ASTRALITE_SHOVEL = registerItem(
            "astralite_shovel",
            () -> new ShovelItem(
                    ModToolTiers.ASTRALITE,
                    new Item.Properties()
                            .attributes(ShovelItem.createAttributes(ModToolTiers.ASTRALITE, 3.5f, -3.0f))
            ));

    /**
     * Astralite Hoe — ultra-efficient farming tool.
     */
    public static final ModItems.RegistryObject<HoeItem> ASTRALITE_HOE = registerItem(
            "astralite_hoe",
            () -> new HoeItem(
                    ModToolTiers.ASTRALITE,
                    new Item.Properties()
                            .attributes(HoeItem.createAttributes(ModToolTiers.ASTRALITE, -1, 0.0f))
            ));

    // ─── Registration Helpers ─────────────────────────────────────────────────

    public static void init() {
        Constants.LOG.info("Registering Astral Skies Gear");
    }

    private static Holder<ArmorMaterial> registerArmorMaterial(String name, ArmorMaterial material) {
        return Registry.registerForHolder(
                BuiltInRegistries.ARMOR_MATERIAL,
                ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, name),
                material
        );
    }

    private static ModItems.RegistryObject<ArmorItem> registerArmor(String name, ArmorItem.Type type) {
        ArmorItem item = new ArmorItem(
                ASTRALITE_ARMOR_MATERIAL,
                type,
                new Item.Properties()
                        .durability(type.getDurability(37 * 3))
        );
        Registry.register(BuiltInRegistries.ITEM,
                ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, name), item);
        return new ModItems.RegistryObject<>(item);
    }

    private static <T extends Item> ModItems.RegistryObject<T> registerItem(String name, Supplier<T> supplier) {
        T item = supplier.get();
        Registry.register(BuiltInRegistries.ITEM,
                ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, name), item);
        return new ModItems.RegistryObject<>(item);
    }
}
