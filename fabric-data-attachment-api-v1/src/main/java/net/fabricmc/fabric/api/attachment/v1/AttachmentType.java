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

import java.util.function.Supplier;

import com.mojang.serialization.Codec;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

/**
 * An attachment allows "attaching" arbitrary data to various game objects (entities, block entities, worlds and chunks at the moment).
 * Use the methods provided in {@link AttachmentRegistry} to create and register attachments.
 *
 * <p>Attachments can optionally be made to persist between restarts using a provided {@link Codec}.</p>
 *
 * @param <A> type of the attached data. It is encouraged for this to be an immutable type.
 */
@ApiStatus.NonExtendable
@ApiStatus.Experimental
public interface AttachmentType<A> {
	/**
	 * @return the identifier that uniquely identifies this attachment
	 */
	Identifier identifier();

	/**
	 * An optional {@link Codec} used for reading and writing attachments to NBT for persistence and copying.
	 *
	 * @return the persistence codec, may be {@code null}
	 */
	@Nullable
	Codec<A> codec();

	/**
	 * @return whether the attachments persist across server restarts
	 */
	boolean persistent();

	/**
	 * If an object has no value associated to an attachment,
	 * this initializer is used to create a non-{@code null} starting value.
	 *
	 * <p>It is <i>encouraged</i> for {@link A} to be an immutable data type, such as a primitive type
	 * or an immutable record.</p>
	 *
	 * <p>Otherwise, one must be very careful, as attachments <i>must not share any mutable state</i>.
	 * As an example, for a (mutable) list/array attachment type,
	 * the initializer should create a new independent instance each time it is called.</p>
	 *
	 * @return the initializer for this attachment, may be {@code null}
	 */
	@Nullable
	Supplier<A> initializer();

	/**
	 * @return the {@link EntityCopyHandler} of this attachment type, may be {@code null}
	 */
	@Nullable
	AttachmentType.EntityCopyHandler<A> entityCopyHandler();

	/**
	 * @return whether the attachments should persist after a player's death and respawn
	 */
	boolean copyOnDeath();

	/**
	 * A functional interface to handle copying attachment data from an old entity instance to a new one, for example
	 * during player respawn, entity conversion, or when an entity is teleported between worlds.
	 *
	 * <p>It is <i>imperative</i> that the data attached to the new entity doesn't hold a reference to the old entity,
	 * as that can and will break in unexpected ways.</p>
	 */
	@FunctionalInterface
	interface EntityCopyHandler<T> {
		/**
		 * Copies an attachment's data from an old entity instance (which will be discarded) to a new one. The data returned
		 * will be attached to the new entity and <i>must not</i> hold a reference to the old one.
		 *
		 * @param original  the previously attached data
		 * @param oldEntity the entity to copy the attachment from
		 * @param newEntity the entity to copy the attachment to
		 * @return the new data for this attachment. <i>Must not</i> hold a reference to {@code oldEntity}.
		 */
		T copyAttachment(T original, Entity oldEntity, Entity newEntity);
	}
}
