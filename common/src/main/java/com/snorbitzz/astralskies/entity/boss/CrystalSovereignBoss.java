package com.snorbitzz.astralskies.entity.boss;

import com.snorbitzz.astralskies.registry.LegendaryItems;
import com.snorbitzz.astralskies.registry.ModItems;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * Crystal Sovereign — a majestic crystalline ruler of the sky islands.
 * HP: 950 | Damage: 18 | Boss bar: PINK
 * Ability: Crystal Shield (Resistance I on self) + Crystal Shards (wither players nearby).
 * Drops: Sovereign's Boots (legendary)
 */
public class CrystalSovereignBoss extends PathfinderMob {

    private final ServerBossEvent bossBar;
    private int abilityCooldown = 120;

    public CrystalSovereignBoss(EntityType<? extends CrystalSovereignBoss> type, Level level) {
        super(type, level);
        this.setCustomName(Component.literal("§d💎 Crystal Sovereign"));
        this.bossBar = new ServerBossEvent(
                Component.literal("§d💎 Crystal Sovereign"),
                BossEvent.BossBarColor.PINK,
                BossEvent.BossBarOverlay.NOTCHED_20
        );
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 950.0)
                .add(Attributes.ATTACK_DAMAGE, 18.0)
                .add(Attributes.MOVEMENT_SPEED, 0.30)
                .add(Attributes.FOLLOW_RANGE, 48.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.9)
                .add(Attributes.ARMOR, 14.0)   // heaviest armor
                .add(Attributes.ARMOR_TOUGHNESS, 6.0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0, true));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 0.8));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 16.0f));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) return;
        bossBar.setProgress(getHealth() / getMaxHealth());

        if (abilityCooldown-- <= 0) {
            // Crystal Shield phase
            this.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 100, 0, false, false));
            // Crystal Shards — wither nearby players
            level().getEntitiesOfClass(Player.class, getBoundingBox().inflate(10)).forEach(p -> {
                p.addEffect(new MobEffectInstance(MobEffects.WITHER, 80, 1, false, true));
                p.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 0, false, true));
            });
            abilityCooldown = 120;
        }
    }

    @Override public void startSeenByPlayer(ServerPlayer p) { super.startSeenByPlayer(p); bossBar.addPlayer(p); }
    @Override public void stopSeenByPlayer(ServerPlayer p) { super.stopSeenByPlayer(p); bossBar.removePlayer(p); }

    @Override
    public void die(DamageSource src) {
        super.die(src);
        if (!level().isClientSide && src.getEntity() instanceof Player p) {
            p.sendSystemMessage(Component.literal("§dThe Crystal Sovereign shatters into a thousand shards."));
            spawnAtLocation(new ItemStack(LegendaryItems.SOVEREIGN_BOOTS.get(), 1));
            spawnAtLocation(new ItemStack(ModItems.ASTRALITE_SHARD.get(), 4 + (int)(Math.random() * 4)));
        }
    }
}
