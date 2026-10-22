package me.alfie.totem_of_recalling;

import me.alfie.alfinolib.networking.NetworkPacket;
import me.alfie.alfinolib.networking.codec.StreamCodec;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public record TotemAnimationPacket() implements NetworkPacket<TotemAnimationPacket> {

    public static final TotemAnimationPacket INSTANCE = new TotemAnimationPacket();

    public static final StreamCodec<FriendlyByteBuf, TotemAnimationPacket> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    @Override
    public void exec(NetworkEvent.Context context) {
        context.enqueueWork(ClientHooks::playTotemAnimation);
        context.setPacketHandled(true);
    }
}
