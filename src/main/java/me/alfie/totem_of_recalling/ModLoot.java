package me.alfie.totem_of_recalling;

import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.EnchantRandomlyFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraftforge.event.LootTableLoadEvent;

// Adds the totem enchantments to end city chests
public class ModLoot {

    private static final float END_CITY_BOOK_CHANCE = 0.25f;

    public static void onLootTableLoad(LootTableLoadEvent event) {
        if(!event.getName().equals(BuiltInLootTables.END_CITY_TREASURE)) return;

        EnchantRandomlyFunction.Builder enchant = EnchantRandomlyFunction.randomEnchantment();
        ModEnchantments.ENCHANTMENTS.getEntries().forEach(entry -> enchant.withEnchantment(entry.get()));

        event.getTable().addPool(LootPool.lootPool()
                .name(TotemOfRecalling.MODID + ":end_city_enchantments")
                .setRolls(ConstantValue.exactly(1))
                .when(LootItemRandomChanceCondition.randomChance(END_CITY_BOOK_CHANCE))
                .add(LootItem.lootTableItem(Items.BOOK).apply(enchant))
                .build());
    }
}
