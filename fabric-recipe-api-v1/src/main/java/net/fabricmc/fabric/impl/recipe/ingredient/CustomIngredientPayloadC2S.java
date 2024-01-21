package net.fabricmc.fabric.impl.recipe.ingredient;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record CustomIngredientPayloadC2S(int protocolVersion, Set<Identifier> registeredSerializers) implements CustomPayload {
	public static final PacketCodec<PacketByteBuf, CustomIngredientPayloadC2S> PACKET_CODEC = PacketCodec.tuple(
			PacketCodecs.VAR_INT, CustomIngredientPayloadC2S::protocolVersion,
			PacketCodecs.collection(HashSet::new, Identifier.PACKET_CODEC), CustomIngredientPayloadC2S::registeredSerializers,
			CustomIngredientPayloadC2S::new
	);
	public static final CustomPayload.Id<CustomIngredientPayloadC2S> PACKET_ID = new Id<>(CustomIngredientSync.PACKET_ID);

	@Override
	public Id<? extends CustomPayload> getId() {
		return PACKET_ID;
	}
}
