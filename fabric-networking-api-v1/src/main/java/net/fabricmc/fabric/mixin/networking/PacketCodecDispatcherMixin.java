package net.fabricmc.fabric.mixin.networking;

import com.llamalad7.mixinextras.sugar.Local;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.EncoderException;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.handler.PacketCodecDispatcher;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.c2s.common.CustomPayloadC2SPacket;
import net.minecraft.network.packet.s2c.common.CustomPayloadS2CPacket;

@Mixin(PacketCodecDispatcher.class)
public abstract class PacketCodecDispatcherMixin<B extends ByteBuf, V, T> implements PacketCodec<B, V> {
	// Add the custom payload id to the error message
	@Inject(method = "encode(Lio/netty/buffer/ByteBuf;Ljava/lang/Object;)V", at = @At(value = "NEW", target = "(Ljava/lang/String;Ljava/lang/Throwable;)Lio/netty/handler/codec/EncoderException;"))
	public void encode(B byteBuf, V packet, CallbackInfo ci, @Local(ordinal = 1) T packetId, @Local Exception e) {
		CustomPayload payload = null;

		if (packet instanceof CustomPayloadC2SPacket customPayloadC2SPacket) {
			payload = customPayloadC2SPacket.payload();
		} else if (packet instanceof CustomPayloadS2CPacket customPayloadS2CPacket) {
			payload = customPayloadS2CPacket.payload();
		}

		if (payload != null && payload.getId() != null) {
			throw new EncoderException("Failed to encode packet '%s' (%s)".formatted(packetId, payload.getId().id().toString()), e);
		}
	}
}
