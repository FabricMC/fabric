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

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;

/**
 * An immutable association of an immutable object instance (for example {@code Item} or {@code Fluid}) and an optional NBT tag.
 *
 * <p>This is exposed for convenience for code that needs to be generic across multiple transfer variants,
 * but note that a {@link Storage} is not necessarily bound to {@code TransferVariant}. Its generic parameter can be any immutable object.
 *
 * <p><b>Transfer variants must always be compared with {@link #equals}, never by reference!</b>
 * {@link #hashCode} is guaranteed to be correct and constant time independently of the size of the NBT.
 *
 * @param <O> The type of the immutable object instance, for example {@code Item} or {@code Fluid}.
 *
 * <b>Experimental feature</b>, we reserve the right to remove or change it without further notice.
 * The transfer API is a complex addition, and we want to be able to correct possible design mistakes.
 */
@ApiStatus.Experimental
public interface TransferVariant<O> {
	/**
	 * Return true if this variant is blank, and false otherwise.
	 */
	boolean isBlank();

	/**
	 * Return the immutable object instance of this variant.
	 */
	O getObject();

	/**
	 * Return the underlying tag.
	 *
	 * <p><b>NEVER MUTATE THIS NBT TAG</b>, if you need to mutate it you can use {@link #copyNbt()} to retrieve a copy instead.
	 */
	@Nullable
	CompoundTag getNbt();

	/**
	 * Return true if this variant has a tag, false otherwise.
	 */
	default boolean hasNbt() {
		return getNbt() != null;
	}

	/**
	 * Return true if the tag of this variant matches the passed tag, and false otherwise.
	 *
	 * <p>Note: True is returned if both tags are {@code null}.
	 */
	default boolean nbtMatches(@Nullable CompoundTag other) {
		return Objects.equals(getNbt(), other);
	}

	/**
	 * Return {@code true} if the object of this variant matches the passed fluid.
	 */
	default boolean isOf(O object) {
		return getObject() == object;
	}

	/**
	 * Return a copy of the tag of this variant, or {@code null} if this variant doesn't have a tag.
	 *
	 * <p>Note: Use {@link #nbtMatches} if you only need to check for custom tag equality, or {@link #getNbt()} if you don't need to mutate the tag.
	 */
	@Nullable
	default CompoundTag copyNbt() {
		CompoundTag nbt = getNbt();
		return nbt == null ? null : nbt.copy();
	}

	/**
	 * Save this variant into an NBT compound tag. Subinterfaces should have a matching static {@code fromNbt}.
	 *
	 * <p>Note: This is safe to use for persisting data as objects are saved using their full Identifier.
	 */
	CompoundTag toNbt();

	/**
	 * Write this variant into a packet byte buffer. Subinterfaces should have a matching static {@code fromPacket}.
	 *
	 * <p>Implementation note: Objects are saved using their raw registry integer id.
	 */
	void toPacket(PacketByteBuf buf);
}
