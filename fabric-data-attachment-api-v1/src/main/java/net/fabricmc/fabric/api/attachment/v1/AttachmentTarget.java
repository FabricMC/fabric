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
import java.util.function.UnaryOperator;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.chunk.WorldChunk;

/**
 * Marks all objects on which data can be attached using {@link AttachmentType}s.
 *
 * <p>Fabric implements this on {@link Entity}, {@link BlockEntity}, {@link ServerWorld} and {@link WorldChunk} via mixin.</p>
 */
@ApiStatus.Experimental
@ApiStatus.NonExtendable
public interface AttachmentTarget {
	String NBT_ATTACHMENT_KEY = "fabric:attachments";

	/**
	 * Gets the data associated with the given {@link AttachmentType}, or {@code null} if it doesn't yet exist.
	 *
	 * @param type the attachment type
	 * @param <A>  the type of the data
	 * @return the attached data
	 */
	@Nullable
	default <A> A getAttached(AttachmentType<A> type) {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	/**
	 * Gets the data associated with the given {@link AttachmentType}, throwing an exception if it doesn't yet exist.
	 *
	 * @param type the attachment type
	 * @param <A>  the type of the data
	 * @return the attached data
	 */
	default <A> A getAttachedOrThrow(AttachmentType<A> type) {
		return Objects.requireNonNull(getAttached(type));
	}

	/**
	 * Gets the data associated with the given {@link AttachmentType}, or initializes it using the provided non-{@code null}
	 * default value.
	 *
	 * @param type         the attachment type
	 * @param defaultValue the fallback default value
	 * @param <A>          the type of the data
	 * @return the attached data, initialized if originally absent
	 */
	default <A> A getAttachedOrSet(AttachmentType<A> type, A defaultValue) {
		Objects.requireNonNull(defaultValue, "default value cannot be null");
		A attached = getAttached(type);

		if (attached != null) {
			return attached;
		} else {
			setAttached(type, defaultValue);
			return defaultValue;
		}
	}

	/**
	 * Gets the data associated with the given {@link AttachmentType}, or initializes it using the non-{@code null} result
	 * of the provided {@link Supplier}.
	 *
	 * @param type        the attachment type
	 * @param initializer the fallback initializer
	 * @param <A>         the type of the data
	 * @return the attached data, initialized if originally absent
	 */
	default <A> A getAttachedOrCreate(AttachmentType<A> type, Supplier<A> initializer) {
		A attached = getAttached(type);

		if (attached != null) {
			return attached;
		} else {
			A initialized = Objects.requireNonNull(initializer.get(), "initializer result cannot be null");
			setAttached(type, initialized);
			return initialized;
		}
	}

	/**
	 * Specialization of {@link #getAttachedOrCreate(AttachmentType, Supplier)}, but <i>only for attachment types with
	 * {@link AttachmentType#initializer() initializers}.</i> It will throw an exception if one is not present.
	 *
	 * @param type the attachment type
	 * @param <A>  the type of the data
	 * @return the attached data, initialized if originally absent
	 */
	default <A> A getAttachedOrCreate(AttachmentType<A> type) {
		Supplier<A> init = type.initializer();

		if (init == null) {
			throw new IllegalArgumentException("Single-argument getAttachedOrCreate is reserved for attachment types with default initializers");
		}

		return getAttachedOrCreate(type, init);
	}

	/**
	 * Gets the data associated with the given {@link AttachmentType}, or returns the provided default value if it doesn't exist.
	 * Unlike {@link #getAttachedOrCreate(AttachmentType, Supplier)}, this doesn't initialize the attachment with the default value.
	 *
	 * @param type         the attachment type
	 * @param defaultValue the default value to use as fallback
	 * @param <A>          the type of the attached data
	 * @return the attached data, or the default value
	 */
	default <A> A getAttachedOrElse(AttachmentType<A> type, A defaultValue) {
		Objects.requireNonNull(defaultValue, "default value cannot be null");

		A attached = getAttached(type);
		return attached == null ? defaultValue : attached;
	}

	/**
	 * Gets the data associated with the given {@link AttachmentType}, or gets the provided default value from the
	 * argument if it doesn't exist. Unlike {@link #getAttachedOrCreate(AttachmentType, Supplier)}, this doesn't
	 * initialize the attachment with the default value.
	 *
	 * @param type         the attachment type
	 * @param defaultValue the default value supplier to use as fallback
	 * @param <A>          the type of the attached data
	 * @return the attached data, or the default value
	 */
	default <A> A getAttachedOrGet(AttachmentType<A> type, Supplier<A> defaultValue) {
		Objects.requireNonNull(defaultValue, "default value cannot be null");

		A attached = getAttached(type);
		return attached == null ? defaultValue.get() : attached;
	}

	/**
	 * Sets the data associated with the given {@link AttachmentType}. Passing {@code null} removes the data.
	 *
	 * @param type  the attachment type
	 * @param value the new value
	 * @param <A>   the type of the data
	 * @return the previous data
	 */
	@Nullable
	default <A> A setAttached(AttachmentType<A> type, @Nullable A value) {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	/**
	 * Tests whether the given {@link AttachmentType} has any associated data.
	 *
	 * @param type the attachment type
	 * @return whether there is associated data
	 */
	default boolean hasAttached(AttachmentType<?> type) {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	/**
	 * Removes any data associated with the given {@link AttachmentType}. Equivalent to calling {@link #setAttached(AttachmentType, Object)}
	 * with {@code null}.
	 *
	 * @param type the attachment type
	 * @param <A>  the type of the data
	 * @return the previous data
	 */
	@Nullable
	default <A> A removeAttached(AttachmentType<A> type) {
		return setAttached(type, null);
	}

	/**
	 * Modifies the data associated with the given {@link AttachmentType}. Functionally the same as calling {@link #getAttached(AttachmentType)},
	 * applying the modifier, then calling {@link #setAttached(AttachmentType, Object)} with the result.
	 *
	 * @param type     the attachment type
	 * @param modifier the operation to apply to the current data
	 * @param <A>      the type of the data
	 * @return the previous data
	 */
	@Nullable
	default <A> A modifyAttached(AttachmentType<A> type, UnaryOperator<A> modifier) {
		return setAttached(type, modifier.apply(getAttached(type)));
	}
}
