package net.fabricmc.fabric.impl.attachment.sync;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.impl.attachment.AttachmentEntrypoint;
import net.fabricmc.fabric.impl.attachment.AttachmentRegistryImpl;

public class AttachmentChanges {
	public static final PacketCodec<PacketByteBuf, AttachmentChanges> PACKET_CODEC = PacketCodec.ofStatic(
			AttachmentChanges::writeToNetwork,
			AttachmentChanges::readFromNetwork
	);
	// Null values signify removal
	private final Map<AttachmentType<?>, @Nullable Object> map;

	public AttachmentChanges() {
		this(new HashMap<>());
	}

	public AttachmentChanges(Map<AttachmentType<?>, Object> data) {
		this.map = data;
	}

	@SuppressWarnings("unchecked")
	private static @NotNull AttachmentChanges readFromNetwork(PacketByteBuf buf) {
		Map<AttachmentType<?>, Object> map = new HashMap<>();
		int size = buf.readVarInt();

		for (int i = 0; i < size; i++) {
			Identifier id = buf.readIdentifier();
			AttachmentType<?> type = AttachmentRegistryImpl.get(id);

			if (type == null) {
				// Can't skip as we don't know the size of the data
				throw new DecoderException("Unknown attachment type '" + id.toString() + "'");
			}

			PacketCodec<PacketByteBuf, Object> packetCodec = (PacketCodec<PacketByteBuf, Object>) type.packetCodec();

			if (packetCodec == null) {
				throw new EncoderException("Attachment type '" + id.toString() + "' has no packet codec, skipping");
			}

			map.put(type, buf.readOptional(packetCodec).orElse(null));
		}

		return new AttachmentChanges(map);
	}

	@SuppressWarnings("unchecked")
	private static void writeToNetwork(PacketByteBuf buf, AttachmentChanges data) {
		Map<AttachmentType<?>, @Nullable Object> map = data.map;
		buf.writeVarInt(map.size());

		for (Map.Entry<AttachmentType<?>, @Nullable Object> entry : map.entrySet()) {
			AttachmentType<?> type = entry.getKey();
			Identifier id = type.identifier();
			@Nullable
			Object o = entry.getValue();

			if (!AttachmentSync.CLIENT_SUPPORTED_ATTACHMENTS.get().contains(id)) {
				AttachmentEntrypoint.LOGGER.warn(
						"Attachment type '{}' does not exist on client, skipping",
						id.toString()
				);
				continue;
			}

			PacketCodec<PacketByteBuf, Object> packetCodec = (PacketCodec<PacketByteBuf, Object>) type.packetCodec();

			if (packetCodec == null) {
				AttachmentEntrypoint.LOGGER.warn("Attachment type '{}' has no packet codec, skipping", id.toString());
				continue;
			}

			buf.writeIdentifier(id);
			buf.writeOptional(Optional.ofNullable(o), packetCodec);
		}
	}
}
