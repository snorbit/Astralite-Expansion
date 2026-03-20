package com.snorbitzz.astralskies.item;

import com.snorbitzz.astralskies.entity.KurumiCompanionEntity;
import com.snorbitzz.astralskies.entity.ModEntities;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Zafkiel — the clock weapon dropped by Kurumi Tokisaki.
 *
 * Zafkiel is Kurumi's Angel (spirit weapon) — a large flintlock pistol shaped
 * like a clock. In the mod it is implemented as a special use-item because
 * weapons are normally attribute-based in 1.21.1.
 *
 * On right-click, it fires a "bullet of time":
 *  - Applies Slowness IV to all enemies within 20 blocks for 10 seconds.
 *  - Heals the user for 6 HP immediately.
 *  - 200 tick cooldown (10 sec).
 *
 * The item itself has high attack damage via the tool tier system and is
 * registered as a sword subtype for melee (enchantable, etc).
 */
public class ZafkielItem extends Item {

    private static final int COOLDOWN_TICKS = 200;

    public ZafkielItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (player.getCooldowns().isOnCooldown(this)) {
            return InteractionResultHolder.fail(stack);
        }

        if (!level.isClientSide) {
            // Slowness aura
            level.getEntitiesOfClass(
                    net.minecraft.world.entity.LivingEntity.class,
                    player.getBoundingBox().inflate(20.0),
                    e -> e != player
            ).forEach(entity -> entity.addEffect(
                    new net.minecraft.world.effect.MobEffectInstance(
                            net.minecraft.world.effect.MobEffects.MOVEMENT_SLOWDOWN,
                            200, 3, false, true)
            ));

            // Heal player
            player.heal(6.0f);

            // Visual/audio feedback
            level.playSound(null, player.blockPosition(),
                    net.minecraft.sounds.SoundEvents.BELL_BLOCK,
                    net.minecraft.sounds.SoundSource.PLAYERS,
                    1.0f, 0.5f);

            player.sendSystemMessage(Component.literal(
                    "§5⏱ Zafkiel: §d\"Aleph — the first bullet of time.\""));
        }

        player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context,
                                List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("§5Kurumi's Angel — the Spirit's clock weapon."));
        tooltip.add(Component.literal("§dRight-click: §fFire Aleph — Slowness IV (20 blocks) + +6 HP"));
        tooltip.add(Component.literal("§810 second cooldown"));
    }
}
