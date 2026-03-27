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
 * Ruined Titan — a colossal crumbling giant, slow but devastatingly tough.
 * HP: 1300 (highest in the mod) | Damage: 20 | Boss bar: GREEN
 * Ability: Earthquake — Mining Fatigue + Slowness on all nearby players repeatedly.
 *          Regenerates over time when not being attacked.
 * Drops: Titan's Pickaxe (legendary pickaxe, strong enough to instamine sky stone)
 */
public class RuinedTitanBoss extends PathfinderMob {

    private final ServerBossEvent bossBar;
    private int abilityCooldown = 80;
    private int regenCooldown = 40;

    public RuinedTitanBoss(EntityType<? extends RuinedTitanBoss> type, Level level) {
        super(type, level);
        this.setCustomName(Component.literal("§2🗿 Ruined Titan"));
        this.bossBar = new ServerBossEvent(
                Component.literal("§2🗿 Ruined Titan"),
                BossEvent.BossBarColor.GREEN,
                BossEvent.BossBarOverlay.SEGMENTED_20
        );
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 1300.0)   // most HP
                .add(Attributes.ATTACK_DAMAGE, 20.0)
                .add(Attributes.MOVEMENT_SPEED, 0.22) // slowest boss
                .add(Attributes.FOLLOW_RANGE, 40.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0)
                .add(Attributes.ARMOR, 12.0)
                .add(Attributes.ARMOR_TOUGHNESS, 6.0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0, false));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 0.6));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 16.0f));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) return;
        bossBar.setProgress(getHealth() / getMaxHealth());

        // Earthquake pulse
        if (abilityCooldown-- <= 0) {
            level().getEntitiesOfClass(Player.class, getBoundingBox().inflate(16)).forEach(p -> {
                p.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 120, 3, false, true));
                p.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 80, 2, false, true));
                // Shake the screen via a rumble effect
                p.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 40, 0, false, true));
            });
            abilityCooldown = 80;
        }

        // Passive regen when not in combat
        if (regenCooldown-- <= 0) {
            if (getTarget() == null && getHealth() < getMaxHealth()) {
                this.heal(8.0f);
            }
            regenCooldown = 40;
        }
    }

    @Override public void startSeenByPlayer(ServerPlayer p) { super.startSeenByPlayer(p); bossBar.addPlayer(p); }
    @Override public void stopSeenByPlayer(ServerPlayer p) { super.stopSeenByPlayer(p); bossBar.removePlayer(p); }

    @Override
    public void die(DamageSource src) {
        super.die(src);
        if (!level().isClientSide && src.getEntity() instanceof Player p) {
            p.sendSystemMessage(Component.literal("§2The Ruined Titan crumbles to ancient stone."));
            spawnAtLocation(new ItemStack(LegendaryItems.TITANS_PICKAXE.get(), 1));
            spawnAtLocation(new ItemStack(ModItems.ASTRALITE_UPGRADE_TEMPLATE.get(), 2));
            spawnAtLocation(new ItemStack(ModItems.ASTRALITE_SCRAP.get(), 8 + (int)(Math.random() * 8)));
        }
    }
}
