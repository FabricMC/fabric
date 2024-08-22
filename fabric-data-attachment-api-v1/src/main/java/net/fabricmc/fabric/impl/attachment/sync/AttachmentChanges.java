package net.fabricmc.fabric.impl.attachment.sync;

import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.Maps;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.World;

import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.impl.attachment.AttachmentEntrypoint;
import net.fabricmc.fabric.impl.attachment.AttachmentRegistryImpl;

public class AttachmentChanges {
	@SuppressWarnings("unchecked")
	private static final PacketCodec<PacketByteBuf, Map<Identifier, Object>> DATA_CODEC = PacketCodec.ofStatic(
			(buf, map) -> {
				buf.writeVarInt(map.size());

				for (Map.Entry<Identifier, Object> entry : map.entrySet()) {
					Identifier id = entry.getKey();
					Object o = entry.getValue();
					AttachmentType<?> type = AttachmentRegistryImpl.get(id);

					if (type == null) {
						AttachmentEntrypoint.LOGGER.warn("Unknown attachment type '{}', skipping", id.toString());
						continue;
					} else if (!AttachmentSync.CLIENT_SUPPORTED_ATTACHMENTS.get().contains(id)) {
						AttachmentEntrypoint.LOGGER.warn("Attachment type '{}' does not exist on client, skipping", id.toString());
						continue;
					}

					PacketCodec<PacketByteBuf, Object> packetCodec = (PacketCodec<PacketByteBuf, Object>) type.packetCodec();

					if (packetCodec == null) {
						AttachmentEntrypoint.LOGGER.warn("Attachment type '{}' has no packet codec, skipping", id.toString());
						continue;
					}

					buf.writeIdentifier(id);
					packetCodec.encode(buf, o);
				}
			},
			buf -> {
				Map<Identifier, Object> map = new HashMap<>();
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

					map.put(id, packetCodec.decode(buf));
				}

				return map;
			}
	);
	public static final PacketCodec<PacketByteBuf, AttachmentChanges> PACKET_CODEC = PacketCodecs
			// generic nightmare
			.<PacketByteBuf, TargetInfo, Map<Identifier, Object>, Map<TargetInfo, Map<Identifier, Object>>>map(
					HashMap::new,
					TargetInfo.PACKET_CODEC,
					DATA_CODEC
			).xmap(AttachmentChanges::new, AttachmentChanges::getChanges);

	private Map<TargetInfo, Map<Identifier, Object>> changes;

	public AttachmentChanges() {
		this(new HashMap<>());
	}

	public AttachmentChanges(Map<TargetInfo, Map<Identifier, Object>> changes) {
		this.changes = changes;
	}

	public Map<TargetInfo, Map<Identifier, Object>> getChanges() {
		return changes;
	}

	public enum TargetType {
		ENTITY(PacketCodecs.VAR_INT.xmap(EntityTarget::new, EntityTarget::networkId)),
		BLOCK_ENTITY(GlobalPos.PACKET_CODEC.xmap(BlockEntityTarget::new, BlockEntityTarget::pos)),
		CHUNK(PacketCodec.tuple(
				RegistryKey.createPacketCodec(RegistryKeys.WORLD), ChunkTarget::dimension,
				PacketCodecs.VAR_LONG.xmap(ChunkPos::new, ChunkPos::toLong), ChunkTarget::pos,
				ChunkTarget::new
		));

		public static final PacketCodec<ByteBuf, TargetType> PACKET_CODEC = PacketCodecs.indexed(
				i -> TargetType.values()[i],
				TargetType::ordinal
		);
		private final PacketCodec<ByteBuf, ? extends TargetInfo> codec;

		TargetType(PacketCodec<ByteBuf, ? extends TargetInfo> codec) {
			this.codec = codec;
		}

		public PacketCodec<ByteBuf, ? extends TargetInfo> codec() {
			return codec;
		}
	}

	public sealed interface TargetInfo permits BlockEntityTarget, ChunkTarget, EntityTarget {
		PacketCodec<ByteBuf, TargetInfo> PACKET_CODEC = TargetType.PACKET_CODEC.dispatch(
				TargetInfo::type,
				TargetType::codec
		);

		TargetType type();
	}

	record EntityTarget(int networkId) implements TargetInfo {
		@Override
		public TargetType type() {
			return TargetType.ENTITY;
		}
	}

	record BlockEntityTarget(GlobalPos pos) implements TargetInfo {
		@Override
		public TargetType type() {
			return TargetType.BLOCK_ENTITY;
		}
	}

	record ChunkTarget(RegistryKey<World> dimension, ChunkPos pos) implements TargetInfo {
		@Override
		public TargetType type() {
			return TargetType.CHUNK;
		}
	}
}
