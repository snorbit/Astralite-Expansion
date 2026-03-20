package com.snorbitzz.astralskies.aircraft;

import com.snorbitzz.astralskies.Constants;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

/**
 * Registers the AircraftEntity type.
 * Dimensions: 1.5w × 1.5h (roughly the size of a boat but taller).
 */
public class AircraftEntities {

    public static final EntityType<AircraftEntity> AIRCRAFT =
            EntityType.Builder.<AircraftEntity>of(AircraftEntity::new, MobCategory.MISC)
                    .sized(1.5f, 1.5f)
                    .clientTrackingRange(8)
                    .build(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "aircraft").toString());

    public static void init() {
        Registry.register(BuiltInRegistries.ENTITY_TYPE,
                ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "aircraft"),
                AIRCRAFT);
        Constants.LOG.info("Registered AircraftEntity");
    }
}
