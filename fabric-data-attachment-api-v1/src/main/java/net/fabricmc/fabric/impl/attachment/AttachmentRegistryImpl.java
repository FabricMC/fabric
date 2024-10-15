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

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;

public final class AttachmentRegistryImpl {
	private static final Logger LOGGER = LoggerFactory.getLogger("fabric-data-attachment-api-v1");
	private static final Map<Identifier, AttachmentType<?>> attachmentRegistry = new HashMap<>();

	public static <A> void register(Identifier id, AttachmentType<A> attachmentType) {
		AttachmentType<?> existing = attachmentRegistry.put(id, attachmentType);

		if (existing != null) {
			LOGGER.warn("Encountered duplicate type registration for id {}", id);
		}
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
		private Codec<A> persistenceCodec = null;
		private boolean copyOnDeath = false;

		@Override
		public AttachmentRegistry.Builder<A> persistent(Codec<A> codec) {
			Objects.requireNonNull(codec, "codec cannot be null");

			this.persistenceCodec = codec;
			return this;
		}

		@Override
		public AttachmentRegistry.Builder<A> copyOnDeath() {
			this.copyOnDeath = true;
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
			Objects.requireNonNull(id, "identifier cannot be null");

			var attachment = new AttachmentTypeImpl<>(id, defaultInitializer, persistenceCodec, copyOnDeath);
			register(id, attachment);
			return attachment;
		}
	}
}
