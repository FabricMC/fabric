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

package net.fabricmc.fabric.api.transfer.v1.fluid;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;

import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidKeyRendering;
import net.fabricmc.fabric.impl.transfer.fluid.FluidKeyImpl;

/**
 * An immutable association of a still fluid and an optional NBT tag.
 *
 * <p>Do not extend this class. Use {@link #of(Fluid)} and {@link #of(Fluid, CompoundTag)} to create instances.
 *
 * <p>{@link FluidKeyRendering} can be used for client-side rendering of fluid keys.
 *
 * @deprecated Experimental feature, we reserve the right to remove or change it without further notice.
 * The transfer API is a complex addition, and we want to be able to correct possible design mistakes.
 */
@ApiStatus.Experimental
@Deprecated
@ApiStatus.NonExtendable
public interface FluidKey {
	/**
	 * Retrieve an empty FluidKey.
	 */
	static FluidKey empty() {
		return of(Fluids.EMPTY);
	}

	/**
	 * Retrieve a FluidKey with a fluid, and a {@code null} tag.
	 */
	static FluidKey of(Fluid fluid) {
		return of(fluid, null);
	}

	/**
	 * Retrieve a FluidKey with a fluid, and an optional tag.
	 */
	static FluidKey of(Fluid fluid, @Nullable CompoundTag tag) {
		return FluidKeyImpl.of(fluid, tag);
	}

	/**
	 * Return true if this key is empty, i.e. its fluid is Fluids.EMPTY, and false otherwise.
	 */
	boolean isEmpty();

	/**
	 * Return true if the tag of this key matches the passed tag, and false otherwise.
	 *
	 * <p>Note: True is returned if both tags are {@code null}.
	 */
	boolean tagMatches(@Nullable CompoundTag other);

	/**
	 * Return true if this key has a tag, false otherwise.
	 */
	boolean hasTag();

	/**
	 * Return the fluid of this key.
	 */
	Fluid getFluid();

	/**
	 * Return a copy of the tag of this key, or {@code null} if this key doesn't have a tag.
	 *
	 * <p>Note: Use {@link #tagMatches} if you only need to check for tag equality.
	 */
	@Nullable
	CompoundTag copyTag();

	/**
	 * Save this key into an NBT compound tag. {@link #fromNbt} can be used to retrieve the key later.
	 *
	 * <p>Note: This key is safe to use for persisting data as fluids are saved using their full Identifier.
	 */
	CompoundTag toNbt();

	/**
	 * Deserialize a key from an NBT compound tag, assuming it was serialized using {@link #toNbt}.
	 *
	 * <p>If an error occurs during deserialization, it will be logged with the DEBUG level, and an empty key will be returned.
	 */
	static FluidKey fromNbt(CompoundTag nbt) {
		return FluidKeyImpl.fromNbt(nbt);
	}

	/**
	 * Write this key into a packet byte buffer. {@link #fromPacket} can be used to retrieve the key later.
	 *
	 * <p>Implementation note: Fluids are saved using their raw registry integer id.
	 */
	void toPacket(PacketByteBuf buf);

	/**
	 * Read a key from a packet byte buffer, assuming it was serialized using {@link #toPacket}.
	 */
	static FluidKey fromPacket(PacketByteBuf buf) {
		return FluidKeyImpl.fromPacket(buf);
	}
}
