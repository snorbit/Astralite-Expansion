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
 * Sky Tyrant — a savage warlord of the sky who rules through brute force.
 * HP: 1100 | Damage: 24 (highest melee damage) | Boss bar: RED
 * Ability: Berserk charge — Strength III on self + knockback burst on players.
 * Drops: Tyrant's Blade (legendary sword, 18 dmg)
 */
public class SkyTyrantBoss extends PathfinderMob {

    private final ServerBossEvent bossBar;
    private int abilityCooldown = 100;

    public SkyTyrantBoss(EntityType<? extends SkyTyrantBoss> type, Level level) {
        super(type, level);
        this.setCustomName(Component.literal("§c🗡 Sky Tyrant"));
        this.bossBar = new ServerBossEvent(
                Component.literal("§c🗡 Sky Tyrant"),
                BossEvent.BossBarColor.RED,
                BossEvent.BossBarOverlay.NOTCHED_20
        );
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 1100.0)
                .add(Attributes.ATTACK_DAMAGE, 24.0)   // highest damage
                .add(Attributes.MOVEMENT_SPEED, 0.36)
                .add(Attributes.FOLLOW_RANGE, 48.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.9)
                .add(Attributes.ARMOR, 9.0)
                .add(Attributes.ARMOR_TOUGHNESS, 4.0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.2, true));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 0.9));
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
            // Berserk: self-buff + knock back all nearby players
            this.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 120, 2, false, false));
            this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 120, 1, false, false));
            level().getEntitiesOfClass(Player.class, getBoundingBox().inflate(8)).forEach(p -> {
                // Knockback burst
                double dx = p.getX() - getX();
                double dz = p.getZ() - getZ();
                double len = Math.sqrt(dx*dx + dz*dz);
                if (len > 0) p.push(dx/len * 2.5, 0.5, dz/len * 2.5);
                p.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 1, false, true));
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
            p.sendSystemMessage(Component.literal("§cThe Sky Tyrant roars his last battle cry and falls."));
            spawnAtLocation(new ItemStack(LegendaryItems.TYRANTS_BLADE.get(), 1));
            spawnAtLocation(new ItemStack(ModItems.ASTRALITE_SHARD.get(), 5 + (int)(Math.random() * 4)));
        }
    }
}
