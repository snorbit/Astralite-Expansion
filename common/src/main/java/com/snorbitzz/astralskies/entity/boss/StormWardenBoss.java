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
 * Storm Warden — a towering lightning-wreathed sky guardian.
 * HP: 1000 | Damage: 20 | Boss bar: YELLOW
 * Ability: Lightning aura — periodic Weakness + Blindness on nearby players.
 * Drops: Stormcall Chestplate (legendary)
 */
public class StormWardenBoss extends PathfinderMob {

    private final ServerBossEvent bossBar;
    private int abilityCooldown = 100;

    public StormWardenBoss(EntityType<? extends StormWardenBoss> type, Level level) {
        super(type, level);
        this.setCustomName(Component.literal("§e⚡ Storm Warden"));
        this.bossBar = new ServerBossEvent(
                Component.literal("§e⚡ Storm Warden"),
                BossEvent.BossBarColor.YELLOW,
                BossEvent.BossBarOverlay.SEGMENTED_20
        );
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 1000.0)
                .add(Attributes.ATTACK_DAMAGE, 20.0)
                .add(Attributes.MOVEMENT_SPEED, 0.34)
                .add(Attributes.FOLLOW_RANGE, 48.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.8)
                .add(Attributes.ARMOR, 8.0)
                .add(Attributes.ARMOR_TOUGHNESS, 4.0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.1, true));
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
            // Storm Pulse: Weakness + Blindness on nearby players
            level().getEntitiesOfClass(Player.class, getBoundingBox().inflate(12)).forEach(p -> {
                p.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 100, 1, false, true));
                p.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 60, 0, false, true));
            });
            // Strike lightning at player if on server
            if (getTarget() instanceof Player target && level() instanceof net.minecraft.server.level.ServerLevel sl) {
                sl.strikeLightning(net.minecraft.world.entity.EntityType.LIGHTNING_BOLT.create(sl));
            }
            abilityCooldown = 100;
        }
    }

    @Override public void startSeenByPlayer(ServerPlayer p) { super.startSeenByPlayer(p); bossBar.addPlayer(p); }
    @Override public void stopSeenByPlayer(ServerPlayer p) { super.stopSeenByPlayer(p); bossBar.removePlayer(p); }

    @Override
    public void die(DamageSource src) {
        super.die(src);
        if (!level().isClientSide && src.getEntity() instanceof Player p) {
            p.sendSystemMessage(Component.literal("§eThe Storm Warden falls silent. The thunder fades."));
            spawnAtLocation(new ItemStack(LegendaryItems.STORMCALL_CHESTPLATE.get(), 1));
            spawnAtLocation(new ItemStack(ModItems.ASTRALITE_SHARD.get(), 3 + (int)(Math.random() * 4)));
        }
    }
}
