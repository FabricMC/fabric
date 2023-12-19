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

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.attachment.v1.Attachment;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSerializer;

public final class AttachmentRegistryImpl {
	private static final Logger LOGGER = LoggerFactory.getLogger("fabric-api-data-attachment-api-v1");
	private static final Map<Identifier, Attachment<?>> attachmentRegistry = new HashMap<>();

	public static <A> void register(Identifier id, Attachment<A> attachment) {
		Attachment<?> existing = attachmentRegistry.put(id, attachment);

		if (existing != null) {
			LOGGER.warn("Encountered duplicate attachment registration for id " + id);
		}
	}

	@Nullable
	public static Attachment<?> get(Identifier id) {
		return attachmentRegistry.get(id);
	}

	public static <A> AttachmentRegistry.Builder<A> builder() {
		return new BuilderImpl<>();
	}

	public static class BuilderImpl<A> implements AttachmentRegistry.Builder<A> {
		@Nullable
		private Supplier<A> initializer = null;
		@Nullable
		private AttachmentSerializer<A> serializer = null;
		private boolean synced = false;

		@Override
		public AttachmentRegistry.Builder<A> synced(boolean synced) {
			this.synced = synced;
			return this;
		}

		@Override
		public AttachmentRegistry.Builder<A> defaultValue(A defaultVal) {
			Objects.requireNonNull(defaultVal, "default value cannot be null");

			initializer = () -> defaultVal;
			return this;
		}

		@Override
		public AttachmentRegistry.Builder<A> initializer(Supplier<A> initializer) {
			Objects.requireNonNull(initializer, "initializer cannot be null");

			this.initializer = initializer;
			return this;
		}

		@Override
		public AttachmentRegistry.Builder<A> serializer(AttachmentSerializer<A> serializer) {
			Objects.requireNonNull(serializer, "serializer cannot be null");

			this.serializer = serializer;
			return this;
		}

		@Override
		public Attachment<A> buildAndRegister(Identifier id) {
			if (initializer == null) {
				throw new IllegalArgumentException("Cannot construct an attachment without an initializer or a default value");
			}

			var attachment = new AttachmentImpl<>(id, initializer, serializer, synced);
			register(id, attachment);
			return attachment;
		}
	}
}
