package me.alfie.totem_of_recalling;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

// Client-only entrypoint, not loaded on dedicated servers
@Mod(value = TotemOfRecalling.MODID, dist = Dist.CLIENT)
public class TotemOfRecallingClient {

    public TotemOfRecallingClient(ModContainer container) {
        // Adds the "Config" button for this mod in the Mods screen
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);

        NeoForge.EVENT_BUS.addListener(TotemOfRecallingClient::onTooltip);
    }

    public static void onTooltip(ItemTooltipEvent event) {
        if(!event.getItemStack().is(TotemOfRecalling.TOTEM_OF_RECALLING.get())) return;

        String key = TotemOfRecalling.isActivated(event.getItemStack()) ? "activated" : "inactivated";
        // Index 1 places it directly under the item name
        event.getToolTip().add(Math.min(1, event.getToolTip().size()),
                Component.translatable("totemofrecalling.tooltip." + key).withStyle(ChatFormatting.GRAY));
    }
}
