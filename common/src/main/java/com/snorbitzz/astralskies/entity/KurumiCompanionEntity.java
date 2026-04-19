package com.snorbitzz.astralskies.entity;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * Kurumi Companion — player's pet, hatches from the Kurumi Egg dropped by the Kurumi boss.
 *
 * Behaviour:
 *  - Follows the owner everywhere.
 *  - Fights hostile mobs for the owner (like a tamed wolf but more powerful).
 *  - Says occasional supportive Kurumi-style quotes to the owner.
 *  - Very high HP so she doesn't die easily.
 *
 * Ownership: set by the player who throws the egg item (handled in the egg item class).
 */
public class KurumiCompanionEntity extends TamableAnimal {

    private static final String[] COMPANION_LINES = {
        "§d[Kurumi] §fI'll protect you, master~",
        "§d[Kurumi] §fShall we find something to fight? I'm in the mood.",
        "§d[Kurumi] §fStay close to me. The sky can be... dangerous.",
        "§d[Kurumi] §fFufu~ what an interesting place this is.",
        "§d[Kurumi] §fI wonder if there are any worthy opponents nearby?",
        "§d[Kurumi] §fYou seem tired. Rest — I'll keep watch.",
    };

    private int companionChatCooldown = 200;

    public KurumiCompanionEntity(EntityType<? extends KurumiCompanionEntity> type, Level level) {
        super(type, level);
        this.setCustomName(Component.literal("§d†Kurumi†"));
        this.setCustomNameVisible(true);
        this.setTame(false, false);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return TamableAnimal.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 200.0)
                .add(Attributes.ATTACK_DAMAGE, 12.0)
                .add(Attributes.MOVEMENT_SPEED, 0.36)
                .add(Attributes.FOLLOW_RANGE, 32.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.5);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.1, true));
        this.goalSelector.addGoal(4, new FollowOwnerGoal(this, 1.0, 6.0f, 2.0f));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0f));
        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide && isTame() && companionChatCooldown-- <= 0) {
            var owner = this.getOwner();
            if (owner instanceof Player player) {
                String line = COMPANION_LINES[(int)(Math.random() * COMPANION_LINES.length)];
                player.sendSystemMessage(Component.literal(line));
            }
            companionChatCooldown = 400 + (int)(Math.random() * 400); // 20–40 sec
        }
    }

    // ─── TamableAnimal implementation ─────────────────────────────────────────

    @Override
    public net.minecraft.world.entity.AgeableMob getBreedOffspring(net.minecraft.server.level.ServerLevel level,
                                                                     net.minecraft.world.entity.AgeableMob other) {
        return null; // Cannot breed
    }

    @Override
    public boolean isFood(net.minecraft.world.item.ItemStack stack) {
        return false; // Cannot breed, no food items
    }
}
