package net.fabricmc.fabric.api.networking.v1;

import net.fabricmc.fabric.impl.networking.PayloadTypeRegistryImpl;

import org.jetbrains.annotations.Nullable;

import net.minecraft.network.NetworkSide;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.RegistryByteBuf;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public interface PayloadTypeRegistry<B extends PacketByteBuf> {
	<T extends CustomPayload> CustomPayload.Type<? super B, T> register(CustomPayload.Id<T> id, PacketCodec<? super B, T> codec);

	@Nullable
	CustomPayload.Type<B, ? extends CustomPayload> get(Identifier id);

	@Nullable
	<T extends CustomPayload> CustomPayload.Type<B, T> get(CustomPayload.Id<T> id);

	static PayloadTypeRegistry<PacketByteBuf> configuration(NetworkSide side) {
		return switch (side) {
			case SERVERBOUND -> PayloadTypeRegistryImpl.CONFIGURATION_C2S;
			case CLIENTBOUND -> PayloadTypeRegistryImpl.CONFIGURATION_S2C;
		};
	}

	static PayloadTypeRegistry<RegistryByteBuf> play(NetworkSide side) {
		return switch (side) {
			case SERVERBOUND -> PayloadTypeRegistryImpl.PLAY_C2S;
			case CLIENTBOUND -> PayloadTypeRegistryImpl.PLAY_S2C;
		};
	}
}
