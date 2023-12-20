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
 * Marks all objects on which data can be attached using {@link Attachment}s.
 *
 * <p>Fabric implements this on {@link Entity}, {@link BlockEntity}, {@link ServerWorld} and {@link WorldChunk} via mixin.</p>
 */
public interface AttachmentTarget {
	String NBT_ATTACHMENT_KEY = "fabric:attachments";

	/**
	 * Gets the data associated with the given {@link Attachment}. If it doesn't exist yet,
	 * it is generated using {@link Attachment#initializer()}.
	 *
	 * @param attachment the attachment
	 * @param <A> the type of the data
	 * @return the attached data
	 */
	default <A> A getAttached(Attachment<A> attachment) {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	/**
	 * Sets the data associated with the given {@link Attachment}. Passing {@code null} removes the data.
	 *
	 * @param attachment the attachment
	 * @param value the new value
	 * @param <A> the type of the data
	 * @return the previous data
	 */
	@Nullable
	default <A> A setAttached(Attachment<A> attachment, @Nullable A value) {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	/**
	 * Tests whether the given {@link Attachment} has any associated data.
	 *
	 * @param attachment the attachment
	 * @return whether there is associated data
	 */
	default boolean hasAttached(Attachment<?> attachment) {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	/**
	 * Removes any data associated with the given {@link Attachment}. Equivalent to calling {@link #setAttached(Attachment, Object)}
	 * with {@code null}.
	 *
	 * @param attachment the attachment
	 * @param <A> the type of the data
	 * @return the previous data
	 */
	@Nullable
	default <A> A removeAttached(Attachment<A> attachment) {
		return setAttached(attachment, null);
	}

	/**
	 * Modifies the data associated with the given {@link Attachment}. Functionally the same as calling {@link #getAttached(Attachment)},
	 * applying the modifier, then calling {@link #setAttached(Attachment, Object)} with the result.
	 *
	 * @param attachment the attachment
	 * @param modifier the operation to apply to the current data
	 * @param <A> the type of the data
	 * @return the previous data
	 */
	@Nullable
	default <A> A modifyAttached(Attachment<A> attachment, UnaryOperator<A> modifier) {
		return setAttached(attachment, modifier.apply(getAttached(attachment)));
	}
}
