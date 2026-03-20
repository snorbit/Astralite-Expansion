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

import java.util.List;

/**
 * Kurumi Egg — thrown/used to hatch a Kurumi Companion pet.
 *
 * Right-click to hatch — spawns one KurumiCompanionEntity tamed to the user.
 * Single-use (stack size 1, consumed on use).
 * Only obtainable by defeating the Kurumi boss.
 */
public class KurumiEggItem extends Item {

    public KurumiEggItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide && level instanceof ServerLevel serverLevel) {
            KurumiCompanionEntity companion = ModEntities.KURUMI_COMPANION.create(serverLevel);
            if (companion != null) {
                companion.setPos(player.getX(), player.getY() + 1, player.getZ());
                companion.tame(player);
                companion.setOwnerUUID(player.getUUID());
                serverLevel.addFreshEntity(companion);

                player.sendSystemMessage(Component.literal(
                        "§d✦ Kurumi: §f\"Fufu~ hello, master. I'll be in your care from now on.\""));

                // Consume the egg
                if (!player.isCreative()) stack.shrink(1);
            }
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context,
                                List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("§5A mysterious egg that radiates time energy."));
        tooltip.add(Component.literal("§dRight-click §fto hatch your very own Kurumi companion."));
        tooltip.add(Component.literal("§8Obtained by defeating §5†Kurumi†"));
    }
}
