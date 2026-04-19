package com.snorbitzz.astralskies.entity;

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
import net.minecraft.world.phys.Vec3;

/**
 * Kurumi Tokisaki — Secret Endgame Boss.
 *
 * Inspired by the Spirit of Time from Date A Live.
 * Spawned only through a hidden trigger (Phase 4 secret system for Snorbitzz_Jnr).
 * Does NOT appear naturally — must be summoned.
 *
 * Stats: 800 HP, high attack (18 dmg), fast movement.
 * Boss bar: PURPLE with SEGMENTED overlay.
 *
 * Abilities (implemented as periodic effects):
 *  Phase 1 (>66% HP): Melee + Slowness aura on nearby players.
 *  Phase 2 (33–66% HP): Adds Weakness to her attacks.
 *  Phase 3 (<33% HP): Enrages — speed boost (+30%), lifesteal on hit.
 *
 * Drops (on death):
 *  - 1× Zafkiel (the clock weapon — unique item)
 *  - 1× Kurumi Egg (hatches into the Kurumi companion pet)
 *  - 5–8× Astralite Shard
 *
 * Intro quote when a player gets within aggro range.
 */
public class KurumiEntity extends PathfinderMob {

    private final ServerBossEvent bossBar;
    private boolean hasPlayedIntro = false;
    private int phaseAbilityCooldown = 0;

    public KurumiEntity(EntityType<? extends KurumiEntity> type, Level level) {
        super(type, level);
        this.setCustomName(Component.literal("§5†Kurumi†"));
        this.setCustomNameVisible(true);
        this.bossBar = new ServerBossEvent(
                Component.literal("§5✦ Kurumi Tokisaki ✦"),
                BossEvent.BossBarColor.PURPLE,
                BossEvent.BossBarOverlay.NOTCHED_10
        );
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 800.0)
                .add(Attributes.ATTACK_DAMAGE, 18.0)
                .add(Attributes.MOVEMENT_SPEED, 0.38)
                .add(Attributes.FOLLOW_RANGE, 48.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.8)
                .add(Attributes.ARMOR, 8.0)
                .add(Attributes.ARMOR_TOUGHNESS, 4.0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.2, true));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 16.0f));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) return;

        float hpFraction = this.getHealth() / this.getMaxHealth();
        bossBar.setProgress(hpFraction);

        // Intro message when a player enters range
        if (!hasPlayedIntro) {
            Player near = level().getNearestPlayer(this, 48.0);
            if (near != null) {
                near.sendSystemMessage(Component.literal(
                        "§5✦ Kurumi: §d\"My, my... a visitor. How delightful. " +
                        "I've been waiting for someone interesting to devour.\""));
                hasPlayedIntro = true;
            }
        }

        // Phase abilities
        if (phaseAbilityCooldown-- <= 0) {
            applyPhaseAbility(hpFraction);
            phaseAbilityCooldown = 60;
        }

        // Phase 3 enrage — speed boost
        if (hpFraction < 0.33f) {
            this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 40, 1, false, false));
            this.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 40, 0, false, false));
        }
    }

    private void applyPhaseAbility(float hpFraction) {
        if (hpFraction > 0.66f) {
            // Phase 1: Slowness aura on nearby players
            level().getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(10.0)).forEach(p ->
                    p.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 1, false, true)));
        } else if (hpFraction > 0.33f) {
            // Phase 2: Weakness aura
            level().getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(10.0)).forEach(p ->
                    p.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 100, 0, false, true)));
        } else {
            // Phase 3: Darkness aura — disorients the player
            level().getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(12.0)).forEach(p ->
                    p.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 80, 0, false, true)));
        }
    }

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        this.bossBar.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.bossBar.removePlayer(player);
    }

    @Override
    public void die(DamageSource source) {
        super.die(source);
        if (!level().isClientSide && source.getEntity() instanceof Player player) {
            player.sendSystemMessage(Component.literal(
                    "§5✦ Kurumi: §d\"Fufu... how unexpected. But remember..." +
                    " §5time is never truly defeated.§d\""));

            // Drop: Zafkiel + Kurumi Egg + Shards
            this.spawnAtLocation(new ItemStack(ModItems.ZAFKIEL.get(), 1));
            this.spawnAtLocation(new ItemStack(ModItems.KURUMI_EGG.get(), 1));
            this.spawnAtLocation(new ItemStack(ModItems.ASTRALITE_SHARD.get(),
                    5 + (int)(Math.random() * 4)));
        }
    }
}
