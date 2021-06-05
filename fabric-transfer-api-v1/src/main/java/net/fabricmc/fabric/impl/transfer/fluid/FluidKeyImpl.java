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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidKey;

public class FluidKeyImpl implements FluidKey {
	public static FluidKey of(Fluid fluid, @Nullable CompoundTag tag) {
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
	private final @Nullable CompoundTag tag;
	private final int hashCode;

	public FluidKeyImpl(Fluid fluid, CompoundTag tag) {
		this.fluid = fluid;
		this.tag = tag == null ? null : tag.copy(); // defensive copy
		this.hashCode = Objects.hash(fluid, tag);
	}

	@Override
	public boolean isEmpty() {
		return fluid == Fluids.EMPTY;
	}

	@Override
	public boolean tagMatches(@Nullable CompoundTag other) {
		return Objects.equals(tag, other);
	}

	@Override
	public boolean hasTag() {
		return tag != null;
	}

	@Override
	public Fluid getFluid() {
		return fluid;
	}

	@Override
	public @Nullable CompoundTag copyTag() {
		return tag == null ? null : tag.copy();
	}

	@Override
	public CompoundTag toNbt() {
		CompoundTag result = new CompoundTag();
		result.putString("fluid", Registry.FLUID.getId(fluid).toString());

		if (tag != null) {
			result.put("tag", tag.copy());
		}

		return result;
	}

	public static FluidKey fromNbt(CompoundTag tag) {
		try {
			Fluid fluid = Registry.FLUID.get(new Identifier(tag.getString("fluid")));
			CompoundTag aTag = tag.contains("tag") ? tag.getCompound("tag") : null;
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
			buf.writeCompoundTag(tag);
		}
	}

	public static FluidKey fromPacket(PacketByteBuf buf) {
		if (!buf.readBoolean()) {
			return FluidKey.empty();
		} else {
			Fluid fluid = Registry.FLUID.get(buf.readVarInt());
			CompoundTag tag = buf.readCompoundTag();
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
