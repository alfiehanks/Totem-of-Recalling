package me.alfie.totem_of_recalling;

import me.alfie.alfinolib.networking.NetworkPacket;
import me.alfie.alfinolib.networking.Networking;
import me.alfie.alfinolib.networking.codec.CommonCodecs;
import me.alfie.alfinolib.networking.codec.StreamCodec;
import me.alfie.alfinolib.networking.codec.StreamCodecBuilder;
import me.alfie.alfinolib.util.ResourceId;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.TeleportTransition;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;
import org.jline.utils.Colors;

import java.awt.*;

public record RecallPacket(int expLevels) implements NetworkPacket<RecallPacket> {

    public static final Type<@NotNull RecallPacket> TYPE = new Type<>(new ResourceId(TotemOfRecalling.MODID, "recall").mc());
    @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static final StreamCodec<RegistryFriendlyByteBuf, RecallPacket> STREAM_CODEC = StreamCodecBuilder.<RegistryFriendlyByteBuf, RecallPacket>create()
            .add(CommonCodecs.INT, RecallPacket::expLevels).build(RecallPacket::new);

    @Override
    public void exec(IPayloadContext context) {
        context.enqueueWork(() -> {
            ServerPlayer player = (ServerPlayer) context.player();
            InteractionHand hand = player.getMainHandItem().is(TotemOfRecalling.TOTEM_OF_RECALLING.get()) ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
            ItemStack totem = player.getItemInHand(hand);
            if(!totem.is(TotemOfRecalling.TOTEM_OF_RECALLING.get()) || player.getCooldowns().isOnCooldown(totem) || RecallManager.isRecalling(player)) return;

            if(!TotemOfRecalling.isActivated(totem)) {
                player.sendOverlayMessage(Component.translatable("totemofrecalling.message.totem_inactive").withStyle(ChatFormatting.RED));
                return;
            }

            if(player.experienceLevel >= RecallManager.getExpCost(player, totem) || player.hasInfiniteMaterials()) {
                RecallManager.start(player, hand);
            }
        });
    }

    public static void playEffects(ServerPlayer player) {
        Networking.sendToClient(player, TotemAnimationPacket.INSTANCE);

        ServerLevel level = player.level();

        int cloudParticles = 50;
        level.sendParticles(
                new DustParticleOptions(0xA020F0, 4.0f),

                player.getX(), player.getY(0.5), player.getZ(),
                cloudParticles,
                0.8, 1.0, 0.8,
                0.02);

        level.playSound(null, player.blockPosition(), SoundEvents.PLAYER_TELEPORT,
                SoundSource.MASTER, 0.5f, 1.0f);

        level.playSound(null, player.blockPosition(), SoundEvents.TOTEM_USE,
                SoundSource.MASTER, 0.2f, 1.0f);
    }
}
