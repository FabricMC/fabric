package net.fabricmc.fabric.impl.networking;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import net.minecraft.network.NetworkSide;
import net.minecraft.network.NetworkState;

import org.jetbrains.annotations.Nullable;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.RegistryByteBuf;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public class PayloadTypeRegistryImpl<B extends PacketByteBuf> implements PayloadTypeRegistry<B> {
	public static PayloadTypeRegistry<PacketByteBuf> CONFIGURATION_C2S = new PayloadTypeRegistryImpl<>(NetworkState.CONFIGURATION, NetworkSide.SERVERBOUND);
	public static PayloadTypeRegistry<PacketByteBuf> CONFIGURATION_S2C = new PayloadTypeRegistryImpl<>(NetworkState.CONFIGURATION, NetworkSide.CLIENTBOUND);
	public static PayloadTypeRegistry<RegistryByteBuf> PLAY_C2S = new PayloadTypeRegistryImpl<>(NetworkState.PLAY, NetworkSide.SERVERBOUND);
	public static PayloadTypeRegistry<RegistryByteBuf> PLAY_S2C = new PayloadTypeRegistryImpl<>(NetworkState.PLAY, NetworkSide.CLIENTBOUND);

	private final Map<Identifier, CustomPayload.Type<B, ? extends CustomPayload>> packetTypes = new HashMap<>();
	private final NetworkState state;
	private final NetworkSide side;

	private PayloadTypeRegistryImpl(NetworkState state, NetworkSide side) {
		this.state = state;
		this.side = side;
	}

	@Override
	public <T extends CustomPayload> CustomPayload.Type<? super B, T> register(CustomPayload.Id<T> id, PacketCodec<? super B, T> codec) {
		Objects.requireNonNull(id, "id");
		Objects.requireNonNull(codec, "codec");

		final CustomPayload.Type<B, T> payloadType = new CustomPayload.Type<>(id, (PacketCodec<B, T>) codec);

		if (packetTypes.containsKey(id.id())) {
			throw new IllegalArgumentException("Packet type " + id + " is already registered!");
		}

		packetTypes.put(id.id(), payloadType);
		return payloadType;
	}

	@Override
	@Nullable
	public CustomPayload.Type<B, ? extends CustomPayload> get(Identifier id) {
		return packetTypes.get(id);
	}

	@Override
	@Nullable
	public <T extends CustomPayload> CustomPayload.Type<B, T> get(CustomPayload.Id<T> id) {
		//noinspection unchecked
		return (CustomPayload.Type<B, T>) packetTypes.get(id.id());
	}

	public NetworkState getState() {
		return state;
	}

	public NetworkSide getSide() {
		return side;
	}
}
