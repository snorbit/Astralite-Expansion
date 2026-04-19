package com.snorbitzz.astralskies.item;

import com.snorbitzz.astralskies.menu.AstralPowersMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

/**
 * Astral Tome — the key to unlocking Astral Powers.
 *
 * Right-click to open a beautiful UI with 6 powerful abilities.
 * Crafted from Astralite Shards and a Book.
 *
 * The tome glows with a subtle astral energy (foil/enchantment glint).
 */
public class AstralTomeItem extends Item {

    private static final Component TITLE = Component.literal("§b✦ Astral Powers ✦");

    public AstralTomeItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide) {
            player.openMenu(new SimpleMenuProvider(
                    (containerId, inv, p) -> new AstralPowersMenu(containerId, inv),
                    TITLE
            ));
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true; // Always shows enchantment glint
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context,
                                List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("§b✦ An ancient tome pulsing with astral energy."));
        tooltip.add(Component.literal("§7Right-click to open the §bAstral Powers §7menu."));
        tooltip.add(Component.literal("§86 powerful abilities await within."));
    }
}
