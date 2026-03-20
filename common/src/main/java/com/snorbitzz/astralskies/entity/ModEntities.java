package com.snorbitzz.astralskies.entity;

import com.snorbitzz.astralskies.Constants;
import com.snorbitzz.astralskies.aircraft.AircraftEntities;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

/**
 * Registers all Astral Skies custom mob/boss/NPC entity types.
 */
public class ModEntities {

    // ─── Riley (annoying NPC) ─────────────────────────────────────────────────
    public static final EntityType<RileyNpc> RILEY_NPC =
            EntityType.Builder.<RileyNpc>of(RileyNpc::new, MobCategory.CREATURE)
                    .sized(0.6f, 1.2f)       // short!
                    .clientTrackingRange(8)
                    .build(rl("riley_npc"));

    // ─── Riley Boss (weak sky boss) ────────────────────────────────────────────
    public static final EntityType<RileyBoss> RILEY_BOSS =
            EntityType.Builder.<RileyBoss>of(RileyBoss::new, MobCategory.MONSTER)
                    .sized(0.7f, 1.4f)       // slightly bigger than NPC, still small
                    .clientTrackingRange(12)
                    .build(rl("riley_boss"));

    // ─── Kurumi Tokisaki (secret boss) ────────────────────────────────────────
    public static final EntityType<KurumiEntity> KURUMI_BOSS =
            EntityType.Builder.<KurumiEntity>of(KurumiEntity::new, MobCategory.MONSTER)
                    .sized(0.6f, 1.95f)
                    .clientTrackingRange(16)
                    .build(rl("kurumi_boss"));

    // ─── Kurumi Companion (pet hatched from egg) ──────────────────────────────
    public static final EntityType<KurumiCompanionEntity> KURUMI_COMPANION =
            EntityType.Builder.<KurumiCompanionEntity>of(KurumiCompanionEntity::new, MobCategory.CREATURE)
                    .sized(0.6f, 1.95f)
                    .clientTrackingRange(8)
                    .build(rl("kurumi_companion"));

    public static void init() {
        Registry.register(BuiltInRegistries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "riley_npc"), RILEY_NPC);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "riley_boss"), RILEY_BOSS);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "kurumi_boss"), KURUMI_BOSS);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "kurumi_companion"), KURUMI_COMPANION);
        Constants.LOG.info("Astral Skies mob entities registered");
    }

    private static String rl(String path) {
        return ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, path).toString();
    }
}
