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

import net.fabricmc.fabric.api.transfer.v1.storage.ResourceKey;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.network.PacketByteBuf;

import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidKeyRendering;
import net.fabricmc.fabric.impl.transfer.fluid.FluidKeyImpl;

/**
 * An immutable association of a still fluid and an optional NBT tag.
 *
 * <p>Do not extend this class. Use {@link #of(Fluid)} and {@link #of(Fluid, NbtCompound)} to create instances.
 *
 * <p>{@link FluidKeyRendering} can be used for client-side rendering of fluid keys.
 *
 * @deprecated Experimental feature, we reserve the right to remove or change it without further notice.
 * The transfer API is a complex addition, and we want to be able to correct possible design mistakes.
 */
@ApiStatus.Experimental
@Deprecated
@ApiStatus.NonExtendable
public interface FluidKey extends ResourceKey<Fluid> {
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
	static FluidKey of(Fluid fluid, @Nullable NbtCompound tag) {
		return FluidKeyImpl.of(fluid, tag);
	}

	/**
	 * Return the fluid of this key.
	 */
	default Fluid getFluid() {
		return getResource();
	}

	/**
	 * Deserialize a key from an NBT compound tag, assuming it was serialized using {@link #toNbt}.
	 *
	 * <p>If an error occurs during deserialization, it will be logged with the DEBUG level, and an empty key will be returned.
	 */
	static FluidKey fromNbt(NbtCompound nbt) {
		return FluidKeyImpl.fromNbt(nbt);
	}

	/**
	 * Read a key from a packet byte buffer, assuming it was serialized using {@link #toPacket}.
	 */
	static FluidKey fromPacket(PacketByteBuf buf) {
		return FluidKeyImpl.fromPacket(buf);
	}
}
