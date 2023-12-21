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

import java.util.function.UnaryOperator;

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
public interface AttachmentTarget {
	String NBT_ATTACHMENT_KEY = "fabric:attachments";

	/**
	 * Gets the data associated with the given {@link AttachmentType}, or {@code null} if it doesn't yet exist.
	 *
	 * @param type the attachment type
	 * @param <A> the type of the data
	 * @return the attached data
	 */
	@Nullable
	default <A> A getAttached(AttachmentType<A> type) {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	/**
	 * Gets the data associated with the given {@link DefaultedAttachmentType}, or <i>initializes it</i> automatically
	 * using {@link DefaultedAttachmentType#initializer()} if it doesn't yet exist.
	 *
	 * @param type the defaulted attachment type
	 * @param <A> the type of the data
	 * @return the attached data
	 */
	default <A> A getAttached(DefaultedAttachmentType<A> type) {
		A current = getAttached((AttachmentType<? extends A>) type);

		if (current == null) {
			A initialized = type.initializer().get();
			setAttached(type, initialized);
			return initialized;
		} else {
			return current;
		}
	}

	/**
	 * Sets the data associated with the given {@link AttachmentType}. Passing {@code null} removes the data.
	 *
	 * @param type the attachment type
	 * @param value the new value
	 * @param <A> the type of the data
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
	 * @param <A> the type of the data
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
	 * @param type the attachment type
	 * @param modifier the operation to apply to the current data
	 * @param <A> the type of the data
	 * @return the previous data
	 */
	@Nullable
	default <A> A modifyAttached(AttachmentType<A> type, UnaryOperator<A> modifier) {
		return setAttached(type, modifier.apply(getAttached(type)));
	}

	/**
	 * Modifies the data associated with the given {@link DefaultedAttachmentType}. Functionally the same as calling {@link #getAttached(DefaultedAttachmentType)},
	 * applying the modifier, then calling {@link #setAttached(AttachmentType, Object)} with the result.
	 *
	 * @param type the attachment type
	 * @param modifier the operation to apply to the current data
	 * @param <A> the type of the data
	 * @return the previous data
	 */
	@Nullable
	default <A> A modifyAttached(DefaultedAttachmentType<A> type, UnaryOperator<A> modifier) {
		return setAttached(type, modifier.apply(getAttached(type)));
	}
}
