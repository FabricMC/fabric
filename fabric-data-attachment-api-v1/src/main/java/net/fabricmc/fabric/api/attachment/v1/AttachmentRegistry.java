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
import org.jetbrains.annotations.ApiStatus;

import net.minecraft.util.Identifier;

import net.fabricmc.fabric.impl.attachment.AttachmentRegistryImpl;

/**
 * Class used to create and register {@link AttachmentType}s. To quickly create {@link AttachmentType}s, use one of the various
 * {@code createXXX} methods:
 * <ul>
 *     <li>{@link #create(Identifier)}: attachments will be neither persistent, synced or auto-initialized.</li>
 *     <li>{@link #createDefaulted(Identifier, Supplier)}: attachments will be auto-initialized, but neither persistent nor synced.</li>
 *     <li>{@link #createPersistent(Identifier, Codec)}: attachments will be persistent, but neither auto-initialized nor synced.</li>
 *     <li>{@link #createSynced(Identifier, Codec)}: attachments will be synced, but neither auto-initialized nor persistent.</li>
 * </ul>
 *
 * <p>For finer control over the attachment type and its properties, use {@link AttachmentRegistry#builder()} to
 * get a {@link Builder} instance.</p>
 */
@ApiStatus.Experimental
public final class AttachmentRegistry {
	private AttachmentRegistry() {
	}

	/**
	 * Creates <i>and registers</i> an attachment. The data will be neither persisted nor synced, and the
	 *
	 * @param id  the identifier of this attachment
	 * @param <A> the type of attached data
	 * @return the registered {@link AttachmentType} instance
	 */
	public static <A> AttachmentType<A> create(Identifier id) {
		Objects.requireNonNull(id, "identifier cannot be null");

		return AttachmentRegistry.<A>builder().buildAndRegister(id);
	}

	/**
	 * Creates <i>and registers</i> an attachment, that will be automatically initialized with a default value
	 * when an attachment does not exist on a given target, using {@link AttachmentTarget#getAttachedOrCreate(AttachmentType)}.
	 *
	 * @param id          the identifier of this attachment
	 * @param initializer the initializer used to provide a default value
	 * @param <A>         the type of attached data
	 * @return the registered {@link AttachmentType} instance
	 */
	public static <A> AttachmentType<A> createDefaulted(Identifier id, Supplier<A> initializer) {
		Objects.requireNonNull(id, "identifier cannot be null");
		Objects.requireNonNull(initializer, "initializer cannot be null");

		return AttachmentRegistry.<A>builder()
				.initializer(initializer)
				.buildAndRegister(id);
	}

	/**
	 * Creates <i>and registers</i> an attachment, that will persist across server restarts.
	 *
	 * @param id    the identifier of this attachment
	 * @param codec the codec used for serialization
	 * @param <A>   the type of attached data
	 * @return the registered {@link AttachmentType} instance
	 */
	public static <A> AttachmentType<A> createPersistent(Identifier id, Codec<A> codec) {
		Objects.requireNonNull(id, "identifier cannot be null");
		Objects.requireNonNull(codec, "codec cannot be null");

		return AttachmentRegistry.<A>builder().persistent(true).codec(codec).buildAndRegister(id);
	}

	/**
	 * Creates <i>and registers</i> an attachment, that will be synced between server and client.
	 *
	 * @param id    the identifier of this attachment
	 * @param codec the codec used for serialization
	 * @param <A>   the type of attached data
	 * @return the registered {@link AttachmentType} instance
	 */
	public static <A> AttachmentType<A> createSynced(Identifier id, Codec<A> codec) {
		Objects.requireNonNull(id, "identifier cannot be null");
		Objects.requireNonNull(codec, "codec cannot be null");

		return AttachmentRegistry.<A>builder()
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
	 * A builder for creating {@link AttachmentType}s with finer control over their properties.
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
		 * Sets the default initializer for this attachment type. The initializer will be called by
		 * {@link AttachmentTarget#getAttachedOrCreate(AttachmentType)} to automatically initialize attachments that
		 * don't yet exist. It must not return {@code null}.
		 *
		 * <p>It is <i>encouraged</i> for {@link A} to be an immutable data type, such as a primitive type
		 * or an immutable record.</p>
		 *
		 * <p>Otherwise, one must be very careful, as attachments <i>must not share any mutable state</i>.
		 * As an example, for a (mutable) list/array attachment type,
		 * the initializer should create a new independent instance each time it is called.</p>
		 *
		 * @param initializer the initializer
		 * @return the builder
		 */
		Builder<A> initializer(Supplier<A> initializer);

		/**
		 * Sets the {@link Codec} for this attachment.
		 *
		 * @param codec the codec
		 * @return the builder
		 */
		Builder<A> codec(Codec<A> codec);

		/**
		 * Builds and registers the {@link AttachmentType}.
		 *
		 * @param id the attachment's identifier
		 * @return the built and registered {@link AttachmentType}
		 */
		AttachmentType<A> buildAndRegister(Identifier id);
	}
}
