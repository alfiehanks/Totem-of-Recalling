package me.alfie.totem_of_recalling;

import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetExperiencePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class RecallManager {

    private static final Map<UUID, PendingRecall> PENDING = new HashMap<>();

    // How far (in blocks) a player can drift before it counts as moving
    private static final double MOVE_TOLERANCE = 0.1;

    private static class PendingRecall {
        final InteractionHand hand;
        final int totalTicks;
        final Vec3 startPos;
        int ticksLeft;

        PendingRecall(InteractionHand hand, int totalTicks, Vec3 startPos) {
            this.hand = hand;
            this.totalTicks = totalTicks;
            this.startPos = startPos;
            this.ticksLeft = totalTicks;
        }
    }

    public static boolean isRecalling(ServerPlayer player) {
        return PENDING.containsKey(player.getUUID());
    }

    public static void start(ServerPlayer player, InteractionHand hand) {
        player.swing(hand, true);

        player.level().playSound(null, player.blockPosition(), SoundEvents.ENDERMAN_AMBIENT,
                SoundSource.MASTER, 0.5f, 1.5f);

        int portalParticles = 100;
        player.level().sendParticles(
                ParticleTypes.PORTAL,
                player.getX(), player.getY(0.5), player.getZ(),
                portalParticles,
                0.8, 1.0, 0.8,
                0.02);

        int blinking = ModEnchantments.getLevel(player, ModEnchantments.BLINKING, player.getItemInHand(hand));
        int waitTicks = Math.round(Config.CONFIG.WAIT_SECONDS.get() * 20 * reduction(blinking));
        if(waitTicks <= 0) {
            recall(player, hand);
            return;
        }

        PENDING.put(player.getUUID(), new PendingRecall(hand, waitTicks, player.position()));
        sendXpBar(player, 1.0f);
    }

    public static void onServerTick(ServerTickEvent.Post event) {
        Iterator<Map.Entry<UUID, PendingRecall>> it = PENDING.entrySet().iterator();
        while(it.hasNext()) {
            Map.Entry<UUID, PendingRecall> entry = it.next();
            PendingRecall pending = entry.getValue();
            ServerPlayer player = event.getServer().getPlayerList().getPlayer(entry.getKey());

            if(player == null || !player.isAlive()) {
                it.remove();
                if(player != null) resyncXpBar(player);
                continue;
            }

            if(!player.getItemInHand(pending.hand).is(TotemOfRecalling.TOTEM_OF_RECALLING.get())) {
                it.remove();
                resyncXpBar(player);
                player.sendOverlayMessage(Component.translatable("totemofrecalling.message.recall_cancelled_unheld").withStyle(ChatFormatting.RED));

                player.level().playSound(null, player.blockPosition(), SoundEvents.ENDERMAN_HURT,
                        SoundSource.MASTER, 0.5f, 1.5f);

                continue;
            }

            boolean enderstep = ModEnchantments.getLevel(player, ModEnchantments.ENDERSTEP, player.getItemInHand(pending.hand)) > 0;
            if(Config.CONFIG.REQUIRE_STANDING_STILL.get() && !enderstep && player.distanceToSqr(pending.startPos) > MOVE_TOLERANCE * MOVE_TOLERANCE) {
                it.remove();
                resyncXpBar(player);
                player.sendOverlayMessage(Component.translatable("totemofrecalling.message.recall_cancelled_moved").withStyle(ChatFormatting.RED));

                player.level().playSound(null, player.blockPosition(), SoundEvents.ENDERMAN_HURT,
                        SoundSource.MASTER, 0.5f, 1.5f);

                continue;
            }

            pending.ticksLeft--;
            if(pending.ticksLeft <= 0) {
                it.remove();
                recall(player, pending.hand);
            } else {
                sendXpBar(player, (float) pending.ticksLeft / pending.totalTicks);
            }
        }
    }

    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        PENDING.remove(event.getEntity().getUUID());
    }

    private static void recall(ServerPlayer player, InteractionHand hand) {
        // Re-check at the end of the wait, the player may have swapped items or spent levels
        ItemStack totem = player.getItemInHand(hand);
        int cost = getExpCost(player, totem);
        if(!totem.is(TotemOfRecalling.TOTEM_OF_RECALLING.get()) || !TotemOfRecalling.isActivated(totem)
                || (player.experienceLevel < cost && !player.hasInfiniteMaterials())) {
            resyncXpBar(player);
            return;
        }

        TeleportTransition transition = player.findRespawnPositionAndUseSpawnBlock(false, TeleportTransition.DO_NOTHING);
        player.teleport(transition);

        RecallPacket.playEffects(player);

        if(!player.hasInfiniteMaterials()) {
            int riftRecovery = ModEnchantments.getLevel(player, ModEnchantments.RIFT_RECOVERY, totem);
            player.getCooldowns().addCooldown(totem, Math.round(Config.CONFIG.COOLDOWN_SECONDS.get() * 20 * reduction(riftRecovery)));
            player.giveExperienceLevels(-cost);

            if(Config.CONFIG.REQUIRES_SMITHING.get()) {
                totem.set(TotemOfRecalling.ACTIVATED.get(), false);
            }
        }

        resyncXpBar(player);
    }

    public static int getExpCost(ServerPlayer player, ItemStack totem) {
        int cost = Config.CONFIG.EXP_LEVELS_COST.get();
        // Draconic Efficiency halves the cost, rounded up so it's never free
        if(ModEnchantments.getLevel(player, ModEnchantments.DRACONIC_EFFICIENCY, totem) > 0) {
            cost = (cost + 1) / 2;
        }
        return cost;
    }

    // 10% less per enchantment level, capped at 30%
    private static float reduction(int level) {
        return 1.0f - 0.1f * Math.min(level, 3);
    }

    // Fakes the client's XP bar progress without touching the player's real experience
    private static void sendXpBar(ServerPlayer player, float progress) {
        player.connection.send(new ClientboundSetExperiencePacket(progress, player.totalExperience, player.experienceLevel));
    }

    private static void resyncXpBar(ServerPlayer player) {
        sendXpBar(player, player.experienceProgress);
    }
}
