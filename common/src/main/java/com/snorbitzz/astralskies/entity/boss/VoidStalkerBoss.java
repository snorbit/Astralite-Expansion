package com.snorbitzz.astralskies.entity.boss;

import com.snorbitzz.astralskies.registry.LegendaryItems;
import com.snorbitzz.astralskies.registry.ModItems;
import net.minecraft.core.BlockPos;
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

import java.util.Random;

/**
 * Void Stalker — a shadowy predator that teleports, confuses, and corrupts players.
 * HP: 900 | Damage: 19 | Boss bar: WHITE (ghostly)
 * Ability: Teleports behind the player + applies Nausea + Poison.
 * Drops: Void Stalker Leggings (legendary)
 */
public class VoidStalkerBoss extends PathfinderMob {

    private final ServerBossEvent bossBar;
    private int teleportCooldown = 120;
    private static final Random RAND = new Random();

    public VoidStalkerBoss(EntityType<? extends VoidStalkerBoss> type, Level level) {
        super(type, level);
        this.setCustomName(Component.literal("§f◈ Void Stalker"));
        this.bossBar = new ServerBossEvent(
                Component.literal("§f◈ Void Stalker"),
                BossEvent.BossBarColor.WHITE,
                BossEvent.BossBarOverlay.SEGMENTED_20
        );
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 900.0)
                .add(Attributes.ATTACK_DAMAGE, 19.0)
                .add(Attributes.MOVEMENT_SPEED, 0.40)   // fast
                .add(Attributes.FOLLOW_RANGE, 64.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.7)
                .add(Attributes.ARMOR, 6.0)
                .add(Attributes.ARMOR_TOUGHNESS, 3.0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.2, true));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 16.0f));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) return;
        bossBar.setProgress(getHealth() / getMaxHealth());

        if (teleportCooldown-- <= 0) {
            if (getTarget() instanceof Player target) {
                // Teleport behind the player
                double angle = Math.toRadians(target.getYRot() + 180);
                double tx = target.getX() + Math.sin(angle) * 2;
                double tz = target.getZ() + Math.cos(angle) * 2;
                teleportTo(tx, target.getY(), tz);

                // Shadow curse
                target.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 120, 0, false, true));
                target.addEffect(new MobEffectInstance(MobEffects.POISON, 100, 1, false, true));
                target.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 80, 0, false, true));

                if (target instanceof ServerPlayer sp)
                    sp.sendSystemMessage(Component.literal("§fThe Void Stalker flickers behind you..."));
            }
            teleportCooldown = 140;
        }
    }

    @Override public void startSeenByPlayer(ServerPlayer p) { super.startSeenByPlayer(p); bossBar.addPlayer(p); }
    @Override public void stopSeenByPlayer(ServerPlayer p) { super.stopSeenByPlayer(p); bossBar.removePlayer(p); }

    @Override
    public void die(DamageSource src) {
        super.die(src);
        if (!level().isClientSide && src.getEntity() instanceof Player p) {
            p.sendSystemMessage(Component.literal("§fThe Void Stalker dissolves into shadow."));
            spawnAtLocation(new ItemStack(LegendaryItems.VOID_STALKER_LEGGINGS.get(), 1));
            spawnAtLocation(new ItemStack(ModItems.ASTRALITE_UPGRADE_TEMPLATE.get(), 1));
            spawnAtLocation(new ItemStack(ModItems.ASTRALITE_SHARD.get(), 3 + (int)(Math.random() * 3)));
        }
    }
}
