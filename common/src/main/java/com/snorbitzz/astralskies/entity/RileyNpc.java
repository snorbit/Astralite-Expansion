package com.snorbitzz.astralskies.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import java.util.List;
import java.util.Random;

/**
 * Riley NPC — a small annoying humanoid NPC that spawns in sky biomes.
 *
 * Behaviour:
 *  - Follows the nearest player and frequently interrupts them with silly messages.
 *  - Cannot be killed (regains full HP instantly when damaged).
 *  - Cannot attack but stands in the way.
 *  - Makes random irritating comments every 3–8 seconds.
 *  - If the player tries to push her off the edge, she teleports back.
 */
public class RileyNpc extends PathfinderMob {

    private static final String[] RILEY_LINES = {
        "Hey wait for me!!",
        "Where are you going?? Take me with you!",
        "Did you find any diamonds yet? I want some.",
        "Are we there yet?",
        "This sky is SO pretty. Can we stop and look at it?",
        "I'm bored. Can we fight something?",
        "Hey! You almost stepped on me!!",
        "Wow you're really tall.",
        "I picked up something shiny! Oh wait it's just dirt.",
        "Do you think the Leviathan can see us from here?",
        "I'm scared of heights... just kidding I'm totally fine.",
        "HEY HEY HEY look what I found!",
        "This is MY sky island now.",
        "You dropped something! ...Oh no you didn't. Never mind.",
        "Can I have some food? I'm hungry.",
        "WAIT! ...okay never mind I thought I saw something.",
    };

    private int chatCooldown = 0;
    private static final Random RAND = new Random();

    public RileyNpc(EntityType<? extends RileyNpc> type, Level level) {
        super(type, level);
        this.setCustomName(Component.literal("Riley"));
        this.setCustomNameVisible(true);
        this.setInvulnerable(true);  // She cannot be killed
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.MOVEMENT_SPEED, 0.32)
                .add(Attributes.FOLLOW_RANGE, 24.0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new FollowPlayerGoal(this));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 0.8));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8.0f));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
    }

    @Override
    public void tick() {
        super.tick();

        // Always re-heal immediately (invincible)
        if (this.getHealth() < this.getMaxHealth()) {
            this.setHealth(this.getMaxHealth());
        }

        if (!level().isClientSide) {
            // Random chat
            if (chatCooldown <= 0) {
                Player nearby = level().getNearestPlayer(this, 16.0);
                if (nearby != null) {
                    String line = RILEY_LINES[RAND.nextInt(RILEY_LINES.length)];
                    nearby.sendSystemMessage(Component.literal("§e[Riley] §f" + line));
                    chatCooldown = 60 + RAND.nextInt(100); // 3–8 seconds
                }
            } else {
                chatCooldown--;
            }

            // Teleport back if she falls too far below her last solid position
            if (this.getY() < -64) {
                Player nearest = level().getNearestPlayer(this, 64.0);
                if (nearest != null) {
                    this.teleportTo(nearest.getX(), nearest.getY() + 1, nearest.getZ());
                }
            }
        }
    }

    /** Simple goal: follow the player relentlessly. */
    private static class FollowPlayerGoal extends Goal {
        private final PathfinderMob mob;
        private Player target;

        FollowPlayerGoal(PathfinderMob mob) { this.mob = mob; }

        @Override
        public boolean canUse() {
            target = mob.level().getNearestPlayer(mob, 24.0);
            return target != null && mob.distanceTo(target) > 4.0;
        }

        @Override
        public void tick() {
            if (target != null) mob.getNavigation().moveTo(target, 1.0);
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        // She takes zero damage (invincible)
        return false;
    }
}
