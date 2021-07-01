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

package net.fabricmc.fabric.api.transfer.v1.storage;

import java.util.Objects;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;

/**
 * An immutable association of an immutable object instance (for example {@code Item} or {@code Fluid}) and an optional NBT tag.
 *
 * <p>This is exposed for convenience for code that needs to be generic across multiple transfer keys,
 * but note that a {@link Storage} is not necessarily bound to {@code TransferKey}. Its generic parameter can be any immutable object.
 *
 * @param <O> The type of the immutable object instance, for example {@code Item} or {@code Fluid}.
 *
 * @deprecated Experimental feature, we reserve the right to remove or change it without further notice.
 * The transfer API is a complex addition, and we want to be able to correct possible design mistakes.
 */
@ApiStatus.Experimental
@Deprecated
public interface TransferKey<O> {
	/**
	 * Return true if this key is empty, and false otherwise.
	 */
	boolean isEmpty();

	/**
	 * Return the immutable object instance of this key.
	 */
	O getObject();

	/**
	 * Return the underlying tag.
	 *
	 * <p><b>NEVER MUTATE THIS NBT TAG</b>, if you need to mutate it you can use {@link #copyNbt()} to retrieve a copy instead.
	 */
	@Nullable
	NbtCompound getNbt();

	/**
	 * Return true if this key has a tag, false otherwise.
	 */
	default boolean hasNbt() {
		return getNbt() != null;
	}

	/**
	 * Return true if the tag of this key matches the passed tag, and false otherwise.
	 *
	 * <p>Note: True is returned if both tags are {@code null}.
	 */
	default boolean nbtMatches(@Nullable NbtCompound other) {
		return Objects.equals(getNbt(), other);
	}

	/**
	 * Return {@code true} if the object of this key matches the passed fluid.
	 */
	default boolean isOf(O object) {
		return getObject() == object;
	}

	/**
	 * Return a copy of the tag of this key, or {@code null} if this key doesn't have a tag.
	 *
	 * <p>Note: Use {@link #nbtMatches} if you only need to check for custom tag equality, or {@link #getNbt()} if you don't need to mutate the tag.
	 */
	@Nullable
	default NbtCompound copyNbt() {
		NbtCompound nbt = getNbt();
		return nbt == null ? null : nbt.copy();
	}

	/**
	 * Save this key into an NBT compound tag. Subinterfaces should have a matching static {@code fromNbt}.
	 *
	 * <p>Note: This key is safe to use for persisting data as resources are saved using their full Identifier.
	 */
	NbtCompound toNbt();

	/**
	 * Write this key into a packet byte buffer. Subinterfaces should have a matching static {@code fromPacket}.
	 *
	 * <p>Implementation note: Resources are saved using their raw registry integer id.
	 */
	void toPacket(PacketByteBuf buf);
}
