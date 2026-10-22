package me.alfie.totem_of_recalling;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

// Enchantments are data-driven, see data/totemofrecalling/enchantment
public class ModEnchantments {

    public static final ResourceKey<Enchantment> ENDERSTEP = key("enderstep");
    public static final ResourceKey<Enchantment> RIFT_RECOVERY = key("rift_recovery");
    public static final ResourceKey<Enchantment> BLINKING = key("blinking");
    public static final ResourceKey<Enchantment> DRACONIC_EFFICIENCY = key("draconic_efficiency.json");

    private static ResourceKey<Enchantment> key(String name) {
        return ResourceKey.create(Registries.ENCHANTMENT, ResourceLocation.fromNamespaceAndPath(TotemOfRecalling.MODID, name));
    }

    public static int getLevel(Player player, ResourceKey<Enchantment> enchantment, ItemStack stack) {
        return player.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).get(enchantment)
                .map(holder -> EnchantmentHelper.getItemEnchantmentLevel(holder, stack))
                .orElse(0);
    }
}
