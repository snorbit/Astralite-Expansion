package com.snorbitzz.astralskies.registry;

import com.snorbitzz.astralskies.Constants;
import com.snorbitzz.astralskies.aircraft.AircraftBlocks;
import com.snorbitzz.astralskies.registry.LegendaryItems;
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

                        // Aircraft components
                        output.accept(AircraftBlocks.AIRCRAFT_HULL);
                        output.accept(AircraftBlocks.AIRCRAFT_ENGINE);
                        output.accept(AircraftBlocks.AIRCRAFT_PROPELLER);
                        output.accept(AircraftBlocks.AIRCRAFT_CONTROL_PANEL);

                        // Special / boss drops (Kurumi)
                        output.accept(ModItems.ZAFKIEL.get());
                        output.accept(ModItems.KURUMI_EGG.get());

                        // Legendary boss drops (armor)
                        output.accept(LegendaryItems.LEVIATHAN_HELMET.get());
                        output.accept(LegendaryItems.STORMCALL_CHESTPLATE.get());
                        output.accept(LegendaryItems.VOID_STALKER_LEGGINGS.get());
                        output.accept(LegendaryItems.SOVEREIGN_BOOTS.get());

                        // Legendary boss drops (weapons)
                        output.accept(LegendaryItems.TYRANTS_BLADE.get());
                        output.accept(LegendaryItems.TITANS_PICKAXE.get());
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
