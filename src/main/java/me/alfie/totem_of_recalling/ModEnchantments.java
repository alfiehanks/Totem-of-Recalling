package me.alfie.totem_of_recalling;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

// 1.20.1 enchantments aren't data-driven, so they're registered here
public class ModEnchantments {

    public static final EnchantmentCategory TOTEM_CATEGORY = EnchantmentCategory.create("TOTEM_OF_RECALLING", item -> item instanceof TotemOfRecallingItem);

    public static final DeferredRegister<Enchantment> ENCHANTMENTS = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, TotemOfRecalling.MODID);

    // Rarity sets the anvil cost: RARE = 4, VERY_RARE = 8 (halved when applied from a book)
    public static final RegistryObject<Enchantment> ENDERSTEP = ENCHANTMENTS.register("enderstep", () -> new TotemEnchantment(Enchantment.Rarity.VERY_RARE, 1));
    public static final RegistryObject<Enchantment> RIFT_RECOVERY = ENCHANTMENTS.register("rift_recovery", () -> new TotemEnchantment(Enchantment.Rarity.RARE, 3));
    public static final RegistryObject<Enchantment> BLINKING = ENCHANTMENTS.register("blinking", () -> new TotemEnchantment(Enchantment.Rarity.RARE, 3));
    public static final RegistryObject<Enchantment> DRACONIC_EFFICIENCY = ENCHANTMENTS.register("draconic_efficiency", () -> new TotemEnchantment(Enchantment.Rarity.VERY_RARE, 1));

    public static int getLevel(Player player, RegistryObject<Enchantment> enchantment, ItemStack stack) {
        return EnchantmentHelper.getItemEnchantmentLevel(enchantment.get(), stack);
    }
}
