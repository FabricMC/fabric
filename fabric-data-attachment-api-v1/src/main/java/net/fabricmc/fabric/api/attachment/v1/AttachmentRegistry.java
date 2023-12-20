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

import org.jetbrains.annotations.Nullable;

import net.minecraft.util.Identifier;

import net.fabricmc.fabric.impl.attachment.AttachmentRegistryImpl;

/**
 * Class used to create and register {@link Attachment}s. To quickly create {@link Attachment}s with default values, use
 * {@link AttachmentRegistry#createDefaulted(Identifier, Object)}, or {@link AttachmentRegistry#createDefaulted(Identifier, Object, AttachmentSerializer)}
 * to provide an {@link AttachmentSerializer}.
 *
 * <p>For more control over the attachment and its properties, use {@link AttachmentRegistry#builder()} to
 * get a {@link Builder} instance.</p>
 */
public final class AttachmentRegistry {
	private AttachmentRegistry() {
	}

	/**
	 * Creates <i>and registers</i> an attachment, initialized using a default value.
	 *
	 * @param id the identifier of this attachment
	 * @param defaultValue the default value, used for objects which don't have attached data yet. Must not be {@code null}
	 * @param <A> the type of attached data
	 * @return the registered {@link Attachment} instance
	 */
	public static <A> Attachment<A> createDefaulted(Identifier id, A defaultValue) {
		Objects.requireNonNull(id, "identifier cannot be null");
		Objects.requireNonNull(defaultValue, "default value cannot be null");

		return AttachmentRegistry.<A>builder().defaultValue(defaultValue).buildAndRegister(id);
	}

	/**
	 * Creates <i>and registers</i> an attachment, initialized using a default value, and optionally a {@link AttachmentSerializer serializer}.
	 *
	 * @param id the identifier of this attachment
	 * @param defaultValue the default value, used for objects which don't have attached data yet. Must not be {@code null}
	 * @param serializer the serializer for this attachment. {@code null} for no serializer
	 * @param <A> the type of attached data
	 * @return the registered {@link Attachment} instance
	 */
	public static <A> Attachment<A> createDefaulted(Identifier id, A defaultValue, AttachmentSerializer<A> serializer) {
		Objects.requireNonNull(id, "identifier cannot be null");
		Objects.requireNonNull(defaultValue, "default value cannot be null");
		Objects.requireNonNull(serializer, "serializer cannot be null");

		return AttachmentRegistry.<A>builder().defaultValue(defaultValue).buildAndRegister(id);
	}

	/**
	 * Gets the {@link Attachment} associated with the given {@link Identifier}, or {@code null} if none was registered.
	 *
	 * @param id the identifier of the attachment
	 * @return the corresponding attachment, or {@code null} if it doesn't exist
	 */
	@Nullable
	public static Attachment<?> get(Identifier id) {
		return AttachmentRegistryImpl.get(id);
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
		 * Sets whether the attachment should be synced between server and client or not.
		 *
		 * @param synced whether the attachment data should be synced
		 * @return the builder
		 */
		Builder<A> synced(boolean synced);

		/**
		 * Makes the attachment use the argument as a starting value for objects which don't have attached data yet.
		 *
		 * @param defaultVal the default value
		 * @return the builder
		 */
		Builder<A> defaultValue(A defaultVal);

		/**
		 * Uses the given {@link Supplier} to get a starting value for objects which don't have attached data yet.
		 *
		 * @param supplier the initializer
		 * @return the builder
		 */
		Builder<A> initializer(Supplier<A> supplier);

		/**
		 * Sets the {@link AttachmentSerializer} for this attachment.
		 *
		 * @param serializer the serializer
		 * @return the builder
		 */
		Builder<A> serializer(AttachmentSerializer<A> serializer);

		/**
		 * Builds and registers the {@link Attachment}.
		 *
		 * @param id the attachment's identifier
		 * @return the built and registered {@link Attachment}
		 */
		Attachment<A> buildAndRegister(Identifier id);
	}
}
