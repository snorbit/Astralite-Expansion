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
 * The Leviathan — massive sky-sea serpent boss.
 * HP: 1200 | Damage: 22 | Phase: Slowness aura, enrages below 25%
 * Boss bar: BLUE (fitting its oceanic nature)
 * Drops: Leviathan Helmet (legendary)
 */
public class LeviathanBoss extends PathfinderMob {

    private final ServerBossEvent bossBar;
    private int abilityCooldown = 100;

    public LeviathanBoss(EntityType<? extends LeviathanBoss> type, Level level) {
        super(type, level);
        this.setCustomName(Component.literal("§9⚓ Leviathan"));
        this.bossBar = new ServerBossEvent(
                Component.literal("§9⚓ Leviathan"),
                BossEvent.BossBarColor.BLUE,
                BossEvent.BossBarOverlay.SEGMENTED_20
        );
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 1200.0)
                .add(Attributes.ATTACK_DAMAGE, 22.0)
                .add(Attributes.MOVEMENT_SPEED, 0.28)
                .add(Attributes.FOLLOW_RANGE, 48.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0)
                .add(Attributes.ARMOR, 10.0)
                .add(Attributes.ARMOR_TOUGHNESS, 5.0);
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
            // Tidal Aura: Slowness III + Mining Fatigue on nearby players
            level().getEntitiesOfClass(Player.class, getBoundingBox().inflate(14)).forEach(p -> {
                p.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 80, 2, false, true));
                p.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 80, 1, false, true));
            });
            abilityCooldown = 80;
        }
        // Enrage below 25%
        if (getHealth() / getMaxHealth() < 0.25f) {
            this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 40, 1, false, false));
            this.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 40, 1, false, false));
        }
    }

    @Override public void startSeenByPlayer(ServerPlayer p) { super.startSeenByPlayer(p); bossBar.addPlayer(p); }
    @Override public void stopSeenByPlayer(ServerPlayer p) { super.stopSeenByPlayer(p); bossBar.removePlayer(p); }

    @Override
    public void die(DamageSource src) {
        super.die(src);
        if (!level().isClientSide && src.getEntity() instanceof Player p) {
            p.sendSystemMessage(Component.literal("§9The Leviathan collapses — the sky trembles."));
            spawnAtLocation(new ItemStack(LegendaryItems.LEVIATHAN_HELMET.get(), 1));
            spawnAtLocation(new ItemStack(ModItems.ASTRALITE_SHARD.get(), 4 + (int)(Math.random() * 4)));
        }
    }
}
