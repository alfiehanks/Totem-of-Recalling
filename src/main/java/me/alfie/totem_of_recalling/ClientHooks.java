package me.alfie.totem_of_recalling;

import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;

// Client-only code, kept separate so dedicated servers never load client classes
public class ClientHooks {

    public static void playTotemAnimation() {
        Minecraft.getInstance().gameRenderer.displayItemActivation(new ItemStack(TotemOfRecalling.TOTEM_OF_RECALLING.get()));
    }
}
