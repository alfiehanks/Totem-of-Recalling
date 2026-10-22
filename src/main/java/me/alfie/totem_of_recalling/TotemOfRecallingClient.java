package me.alfie.totem_of_recalling;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

// Client-only entrypoint, not loaded on dedicated servers
@Mod(value = TotemOfRecalling.MODID, dist = Dist.CLIENT)
public class TotemOfRecallingClient {

    public TotemOfRecallingClient(ModContainer container) {
        // Adds the "Config" button for this mod in the Mods screen
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }
}
