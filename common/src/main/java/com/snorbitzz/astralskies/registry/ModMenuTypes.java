package com.snorbitzz.astralskies.registry;

import com.snorbitzz.astralskies.Constants;
import com.snorbitzz.astralskies.menu.AstralPowersMenu;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;

/**
 * Registers all MenuType entries for the mod.
 */
public class ModMenuTypes {

    public static final MenuType<AstralPowersMenu> ASTRAL_POWERS = Registry.register(
            BuiltInRegistries.MENU,
            ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "astral_powers"),
            new MenuType<>(AstralPowersMenu::new, FeatureFlags.DEFAULT_FLAGS)
    );

    public static void init() {
        Constants.LOG.info("Registering Astral Skies Menu Types");
    }
}
