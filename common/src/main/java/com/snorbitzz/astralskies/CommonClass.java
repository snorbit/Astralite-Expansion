package com.snorbitzz.astralskies;

import com.snorbitzz.astralskies.platform.Services;
import com.snorbitzz.astralskies.registry.ModBlocks;
import com.snorbitzz.astralskies.registry.ModCreativeTabs;
import com.snorbitzz.astralskies.registry.ModGear;
import com.snorbitzz.astralskies.registry.ModItems;

/**
 * Shared entry point — called by both the Fabric and NeoForge loader-specific entry points.
 * Registration order matters: Blocks → Items → Gear → Creative Tab.
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
        ModCreativeTabs.init();
    }
}