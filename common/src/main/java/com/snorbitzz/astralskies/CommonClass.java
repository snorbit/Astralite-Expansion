package com.snorbitzz.astralskies;

import com.snorbitzz.astralskies.aircraft.AircraftBlocks;
import com.snorbitzz.astralskies.aircraft.AircraftEntities;
import com.snorbitzz.astralskies.platform.Services;
import com.snorbitzz.astralskies.registry.ModBlocks;
import com.snorbitzz.astralskies.registry.ModCreativeTabs;
import com.snorbitzz.astralskies.registry.ModEnchantments;
import com.snorbitzz.astralskies.registry.ModGear;
import com.snorbitzz.astralskies.registry.ModItems;
import com.snorbitzz.astralskies.worldgen.ModWorldGen;

/**
 * Shared entry point for both Fabric and NeoForge.
 * Order: Blocks → Items → Gear → Aircraft → Creative Tab → WorldGen → Enchantments.
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
        AircraftBlocks.init();
        AircraftEntities.init();
        ModCreativeTabs.init();
        ModWorldGen.init();
        ModEnchantments.init();
    }
}