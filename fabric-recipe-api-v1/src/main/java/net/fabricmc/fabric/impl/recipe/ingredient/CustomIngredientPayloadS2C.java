package net.fabricmc.fabric.impl.recipe.ingredient;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record CustomIngredientPayloadS2C(int protocolVersion) implements CustomPayload {
	public static final PacketCodec<PacketByteBuf, CustomIngredientPayloadS2C> PACKET_CODEC = PacketCodec.tuple(
			PacketCodecs.VAR_INT, CustomIngredientPayloadS2C::protocolVersion,
			CustomIngredientPayloadS2C::new
	);
	public static final CustomPayload.Id<CustomIngredientPayloadS2C> PACKET_ID = new Id<>(CustomIngredientSync.PACKET_ID);

	@Override
	public Id<? extends CustomPayload> getId() {
		return PACKET_ID;
	}
}
