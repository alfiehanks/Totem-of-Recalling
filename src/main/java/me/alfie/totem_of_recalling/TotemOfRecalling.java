package me.alfie.totem_of_recalling;

import me.alfie.alfinolib.networking.NetworkRegisterEvent;
import me.alfie.alfinolib.networking.Networking;
import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.item.*;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(TotemOfRecalling.MODID)
public class TotemOfRecalling {
    public static final String MODID = "totemofrecalling";
    public static final Logger LOGGER = LogUtils.getLogger();


    private static final DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, TotemOfRecalling.MODID);
    // Whether the totem can be used, it becomes inactive after use when REQUIRES_SMITHING is enabled
    public static final Supplier<DataComponentType<Boolean>> ACTIVATED = DATA_COMPONENTS.registerComponentType("activated",
            builder -> builder.persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL));

    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(TotemOfRecalling.MODID);
    public static final Supplier<Item> TOTEM_OF_RECALLING = ITEMS.registerSimpleItem("totem_of_recalling", props -> props.stacksTo(1).enchantable(1).rarity(Rarity.RARE).component(ACTIVATED.get(), true));

    public TotemOfRecalling(IEventBus modEventBus, ModContainer modContainer) {
        DATA_COMPONENTS.register(modEventBus);
        ITEMS.register(modEventBus);

        modEventBus.addListener(TotemOfRecalling::registerPackets);
        modEventBus.addListener(TotemOfRecalling::buildCreative);
        NeoForge.EVENT_BUS.addListener(TotemOfRecalling::onTotemUse);
        NeoForge.EVENT_BUS.addListener(RecallManager::onServerTick);
        NeoForge.EVENT_BUS.addListener(RecallManager::onLogout);

        modContainer.registerConfig(ModConfig.Type.SERVER, Config.CONFIG_SPEC);
    }

    public static void onTotemUse(PlayerInteractEvent.RightClickItem event) {
        ItemStack stack = event.getItemStack();

        if(stack.is(TOTEM_OF_RECALLING.get()) && !event.getEntity().getCooldowns().isOnCooldown(stack)) {
            if(event.getEntity().isShiftKeyDown() && event.getLevel().isClientSide()) {
                int expLevels = event.getEntity().experienceLevel;
                Networking.sendToServer(new RecallPacket(expLevels));
            }
        }
    }

    public static boolean isActivated(ItemStack stack) {
        return stack.getOrDefault(ACTIVATED.get(), true);
    }

    public static void buildCreative(BuildCreativeModeTabContentsEvent event) {
        if(event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(TOTEM_OF_RECALLING.get());
        }
    }

    public static void registerPackets(NetworkRegisterEvent event) {
        event.register(Networking.Side.SERVER, RecallPacket.TYPE, RecallPacket.STREAM_CODEC);
        event.register(Networking.Side.CLIENT, TotemAnimationPacket.TYPE, TotemAnimationPacket.STREAM_CODEC);
    }
}
