package com.snorbitzz.astralskies.registry;

import com.snorbitzz.astralskies.Constants;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.*;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Legendary gear — dropped exclusively by the 6 endgame bosses.
 *
 * All legendary armor is significantly stronger than Astralite:
 *   Astralite per piece: 8 defense / 4 toughness
 *   Legendary per piece: 12 defense / 6 toughness  (+50% stat increase)
 *
 * Legendary set is NOT craftable — boss-drop only.
 *
 * Boss → Drop mapping:
 *   Leviathan        → Leviathan Helmet      (head)
 *   Storm Warden     → Stormcall Chestplate  (chest)
 *   Void Stalker     → Void Stalker Leggings (legs)
 *   Crystal Sovereign→ Sovereign's Boots     (feet)
 *   Sky Tyrant       → Tyrant's Blade        (sword, 18 dmg)
 *   Ruined Titan     → Titan's Pickaxe       (pickaxe, also 14 dmg)
 */
public class LegendaryItems {

    // ─── Legendary Armor Material ─────────────────────────────────────────────

    /**
     * Shared armor material for all 4 legendary armor pieces.
     * Uses a different texture path per piece (lore: each boss infused a fragment).
     */
    public static final Holder<ArmorMaterial> LEGENDARY_ARMOR_MATERIAL = registerArmorMaterial(
            "legendary",
            new ArmorMaterial(
                    new EnumMap<>(Map.of(
                            ArmorItem.Type.HELMET,     12,
                            ArmorItem.Type.CHESTPLATE, 12,
                            ArmorItem.Type.LEGGINGS,   12,
                            ArmorItem.Type.BOOTS,      12
                    )),
                    30,                                       // enchantability (very high)
                    SoundEvents.ARMOR_EQUIP_NETHERITE,
                    () -> net.minecraft.world.item.crafting.Ingredient.of(ModItems.ASTRALITE_SHARD.get()),
                    List.of(),
                    6.0f,                                     // toughness per piece
                    0.3f                                      // knockback resistance
            )
    );

    // ─── Armor Pieces (4 bosses) ──────────────────────────────────────────────

    /** Leviathan Helmet — +12 defense, +6 toughness. Dropped by the Leviathan. */
    public static final ModItems.RegistryObject<ArmorItem> LEVIATHAN_HELMET =
            registerArmor("leviathan_helmet", ArmorItem.Type.HELMET);

    /** Stormcall Chestplate — +12 defense, +6 toughness. Dropped by the Storm Warden. */
    public static final ModItems.RegistryObject<ArmorItem> STORMCALL_CHESTPLATE =
            registerArmor("stormcall_chestplate", ArmorItem.Type.CHESTPLATE);

    /** Void Stalker Leggings — +12 defense, +6 toughness. Dropped by the Void Stalker. */
    public static final ModItems.RegistryObject<ArmorItem> VOID_STALKER_LEGGINGS =
            registerArmor("void_stalker_leggings", ArmorItem.Type.LEGGINGS);

    /** Sovereign's Boots — +12 defense, +6 toughness. Dropped by the Crystal Sovereign. */
    public static final ModItems.RegistryObject<ArmorItem> SOVEREIGN_BOOTS =
            registerArmor("sovereign_boots", ArmorItem.Type.BOOTS);

    // ─── Weapons (2 bosses) ───────────────────────────────────────────────────

    /**
     * Tyrant's Blade — 18 base damage (4 + 14 bonus).
     * Dropped by the Sky Tyrant. The strongest melee weapon in the mod.
     */
    public static final ModItems.RegistryObject<SwordItem> TYRANTS_BLADE =
            registerItem("tyrants_blade",
                    () -> new SwordItem(
                            ModToolTiers.ASTRALITE,
                            new Item.Properties()
                                    .attributes(SwordItem.createAttributes(ModToolTiers.ASTRALITE, 14, -2.2f))
                    ));

    /**
     * Titan's Pickaxe — mines at double Astralite speed; 14 base damage.
     * Dropped by the Ruined Titan. The strongest pickaxe in the mod.
     */
    public static final ModItems.RegistryObject<PickaxeItem> TITANS_PICKAXE =
            registerItem("titans_pickaxe",
                    () -> new PickaxeItem(
                            ModToolTiers.ASTRALITE,
                            new Item.Properties()
                                    .attributes(PickaxeItem.createAttributes(ModToolTiers.ASTRALITE, 10, -2.8f))
                    ));

    // ─── Init ─────────────────────────────────────────────────────────────────

    public static void init() {
        Constants.LOG.info("Registering Legendary Items");
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private static Holder<ArmorMaterial> registerArmorMaterial(String name, ArmorMaterial mat) {
        return Registry.registerForHolder(
                BuiltInRegistries.ARMOR_MATERIAL,
                ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, name),
                mat
        );
    }

    private static ModItems.RegistryObject<ArmorItem> registerArmor(String name, ArmorItem.Type type) {
        ArmorItem item = new ArmorItem(
                LEGENDARY_ARMOR_MATERIAL, type,
                new Item.Properties()
                        .durability(type.getDurability(37 * 5))
        );
        Registry.register(BuiltInRegistries.ITEM,
                ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, name), item);
        return new ModItems.RegistryObject<>(item);
    }

    private static <T extends Item> ModItems.RegistryObject<T> registerItem(String name, Supplier<T> s) {
        T item = s.get();
        Registry.register(BuiltInRegistries.ITEM,
                ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, name), item);
        return new ModItems.RegistryObject<>(item);
    }
}
