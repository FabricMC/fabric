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

import java.util.function.Function;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;

import net.fabricmc.fabric.impl.attachment.AttachmentTargetImpl;
import net.fabricmc.fabric.impl.attachment.AttachmentTypeImpl;

/**
 * Data attachments allow anyone to "attach" arbitrary data to common game objects (block entities, chunks, entities and world at the moment).
 *
 * <p>To attach data, one needs to create an attachment type that specifies the type of the arbitrary data, and how it should be persisted.
 * This is done using the various {@code for*} factory methods:
 * <ul>
 *     <li>{@link #forBlockEntity} for block entities.</li>
 *     <li>{@link #forChunk} for chunks.</li>
 *     <li>{@link #forEntity} for entities.</li>
 *     <li>{@link #forWorld} for worlds.</li>
 * </ul>
 *
 * @param <A> Type of the attached object.
 * @param <T> Type of the host of this attachment.
 */
@ApiStatus.NonExtendable
public interface AttachmentType<A, T> {
	/**
	 * Create a new block entity attachment type with a given identifier, an attachment class, and optionally a serializer.
	 *
	 * <p>Serialization will only be used on the logical server.
	 * Make sure to call {@link BlockEntity#markDirty()} after a change to the data attachments to ensure that they get persisted.
	 *
	 * @throws IllegalArgumentException If an attachment type with the given identifier already exists for block entities.
	 */
	static <A> AttachmentType<A, BlockEntity> forBlockEntity(Identifier identifier, Class<A> attachmentClass, @Nullable AttachmentSerializer<A, ? super BlockEntity> serializer) {
		return AttachmentTypeImpl.create(identifier, attachmentClass, BlockEntity.class, serializer);
	}

	/**
	 * Create a new block entity attachment type with a given identifier, an attachment class, and optionally a serializer.
	 *
	 * <p>Serialization will only be used on the logical server.
	 * Make sure to call {@link WorldChunk#setNeedsSaving} after a change to the data attachments to ensure that they get persisted.
	 *
	 * @throws IllegalArgumentException If an attachment type with the given identifier already exists for block entities.
	 */
	static <A> AttachmentType<A, WorldChunk> forChunk(Identifier identifier, Class<A> attachmentClass, @Nullable AttachmentSerializer<A, ? super WorldChunk> serializer) {
		return AttachmentTypeImpl.create(identifier, attachmentClass, WorldChunk.class, serializer);
	}

	/**
	 * Create a new block entity attachment type with a given identifier, an attachment class, and optionally a serializer.
	 *
	 * <p>Serialization will only be used on the logical server.
	 * Entity data is always persisted, there is no need to call any method after modifying the attachments.
	 *
	 * <p><h3>Notes on persistence</h3>
	 *
	 * <p><h4>Serializable server-side attachments</h4>
	 * Serializable attachments will generally be persisted (on the server side), with the following caveat:
	 * <ul>
	 *     <li>When a player dies and respawns, attachment data is not carried over.
	 *     {@code ServerPlayerEvents.COPY_FROM} can be used to copy the data from the old player entity to the new player entity, if desired.</li>
	 * </ul>
	 *
	 * <p><h4>Non-serializable server-side attachments</h4>
	 * Non-serializable attachments will disappear on the server in the following circumstances:
	 * <ul>
	 *     <li>When a player dies and respawns.</li>
	 *     <li>When a non-player entity changes dimension.</li>
	 * </ul>
	 *
	 * <p><h4>Client-side attachments</h4>
	 * On the logical client, entity attachments should be used with caution, as they will disappear in (at least) the following circumstances:
	 * <ul>
	 *     <li>When the client player dies or changes dimension, the player and all the other entities lose their attachments.</li>
	 *     <li>When an entity changes dimension, it loses any client-side attachment.</li>
	 * </ul>
	 *
	 * @throws IllegalArgumentException If an attachment type with the given identifier already exists for block entities.
	 */
	static <A> AttachmentType<A, Entity> forEntity(Identifier identifier, Class<A> attachmentClass, @Nullable AttachmentSerializer<A, ? super Entity> serializer) {
		return AttachmentTypeImpl.create(identifier, attachmentClass, Entity.class, serializer);
	}

	/**
	 * Create a new block entity attachment type with a given identifier, an attachment class, and optionally a serializer.
	 *
	 * <p>Serialization will only be used on the logical server.
	 * World data is always persisted, there is no need to call any method after modifying the attachments.
	 *
	 * @throws IllegalArgumentException If an attachment type with the given identifier already exists for block entities.
	 */
	static <A> AttachmentType<A, World> forWorld(Identifier identifier, Class<A> attachmentClass, @Nullable AttachmentSerializer<A, ? super World> serializer) {
		return AttachmentTypeImpl.create(identifier, attachmentClass, World.class, serializer);
	}

	/**
	 * Return the value attached to the target, or {@code null} if no attachment is present.
	 */
	default A get(T target) {
		return ((AttachmentTargetImpl) target).get(this);
	}

	/**
	 * If the given target already has an attached value, return it.
	 * Otherwise, create it using {@code computeFunction}, attach it if it is non-null, and return it.
	 */
	default A computeIfAbsent(T target, Function<? super T, A> computeFunction) {
		AttachmentTargetImpl impl = (AttachmentTargetImpl) target;
		A attachment = impl.get(this);

		if (attachment == null) {
			impl.set(this, attachment = computeFunction.apply(target));
		}

		return attachment;
	}

	/**
	 * Attach a new value to the target, replacing the old value.
	 *
	 * @param value The new value, or {@code null} to remove the attachment.
	 * @return The old attached value, or {@code null} if no attachment was present.
	 */
	@Nullable
	default A set(T target, @Nullable A value) {
		return ((AttachmentTargetImpl) target).set(this, value);
	}

	/**
	 * Remove the attached value from the target, if there is one.
	 *
	 * @return The old attached value, or {@code null} if no attachment was present.
	 */
	default A remove(T target) {
		return ((AttachmentTargetImpl) target).set(this, null);
	}

	/**
	 * Return the unique identifier of this attachment type.
	 */
	Identifier getIdentifier();

	/**
	 * Return the class of this attachment type.
	 */
	Class<A> getAttachmentClass();

	/**
	 * Return the target class of this attachment type.
	 */
	Class<T> getTargetClass();
}
