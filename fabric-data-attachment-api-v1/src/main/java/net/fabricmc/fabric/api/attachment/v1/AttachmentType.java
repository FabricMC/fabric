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
import java.util.function.Function;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
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
	static <A> AttachmentType<A, BlockEntity> forBlockEntity(Identifier identifier, Class<A> attachmentClass, @Nullable Serializer<A, ? super BlockEntity> serializer) {
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
	static <A> AttachmentType<A, WorldChunk> forChunk(Identifier identifier, Class<A> attachmentClass, @Nullable Serializer<A, ? super WorldChunk> serializer) {
		return AttachmentTypeImpl.create(identifier, attachmentClass, WorldChunk.class, serializer);
	}

	/**
	 * Create a new block entity attachment type with a given identifier, an attachment class, and optionally a serializer.
	 *
	 * <p>Serialization will only be used on the logical server.
	 * Entity data is always persisted, there is no need to call any method after modifying the attachments.
	 *
	 * <p>Note that data is not transferred automatically in the following cases, and requires special handling if desired:
	 * <ul>
	 *     <li>On the (logical) client and server: when a player dies and respawns.
	 *     {@code ServerPlayerEvents.COPY_FROM} can be used to copy the data on the server.</li>
	 *     <li>On the client: when a player changes dimensions.</li>
	 *     <li>On the client and server: when a non-player entity changes dimensions.</li>
	 * </ul>
	 *
	 * @throws IllegalArgumentException If an attachment type with the given identifier already exists for block entities.
	 */
	static <A> AttachmentType<A, Entity> forEntity(Identifier identifier, Class<A> attachmentClass, @Nullable Serializer<A, ? super Entity> serializer) {
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
	static <A> AttachmentType<A, World> forWorld(Identifier identifier, Class<A> attachmentClass, @Nullable Serializer<A, ? super World> serializer) {
		return AttachmentTypeImpl.create(identifier, attachmentClass, World.class, serializer);
	}

	default A get(T target) {
		return ((AttachmentTargetImpl) target).get(this);
	}

	default A computeIfAbsent(T target, Function<? super T, A> computeFunction) {
		AttachmentTargetImpl impl = (AttachmentTargetImpl) target;
		A attachment = impl.get(this);

		if (attachment == null) {
			impl.set(this, attachment = computeFunction.apply(target));
		}

		return attachment;
	}

	@Nullable
	default A set(T target, A value) {
		return ((AttachmentTargetImpl) target).set(this, value);
	}

	default A remove(T target) {
		return ((AttachmentTargetImpl) target).remove(this);
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

	/**
	 * Return the serializer of this attachment type, or {@code null} if it has no serializer.
	 */
	// TODO: should maybe remove from API?
	@Nullable
	Serializer<A, ? super T> getSerializer();

	interface Serializer<A, T> {
		/**
		 * Serialize the value to a new NBT compound.
		 * If null is returned, the value will not be saved at all.
		 */
		@Nullable
		NbtCompound toNbt(A value);

		/**
		 * Create a new instance from an NBT compound previously created by {@link #toNbt}.
		 * If null is returned, the instance will not be placed in the attachment target.
		 */
		@Nullable
		A fromNbt(T target, NbtCompound nbt);

		/**
		 * Create an attachment serializer from a codec.
		 */
		static <A, T> Serializer<A, T> fromCodec(Codec<A> codec) {
			Objects.requireNonNull(codec, "Codec may not be null.");

			return new Serializer<>() {
				@Override
				@Nullable
				public NbtCompound toNbt(A value) {
					@Nullable
					NbtElement element = codec.encodeStart(NbtOps.INSTANCE, value).result().orElse(null);

					if (element instanceof NbtCompound compound) {
						return compound;
					} else if (element != null) {
						NbtCompound compound = new NbtCompound();
						compound.put("fabric:value", element);
						return compound;
					}

					return null;
				}

				@Override
				@Nullable
				public A fromNbt(T target, NbtCompound nbt) {
					NbtElement toDecode = nbt;

					if (nbt.getSize() == 1 && nbt.contains("fabric:value")) {
						toDecode = nbt.get("fabric:value");
					}

					return codec.decode(NbtOps.INSTANCE, toDecode).result().map(Pair::getFirst).orElse(null);
				}
			};
		}
	}
}
