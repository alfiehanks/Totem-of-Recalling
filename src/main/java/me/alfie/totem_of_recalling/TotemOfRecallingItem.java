package me.alfie.totem_of_recalling;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

// 1.21.1 has no enchantable component, so enchantability is set by overriding these
public class TotemOfRecallingItem extends Item {

    public TotemOfRecallingItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }

    @Override
    public int getEnchantmentValue() {
        return 1;
    }
}
