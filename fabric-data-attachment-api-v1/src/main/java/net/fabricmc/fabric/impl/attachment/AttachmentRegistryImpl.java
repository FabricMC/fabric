/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.fabricmc.fabric.impl.attachment;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

import com.mojang.serialization.Codec;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.RuntimeOps;

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;

public final class AttachmentRegistryImpl {
	private static final Logger LOGGER = LoggerFactory.getLogger("fabric-data-attachment-api-v1");
	private static final Map<Identifier, AttachmentType<?>> attachmentRegistry = new HashMap<>();

	public static <A> void register(Identifier id, AttachmentType<A> attachmentType) {
		AttachmentType<?> existing = attachmentRegistry.put(id, attachmentType);

		if (existing != null) {
			LOGGER.warn("Encountered duplicate type registration for id " + id);
		}
	}

	private static <T> AttachmentType.EntityCopyHandler<T> copyHandlerFromCodec(Codec<T> codec) {
		return (original, oldEntity, newEntity) -> codec.encodeStart(RuntimeOps.INSTANCE, original)
				.flatMap(s -> codec.decode(RuntimeOps.INSTANCE, s))
				.result()
				.orElseThrow()
				.getFirst();
	}

	@Nullable
	public static AttachmentType<?> get(Identifier id) {
		return attachmentRegistry.get(id);
	}

	public static <A> AttachmentRegistry.Builder<A> builder() {
		return new BuilderImpl<>();
	}

	public static class BuilderImpl<A> implements AttachmentRegistry.Builder<A> {
		@Nullable
		private Supplier<A> defaultInitializer = null;
		@Nullable
		private Codec<A> codec = null;
		@Nullable
		private AttachmentType.EntityCopyHandler<A> copyHandler = null;
		private boolean persistent = false;
		private boolean copyOnDeath = false;

		@Override
		public AttachmentRegistry.Builder<A> persistent() {
			this.persistent = true;
			return this;
		}

		@Override
		public AttachmentRegistry.Builder<A> copyOnDeath() {
			this.copyOnDeath = true;
			return this;
		}

		@Override
		public AttachmentRegistry.Builder<A> entityCopyHandler(AttachmentType.EntityCopyHandler<A> copyHandler) {
			Objects.requireNonNull(copyHandler, "entity copy handler cannot be null");

			this.copyHandler = copyHandler;
			return this;
		}

		@Override
		public AttachmentRegistry.Builder<A> codec(Codec<A> codec) {
			Objects.requireNonNull(codec, "codec cannot be null");

			if (this.codec != null) {
				throw new IllegalArgumentException(
						"A codec was already set for this attachment type. Declare it once using the Builder#codec() method instead"
				);
			}

			this.codec = codec;
			return this;
		}

		@Override
		public AttachmentRegistry.Builder<A> initializer(Supplier<A> initializer) {
			Objects.requireNonNull(initializer, "initializer cannot be null");

			this.defaultInitializer = initializer;
			return this;
		}

		@Override
		public AttachmentType<A> buildAndRegister(Identifier id) {
			if (codec == null && persistent) {
				throw new IllegalArgumentException("Persistence was enabled, but no codec was provided");
			}

			if (codec != null && copyHandler == null) {
				this.copyHandler = copyHandlerFromCodec(codec);
			}

			if (copyOnDeath && copyHandler == null) {
				throw new IllegalArgumentException("Copy on death was enabled, but no way of copying attachments was provided");
			}

			var attachment = new AttachmentTypeImpl<>(id, defaultInitializer, codec, copyHandler, persistent, copyOnDeath);
			register(id, attachment);
			return attachment;
		}
	}
}
