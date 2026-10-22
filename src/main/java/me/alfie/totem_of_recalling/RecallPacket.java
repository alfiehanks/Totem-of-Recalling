package me.alfie.totem_of_recalling;

import me.alfie.alfinolib.networking.NetworkPacket;
import me.alfie.alfinolib.networking.Networking;
import me.alfie.alfinolib.networking.codec.CommonCodecs;
import me.alfie.alfinolib.networking.codec.StreamCodec;
import me.alfie.alfinolib.networking.codec.StreamCodecBuilder;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

public record RecallPacket(int expLevels) implements NetworkPacket<RecallPacket> {

    public static final StreamCodec<FriendlyByteBuf, RecallPacket> STREAM_CODEC = StreamCodecBuilder.<FriendlyByteBuf, RecallPacket>create()
            .add(CommonCodecs.INT, RecallPacket::expLevels).build(RecallPacket::new);

    @Override
    public void exec(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if(player == null) return;
            InteractionHand hand = player.getMainHandItem().is(TotemOfRecalling.TOTEM_OF_RECALLING.get()) ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
            ItemStack totem = player.getItemInHand(hand);
            if(!totem.is(TotemOfRecalling.TOTEM_OF_RECALLING.get()) || player.getCooldowns().isOnCooldown(totem.getItem()) || RecallManager.isRecalling(player)) return;

            if(player.experienceLevel >= RecallManager.getExpCost(player, totem) || player.getAbilities().instabuild) {
                RecallManager.start(player, hand);
            }
        });
        context.setPacketHandled(true);
    }

    public static void playEffects(ServerPlayer player) {
        Networking.sendToClient(player, TotemAnimationPacket.INSTANCE);

        ServerLevel level = (ServerLevel) player.level();

        int cloudParticles = 50;
        level.sendParticles(
                new DustParticleOptions(Vec3.fromRGB24(0xA020F0).toVector3f(), 4.0f),

                player.getX(), player.getY(0.5), player.getZ(),
                cloudParticles,
                0.8, 1.0, 0.8,
                0.02);

        level.playSound(null, player.blockPosition(), SoundEvents.ENDERMAN_TELEPORT,
                SoundSource.MASTER, 0.5f, 1.0f);

        level.playSound(null, player.blockPosition(), SoundEvents.TOTEM_USE,
                SoundSource.MASTER, 0.2f, 1.0f);
    }
}
