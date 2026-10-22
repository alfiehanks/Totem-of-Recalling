package me.alfie.totem_of_recalling;

import me.alfie.alfinolib.networking.NetworkRegisterEvent;
import me.alfie.alfinolib.networking.Networking;
import net.minecraft.world.item.*;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(TotemOfRecalling.MODID)
public class TotemOfRecalling {
    public static final String MODID = "totemofrecalling";
    public static final Logger LOGGER = LogUtils.getLogger();


    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, TotemOfRecalling.MODID);
    public static final RegistryObject<Item> TOTEM_OF_RECALLING = ITEMS.register("totem_of_recalling", () -> new TotemOfRecallingItem(new Item.Properties().stacksTo(1).rarity(Rarity.RARE)));

    public TotemOfRecalling() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ITEMS.register(modEventBus);
        ModEnchantments.ENCHANTMENTS.register(modEventBus);

        modEventBus.addListener(TotemOfRecalling::registerPackets);
        modEventBus.addListener(TotemOfRecalling::buildCreative);
        MinecraftForge.EVENT_BUS.addListener(TotemOfRecalling::onTotemUse);
        MinecraftForge.EVENT_BUS.addListener(RecallManager::onServerTick);
        MinecraftForge.EVENT_BUS.addListener(RecallManager::onLogout);
        MinecraftForge.EVENT_BUS.addListener(ModLoot::onLootTableLoad);

        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Config.CONFIG_SPEC);
    }

    public static void onTotemUse(PlayerInteractEvent.RightClickItem event) {
        ItemStack stack = event.getItemStack();

        if(stack.is(TOTEM_OF_RECALLING.get()) && !event.getEntity().getCooldowns().isOnCooldown(stack.getItem())) {
            if(event.getEntity().isShiftKeyDown() && event.getLevel().isClientSide()) {
                int expLevels = event.getEntity().experienceLevel;
                Networking.sendToServer(new RecallPacket(expLevels));
            }
        }
    }

    public static void buildCreative(BuildCreativeModeTabContentsEvent event) {
        if(event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(TOTEM_OF_RECALLING.get());
        }
    }

    public static void registerPackets(NetworkRegisterEvent event) {
        event.register(RecallPacket.class, RecallPacket.STREAM_CODEC);
        event.register(TotemAnimationPacket.class, TotemAnimationPacket.STREAM_CODEC);
    }
}
