package me.alfie.totem_of_recalling;

import me.alfie.alfinolib.networking.NetworkPacket;
import me.alfie.alfinolib.networking.codec.StreamCodec;
import me.alfie.alfinolib.util.ResourceId;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record TotemAnimationPacket() implements NetworkPacket<TotemAnimationPacket> {

    public static final TotemAnimationPacket INSTANCE = new TotemAnimationPacket();

    public static final Type<@NotNull TotemAnimationPacket> TYPE = new Type<>(new ResourceId(TotemOfRecalling.MODID, "totem_animation").mc());
    @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static final StreamCodec<RegistryFriendlyByteBuf, TotemAnimationPacket> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    @Override
    public void exec(IPayloadContext context) {
        ClientHooks.playTotemAnimation();
    }
}
