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

package net.fabricmc.fabric.api.attachment.v1;

import java.util.Objects;
import java.util.function.Supplier;

import com.mojang.serialization.Codec;

import net.minecraft.util.Identifier;

import net.fabricmc.fabric.impl.attachment.AttachmentRegistryImpl;

/**
 * Class used to create and register {@link Attachment}s. To quickly create {@link Attachment}s with default values, use
 * {@link #create(Identifier, Supplier)}. Alternatively, {@link #createPersistent(Identifier, Supplier, Codec)} and
 * {@link #createSynced(Identifier, Supplier, Codec)} can be used for attachments that persist across server restarts
 * or that are synced between server and client, respectively.
 *
 * <p>For more control over the attachment and its properties, use {@link AttachmentRegistry#builder()} to
 * get a {@link Builder} instance.</p>
 */
public final class AttachmentRegistry {
	private AttachmentRegistry() {
	}

	/**
	 * Creates <i>and registers</i> an attachment, initialized using a default value. The data will be neither persisted
	 * nor synced
	 *
	 * @param id the identifier of this attachment
	 * @param defaultValue the default value, used for objects which don't have attached data yet. Must not be {@code null}
	 * @param <A> the type of attached data
	 * @return the registered {@link Attachment} instance
	 */
	public static <A> Attachment<A> create(Identifier id, Supplier<A> defaultValue) {
		Objects.requireNonNull(id, "identifier cannot be null");
		Objects.requireNonNull(defaultValue, "default value cannot be null");

		return AttachmentRegistry.<A>builder().initializer(defaultValue).buildAndRegister(id);
	}

	/**
	 * Creates <i>and registers</i> an attachment, initialized using a default value, that will persist across server restarts.
	 *
	 * @param id the identifier of this attachment
	 * @param defaultValue the default value, used for objects which don't have attached data yet. Must not be {@code null}
	 * @param codec the codec used for serialization
	 * @param <A> the type of attached data
	 * @return the registered {@link Attachment} instance
	 */
	public static <A> Attachment<A> createPersistent(Identifier id, Supplier<A> defaultValue, Codec<A> codec) {
		Objects.requireNonNull(id, "identifier cannot be null");
		Objects.requireNonNull(defaultValue, "default value cannot be null");
		Objects.requireNonNull(codec, "codec cannot be null");

		return AttachmentRegistry.<A>builder()
				.initializer(defaultValue)
				.persistent(true)
				.codec(codec)
				.buildAndRegister(id);
	}

	/**
	 * Creates <i>and registers</i> an attachment, initialized using a default value, that will be synced between server and client.
	 *
	 * @param id the identifier of this attachment
	 * @param defaultValue the default value, used for objects which don't have attached data yet. Must not be {@code null}
	 * @param codec the codec used for serialization
	 * @param <A> the type of attached data
	 * @return the registered {@link Attachment} instance
	 */
	public static <A> Attachment<A> createSynced(Identifier id, Supplier<A> defaultValue, Codec<A> codec) {
		Objects.requireNonNull(id, "identifier cannot be null");
		Objects.requireNonNull(defaultValue, "default value cannot be null");
		Objects.requireNonNull(codec, "codec cannot be null");

		return AttachmentRegistry.<A>builder()
				.initializer(defaultValue)
				.synced(true)
				.codec(codec)
				.buildAndRegister(id);
	}

	/**
	 * Creates a {@link Builder}, that gives finer control over the attachment's properties.
	 *
	 * @param <A> the type of the attached data
	 * @return a {@link Builder} instance
	 */
	public static <A> Builder<A> builder() {
		return AttachmentRegistryImpl.builder();
	}

	/**
	 * A builder for creating {@link Attachment}s with finer control over their properties.
	 *
	 * @param <A> the type of the attached data
	 */
	public interface Builder<A> {
		/**
		 * Sets whether the attached data should persist across server restarts.
		 *
		 * @param persistent whether the attached data should persist across server restarts
		 * @return the builder
		 */
		Builder<A> persistent(boolean persistent);

		/**
		 * Sets whether the attachment should be synced between server and client or not.
		 *
		 * @param synced whether the attachment data should be synced
		 * @return the builder
		 */
		Builder<A> synced(boolean synced);

		/**
		 * Uses the given {@link Supplier} to get a starting value for objects which don't have attached data yet.
		 *
		 * @param supplier the initializer
		 * @return the builder
		 */
		Builder<A> initializer(Supplier<A> supplier);

		/**
		 * Sets the {@link Codec} for this attachment.
		 *
		 * @param codec the codec
		 * @return the builder
		 */
		Builder<A> codec(Codec<A> codec);

		/**
		 * Builds and registers the {@link Attachment}.
		 *
		 * @param id the attachment's identifier
		 * @return the built and registered {@link Attachment}
		 */
		Attachment<A> buildAndRegister(Identifier id);
	}
}
