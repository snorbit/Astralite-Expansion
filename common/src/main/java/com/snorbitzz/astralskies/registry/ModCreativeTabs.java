package com.snorbitzz.astralskies.registry;

import com.snorbitzz.astralskies.Constants;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

/**
 * Registers the Astral Skies creative mode tab.
 * All mod blocks and items appear here in order.
 */
public class ModCreativeTabs {

    public static final ModItems.RegistryObject<CreativeModeTab> ASTRAL_SKIES_TAB =
            register("astral_skies_tab", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.astral_skies.astral_skies_tab"))
                    .icon(() -> new ItemStack(ModItems.ASTRALITE_SHARD.get()))
                    .displayItems((parameters, output) -> {
                        // Terrain blocks
                        output.accept(ModBlocks.FLOATING_STONE.get());
                        output.accept(ModBlocks.ASTRAL_DIRT.get());
                        output.accept(ModBlocks.CRYSTAL_BLOCK.get());

                        // Ores
                        output.accept(ModBlocks.ASTRALITE_ORE.get());
                        output.accept(ModBlocks.DEEPSLATE_ASTRALITE_ORE.get());

                        // Processed blocks / structures
                        output.accept(ModBlocks.ASTRALITE_BLOCK.get());
                        output.accept(ModBlocks.ASTRAL_PORTAL_FRAME.get());

                        // Raw materials
                        output.accept(ModItems.RAW_ASTRALITE.get());
                        output.accept(ModItems.ASTRALITE_SCRAP.get());
                        output.accept(ModItems.ASTRALITE_SHARD.get());
                        output.accept(ModItems.ASTRALITE_UPGRADE_TEMPLATE.get());

                        // Tools & utility
                        output.accept(ModItems.ASTRAL_COMPASS.get());

                        // Weapons
                        output.accept(ModGear.ASTRALITE_SWORD.get());

                        // Armor
                        output.accept(ModGear.ASTRALITE_HELMET.get());
                        output.accept(ModGear.ASTRALITE_CHESTPLATE.get());
                        output.accept(ModGear.ASTRALITE_LEGGINGS.get());
                        output.accept(ModGear.ASTRALITE_BOOTS.get());
                    })
                    .build());

    public static void init() {
        Constants.LOG.info("Registering Astral Skies Creative Tab");
    }

    private static ModItems.RegistryObject<CreativeModeTab> register(String name, java.util.function.Supplier<CreativeModeTab> supplier) {
        CreativeModeTab tab = supplier.get();
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB,
                ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, name), tab);
        return new ModItems.RegistryObject<>(tab);
    }
}
