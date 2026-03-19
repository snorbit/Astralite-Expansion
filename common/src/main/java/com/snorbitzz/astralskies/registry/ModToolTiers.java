package com.snorbitzz.astralskies.registry;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;

/**
 * Defines the Astralite tool tier — surpassing Netherite in every category.
 * Harvest level 5 means it can mine anything Netherite can, plus mod-specific blocks.
 */
public enum ModToolTiers implements Tier {

    ASTRALITE(
            5,                  // harvest level (above netherite = 4)
            4000,               // durability
            12.0f,              // mining speed
            4.0f,               // attack damage bonus
            22,                 // enchantability
            BlockTags.INCORRECT_FOR_NETHERITE_TOOL // needs custom tag in real use; using netherite baseline
    );

    private final int level;
    private final int uses;
    private final float speed;
    private final float attackDamageBonus;
    private final int enchantmentValue;
    private final TagKey<net.minecraft.world.level.block.Block> incorrectBlocksForDrops;

    ModToolTiers(int level, int uses, float speed, float attackDamageBonus,
                 int enchantmentValue, TagKey<net.minecraft.world.level.block.Block> incorrectBlocksForDrops) {
        this.level = level;
        this.uses = uses;
        this.speed = speed;
        this.attackDamageBonus = attackDamageBonus;
        this.enchantmentValue = enchantmentValue;
        this.incorrectBlocksForDrops = incorrectBlocksForDrops;
    }

    @Override
    public int getUses() {
        return uses;
    }

    @Override
    public float getSpeed() {
        return speed;
    }

    @Override
    public float getAttackDamageBonus() {
        return attackDamageBonus;
    }

    @Override
    public TagKey<net.minecraft.world.level.block.Block> getIncorrectBlocksForDrops() {
        return incorrectBlocksForDrops;
    }

    @Override
    public int getEnchantmentValue() {
        return enchantmentValue;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return Ingredient.of(ModItems.ASTRALITE_SCRAP.get());
    }
}
