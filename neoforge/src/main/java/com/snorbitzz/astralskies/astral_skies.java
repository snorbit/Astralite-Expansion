package com.snorbitzz.astralskies;

import com.snorbitzz.astralskies.entity.ModEntities;
import com.snorbitzz.astralskies.entity.KurumiCompanionEntity;
import com.snorbitzz.astralskies.entity.KurumiEntity;
import com.snorbitzz.astralskies.entity.RileyBoss;
import com.snorbitzz.astralskies.entity.RileyNpc;
import com.snorbitzz.astralskies.entity.boss.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;

@Mod(Constants.MOD_ID)
public class astral_skies {

    public astral_skies(IEventBus eventBus) {
        Constants.LOG.info("Hello NeoForge world!");
        CommonClass.init();

        // Register entity attributes — required by NeoForge for all LivingEntity subtypes.
        // Without this every boss/mob crashes the game on first spawn.
        eventBus.addListener(astral_skies::registerEntityAttributes);
    }

    private static void registerEntityAttributes(EntityAttributeCreationEvent event) {
        // NPCs
        event.put(ModEntities.RILEY_NPC,      RileyNpc.createAttributes().build());
        event.put(ModEntities.RILEY_BOSS,     RileyBoss.createAttributes().build());

        // Kurumi entities
        event.put(ModEntities.KURUMI_BOSS,      KurumiEntity.createAttributes().build());
        event.put(ModEntities.KURUMI_COMPANION, KurumiCompanionEntity.createAttributes().build());

        // Legendary bosses
        event.put(ModEntities.LEVIATHAN,        LeviathanBoss.createAttributes().build());
        event.put(ModEntities.STORM_WARDEN,     StormWardenBoss.createAttributes().build());
        event.put(ModEntities.VOID_STALKER,     VoidStalkerBoss.createAttributes().build());
        event.put(ModEntities.CRYSTAL_SOVEREIGN,CrystalSovereignBoss.createAttributes().build());
        event.put(ModEntities.SKY_TYRANT,       SkyTyrantBoss.createAttributes().build());
        event.put(ModEntities.RUINED_TITAN,     RuinedTitanBoss.createAttributes().build());

        Constants.LOG.info("Astral Skies entity attributes registered.");
    }
}