package com.snorbitzz.astralskies.entity;

import com.snorbitzz.astralskies.Constants;
import com.snorbitzz.astralskies.entity.boss.*;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

/**
 * Registers all Astral Skies custom mob/boss/NPC entity types.
 */
public class ModEntities {

    // ─── Riley NPC ────────────────────────────────────────────────────────────
    public static final EntityType<RileyNpc> RILEY_NPC =
            EntityType.Builder.<RileyNpc>of(RileyNpc::new, MobCategory.CREATURE)
                    .sized(0.6f, 1.2f).clientTrackingRange(8).build(rl("riley_npc"));

    // ─── Riley Boss ───────────────────────────────────────────────────────────
    public static final EntityType<RileyBoss> RILEY_BOSS =
            EntityType.Builder.<RileyBoss>of(RileyBoss::new, MobCategory.MONSTER)
                    .sized(0.7f, 1.4f).clientTrackingRange(12).build(rl("riley_boss"));

    // ─── Kurumi Boss (secret) ─────────────────────────────────────────────────
    public static final EntityType<KurumiEntity> KURUMI_BOSS =
            EntityType.Builder.<KurumiEntity>of(KurumiEntity::new, MobCategory.MONSTER)
                    .sized(0.6f, 1.95f).clientTrackingRange(16).build(rl("kurumi_boss"));

    // ─── Kurumi Companion (pet) ───────────────────────────────────────────────
    public static final EntityType<KurumiCompanionEntity> KURUMI_COMPANION =
            EntityType.Builder.<KurumiCompanionEntity>of(KurumiCompanionEntity::new, MobCategory.CREATURE)
                    .sized(0.6f, 1.95f).clientTrackingRange(8).build(rl("kurumi_companion"));

    // ─── Legendary Tier Bosses ────────────────────────────────────────────────

    public static final EntityType<LeviathanBoss> LEVIATHAN =
            EntityType.Builder.<LeviathanBoss>of(LeviathanBoss::new, MobCategory.MONSTER)
                    .sized(2.5f, 3.0f).clientTrackingRange(16).build(rl("leviathan"));

    public static final EntityType<StormWardenBoss> STORM_WARDEN =
            EntityType.Builder.<StormWardenBoss>of(StormWardenBoss::new, MobCategory.MONSTER)
                    .sized(1.2f, 2.8f).clientTrackingRange(16).build(rl("storm_warden"));

    public static final EntityType<VoidStalkerBoss> VOID_STALKER =
            EntityType.Builder.<VoidStalkerBoss>of(VoidStalkerBoss::new, MobCategory.MONSTER)
                    .sized(0.8f, 2.4f).clientTrackingRange(16).build(rl("void_stalker"));

    public static final EntityType<CrystalSovereignBoss> CRYSTAL_SOVEREIGN =
            EntityType.Builder.<CrystalSovereignBoss>of(CrystalSovereignBoss::new, MobCategory.MONSTER)
                    .sized(1.0f, 2.6f).clientTrackingRange(16).build(rl("crystal_sovereign"));

    public static final EntityType<SkyTyrantBoss> SKY_TYRANT =
            EntityType.Builder.<SkyTyrantBoss>of(SkyTyrantBoss::new, MobCategory.MONSTER)
                    .sized(1.1f, 2.8f).clientTrackingRange(16).build(rl("sky_tyrant"));

    public static final EntityType<RuinedTitanBoss> RUINED_TITAN =
            EntityType.Builder.<RuinedTitanBoss>of(RuinedTitanBoss::new, MobCategory.MONSTER)
                    .sized(3.0f, 4.0f).clientTrackingRange(16).build(rl("ruined_titan"));

    // ─── Init ─────────────────────────────────────────────────────────────────

    public static void init() {
        reg("riley_npc", RILEY_NPC);
        reg("riley_boss", RILEY_BOSS);
        reg("kurumi_boss", KURUMI_BOSS);
        reg("kurumi_companion", KURUMI_COMPANION);
        reg("leviathan", LEVIATHAN);
        reg("storm_warden", STORM_WARDEN);
        reg("void_stalker", VOID_STALKER);
        reg("crystal_sovereign", CRYSTAL_SOVEREIGN);
        reg("sky_tyrant", SKY_TYRANT);
        reg("ruined_titan", RUINED_TITAN);
        Constants.LOG.info("Astral Skies entities registered (10 types)");
    }

    private static <T extends net.minecraft.world.entity.Entity> void reg(String name, EntityType<T> type) {
        Registry.register(BuiltInRegistries.ENTITY_TYPE,
                ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, name), type);
    }

    private static String rl(String path) {
        return ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, path).toString();
    }
}
