package com.snorbitzz.astralskies.entity;

import com.snorbitzz.astralskies.registry.ModItems;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.world.item.ItemStack;

/**
 * Riley Boss — a weak, short sky boss.
 *
 * Stats: 80 HP, low attack (4 damage), small hitbox.
 * This is the introductory boss — easy relative to Kurumi.
 *
 * Drops:
 *  - 3–5 Astralite Scrap
 *  - 1 Astralite Shard (guaranteed)
 *
 * Spawn: Sky islands (all biomes), rare random encounter.
 *
 * She yells taunts at the player while fighting and complains when she dies.
 */
public class RileyBoss extends PathfinderMob {

    private final ServerBossEvent bossBar;

    private static final String[] TAUNT_LINES = {
        "§c[Riley Boss] §fI'm not as weak as I look!!",
        "§c[Riley Boss] §fHey stop hitting me!!",
        "§c[Riley Boss] §fI'm actually a really strong fighter okay!",
        "§c[Riley Boss] §fYou won't beat me! Probably.",
        "§c[Riley Boss] §fIs that all you've got?? (please let it be)",
    };

    private int tauntCooldown = 80;

    public RileyBoss(EntityType<? extends RileyBoss> type, Level level) {
        super(type, level);
        this.setCustomName(Component.literal("Riley"));
        this.bossBar = new ServerBossEvent(
                this.getDisplayName(),
                BossEvent.BossBarColor.YELLOW,
                BossEvent.BossBarOverlay.NOTCHED_10
        );
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 80.0)
                .add(Attributes.ATTACK_DAMAGE, 4.0)
                .add(Attributes.MOVEMENT_SPEED, 0.30)
                .add(Attributes.FOLLOW_RANGE, 32.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.2);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0, true));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8.0f));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide) {
            bossBar.setProgress(this.getHealth() / this.getMaxHealth());

            if (tauntCooldown-- <= 0 && this.getTarget() instanceof Player player) {
                String line = TAUNT_LINES[(int) (Math.random() * TAUNT_LINES.length)];
                player.sendSystemMessage(Component.literal(line));
                tauntCooldown = 100 + (int) (Math.random() * 80);
            }
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
                    "§e[Riley] §fFine! You win!! But only this time!!!"));
            // Drop rewards
            this.spawnAtLocation(new ItemStack(ModItems.ASTRALITE_SCRAP.get(),
                    3 + (int) (Math.random() * 3)));
            this.spawnAtLocation(new ItemStack(ModItems.ASTRALITE_SHARD.get(), 1));
        }
    }
}
