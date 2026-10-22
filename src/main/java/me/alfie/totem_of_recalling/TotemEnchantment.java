package me.alfie.totem_of_recalling;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;

// Treasure-only enchantment for the Totem of Recalling, only found as end city loot
public class TotemEnchantment extends Enchantment {

    private final int maxLevel;

    public TotemEnchantment(Rarity rarity, int maxLevel) {
        super(rarity, ModEnchantments.TOTEM_CATEGORY, new EquipmentSlot[]{EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND});
        this.maxLevel = maxLevel;
    }

    @Override
    public int getMaxLevel() {
        return maxLevel;
    }

    @Override
    public int getMinCost(int level) {
        return 25 + (level - 1) * 10;
    }

    @Override
    public int getMaxCost(int level) {
        return 75 + (level - 1) * 10;
    }

    @Override
    public boolean isTreasureOnly() {
        return true;
    }

    // Keeps them out of villager trades and random enchanted loot, end city loot adds them explicitly
    @Override
    public boolean isTradeable() {
        return false;
    }

    @Override
    public boolean isDiscoverable() {
        return false;
    }
}
