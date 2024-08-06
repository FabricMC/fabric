package net.fabricmc.fabric.mixin.modprotocol;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;

import net.fabricmc.fabric.impl.modprotocol.ModProtocol;
import net.fabricmc.fabric.impl.modprotocol.ServerMetadataExtension;

import net.minecraft.server.ServerMetadata;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(ServerMetadata.class)
public class ServerMetadataMixin implements ServerMetadataExtension {
	@Unique
	@Nullable
	private List<ModProtocol> modProtocol;

	@Override
	public List<ModProtocol> fabric$getModProtocol() {
		return this.modProtocol;
	}

	@Override
	public void fabric$setModProtocol(List<ModProtocol> protocol) {
		this.modProtocol = protocol;
	}

	@ModifyExpressionValue(method = "<clinit>", at = @At(value = "INVOKE", target = "Lcom/mojang/serialization/codecs/RecordCodecBuilder;create(Ljava/util/function/Function;)Lcom/mojang/serialization/Codec;"))
	private static Codec<ServerMetadata> extendCodec(Codec<ServerMetadata> original) {
		return new Codec<>() {
			@Override
			public <T> DataResult<Pair<ServerMetadata, T>> decode(DynamicOps<T> ops, T input) {
				var decoded = original.decode(ops, input);
				if (decoded.isSuccess()) {
					var protocol = ops.get(input, "fabric:mod_protocol");
					if (protocol.isSuccess()) {
						var result = ModProtocol.LIST_CODEC.decode(ops, protocol.getOrThrow());
						if (result.isSuccess()) {
							ServerMetadataExtension.of(decoded.getOrThrow().getFirst()).fabric$setModProtocol(result.getOrThrow().getFirst());
						}
					}
				}
				return decoded;
			}

			@Override
			public <T> DataResult<T> encode(ServerMetadata input, DynamicOps<T> ops, T prefix) {
				var encode = original.encode(input, ops, prefix);
				if (encode.isSuccess() && ServerMetadataExtension.of(input).fabric$getModProtocol() != null) {
					var protocol = ModProtocol.LIST_CODEC.encodeStart(ops, ServerMetadataExtension.of(input).fabric$getModProtocol());
					if (protocol.isSuccess()) {
						encode = ops.mergeToMap(encode.getOrThrow(), ops.createString("fabric:mod_protocol"), protocol.getOrThrow());
					}
				}

				return encode;
			}
		};
	}
}
