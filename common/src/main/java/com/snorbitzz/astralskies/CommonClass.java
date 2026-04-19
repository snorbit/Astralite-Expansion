package com.snorbitzz.astralskies;

import com.snorbitzz.astralskies.aircraft.AircraftBlocks;
import com.snorbitzz.astralskies.aircraft.AircraftEntities;
import com.snorbitzz.astralskies.entity.ModEntities;
import com.snorbitzz.astralskies.platform.Services;
import com.snorbitzz.astralskies.registry.LegendaryItems;
import com.snorbitzz.astralskies.registry.ModBlocks;
import com.snorbitzz.astralskies.registry.ModCreativeTabs;
import com.snorbitzz.astralskies.registry.ModEnchantments;
import com.snorbitzz.astralskies.registry.ModGear;
import com.snorbitzz.astralskies.registry.ModItems;
import com.snorbitzz.astralskies.registry.ModMenuTypes;
import com.snorbitzz.astralskies.worldgen.ModWorldGen;
import com.snorbitzz.astralskies.worldgen.structure.ModStructures;

/**
 * Shared entry point for both Fabric and NeoForge.
 * Order: Blocks → Items → Gear → Aircraft → Entities → Creative Tab → WorldGen → Enchantments → Menus.
 */
public class CommonClass {

    public static void init() {
        Constants.LOG.info("Initialising {} on {} ({})",
                Constants.MOD_NAME,
                Services.PLATFORM.getPlatformName(),
                Services.PLATFORM.getEnvironmentName());

        ModBlocks.init();
        ModItems.init();
        ModGear.init();
        LegendaryItems.init();
        AircraftBlocks.init();
        AircraftEntities.init();
        ModEntities.init();
        ModCreativeTabs.init();
        ModWorldGen.init();
        ModStructures.init();
        ModEnchantments.init();
        ModMenuTypes.init();
    }
}