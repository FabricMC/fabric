package net.fabricmc.fabric.mixin.networking;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.UnknownCustomPayload;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;

@Mixin(UnknownCustomPayload.class)
public class UnknownCustomPayloadMixin {

	@ModifyReturnValue(method = "createCodec", at = @At("RETURN"))
	private static <T extends PacketByteBuf> PacketCodec<T, CustomPayload> createCodec(@Coerce PacketCodec<T, CustomPayload> codec) {
		return new PacketCodec<>() {
			@Override
			public CustomPayload decode(T buf) {
				return codec.decode(buf);
			}

			@Override
			public void encode(T buf, CustomPayload value) {
				throw new RuntimeException("Failed to find encoder for custom payload. Are you sure you registered one using PayloadTypeRegistry for both the client and server?");
			}
		};
	}

}
