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

package net.fabricmc.fabric.impl.transfer.fluid;

import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidKey;

public class FluidKeyImpl implements FluidKey {
	public static FluidKey of(Fluid fluid, @Nullable NbtCompound tag) {
		Objects.requireNonNull(fluid, "Fluid may not be null.");

		if (!fluid.isStill(fluid.getDefaultState()) && fluid != Fluids.EMPTY) {
			// Note: the empty fluid is not still, that's why we check for it specifically.
			throw new IllegalArgumentException("Fluid may not be flowing.");
		}

		if (tag == null) {
			// Use the cached key inside the fluid
			return ((FluidKeyCache) fluid).fabric_getCachedFluidKey();
		} else {
			// TODO explore caching fluid keys for non null tags.
			return new FluidKeyImpl(fluid, tag);
		}
	}

	private static final Logger LOGGER = LogManager.getLogger("fabric-transfer-api-v1/fluid");

	private final Fluid fluid;
	private final @Nullable NbtCompound tag;
	private final int hashCode;

	public FluidKeyImpl(Fluid fluid, NbtCompound tag) {
		this.fluid = fluid;
		this.tag = tag == null ? null : tag.copy(); // defensive copy
		this.hashCode = Objects.hash(fluid, tag);
	}

	@Override
	public boolean isEmpty() {
		return fluid == Fluids.EMPTY;
	}

	@Override
	public Fluid getResource() {
		return fluid;
	}

	@Override
	public @Nullable NbtCompound getTag() {
		return tag;
	}

	@Override
	public NbtCompound toNbt() {
		NbtCompound result = new NbtCompound();
		result.putString("fluid", Registry.FLUID.getId(fluid).toString());

		if (tag != null) {
			result.put("tag", tag.copy());
		}

		return result;
	}

	public static FluidKey fromNbt(NbtCompound tag) {
		try {
			Fluid fluid = Registry.FLUID.get(new Identifier(tag.getString("fluid")));
			NbtCompound aTag = tag.contains("tag") ? tag.getCompound("tag") : null;
			return of(fluid, aTag);
		} catch (RuntimeException runtimeException) {
			LOGGER.debug("Tried to load an invalid FluidKey from NBT: {}", tag, runtimeException);
			return FluidKey.empty();
		}
	}

	@Override
	public void toPacket(PacketByteBuf buf) {
		if (isEmpty()) {
			buf.writeBoolean(false);
		} else {
			buf.writeBoolean(true);
			buf.writeVarInt(Registry.FLUID.getRawId(fluid));
			buf.writeNbt(tag);
		}
	}

	public static FluidKey fromPacket(PacketByteBuf buf) {
		if (!buf.readBoolean()) {
			return FluidKey.empty();
		} else {
			Fluid fluid = Registry.FLUID.get(buf.readVarInt());
			NbtCompound tag = buf.readNbt();
			return of(fluid, tag);
		}
	}

	@Override
	public String toString() {
		return "FluidKeyImpl{fluid=" + fluid + ", tag=" + tag + '}';
	}

	@Override
	public boolean equals(Object o) {
		// succeed fast with == check
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		FluidKeyImpl fluidKey = (FluidKeyImpl) o;
		// fail fast with hash code
		return hashCode == fluidKey.hashCode && fluid == fluidKey.fluid && tagMatches(fluidKey.tag);
	}

	@Override
	public int hashCode() {
		return hashCode;
	}
}
