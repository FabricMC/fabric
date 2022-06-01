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

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;

public class FluidVariantImpl implements FluidVariant {
	public static FluidVariant of(Fluid fluid, @Nullable CompoundTag nbt) {
		Objects.requireNonNull(fluid, "Fluid may not be null.");

		if (!fluid.isStill(fluid.getDefaultState()) && fluid != Fluids.EMPTY) {
			// Note: the empty fluid is not still, that's why we check for it specifically.
			throw new IllegalArgumentException("Fluid may not be flowing.");
		}

		if (nbt == null || fluid == Fluids.EMPTY) {
			// Use the cached variant inside the fluid
			return ((FluidVariantCache) fluid).fabric_getCachedFluidVariant();
		} else {
			// TODO explore caching fluid variants for non null tags.
			return new FluidVariantImpl(fluid, nbt);
		}
	}

	private static final Logger LOGGER = LogManager.getLogger("fabric-transfer-api-v1/fluid");

	private final Fluid fluid;
	private final @Nullable CompoundTag nbt;
	private final int hashCode;

	public FluidVariantImpl(Fluid fluid, CompoundTag nbt) {
		this.fluid = fluid;
		this.nbt = nbt == null ? null : nbt.copy(); // defensive copy
		this.hashCode = Objects.hash(fluid, nbt);
	}

	@Override
	public boolean isBlank() {
		return fluid == Fluids.EMPTY;
	}

	@Override
	public Fluid getObject() {
		return fluid;
	}

	@Override
	public @Nullable CompoundTag getNbt() {
		return nbt;
	}

	@Override
	public CompoundTag toNbt() {
		CompoundTag result = new CompoundTag();
		result.putString("fluid", Registry.FLUID.getId(fluid).toString());

		if (nbt != null) {
			result.put("tag", nbt.copy());
		}

		return result;
	}

	public static FluidVariant fromNbt(CompoundTag compound) {
		try {
			Fluid fluid = Registry.FLUID.get(new Identifier(compound.getString("fluid")));
			CompoundTag nbt = compound.contains("tag") ? compound.getCompound("tag") : null;
			return of(fluid, nbt);
		} catch (RuntimeException runtimeException) {
			LOGGER.debug("Tried to load an invalid FluidVariant from NBT: {}", compound, runtimeException);
			return FluidVariant.blank();
		}
	}

	@Override
	public void toPacket(PacketByteBuf buf) {
		if (isBlank()) {
			buf.writeBoolean(false);
		} else {
			buf.writeBoolean(true);
			buf.writeVarInt(Registry.FLUID.getRawId(fluid));
			buf.writeCompoundTag(nbt);
		}
	}

	public static FluidVariant fromPacket(PacketByteBuf buf) {
		if (!buf.readBoolean()) {
			return FluidVariant.blank();
		} else {
			Fluid fluid = Registry.FLUID.get(buf.readVarInt());
			CompoundTag nbt = buf.readCompoundTag();
			return of(fluid, nbt);
		}
	}

	@Override
	public String toString() {
		return "FluidVariantImpl{fluid=" + fluid + ", tag=" + nbt + '}';
	}

	@Override
	public boolean equals(Object o) {
		// succeed fast with == check
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		FluidVariantImpl fluidVariant = (FluidVariantImpl) o;
		// fail fast with hash code
		return hashCode == fluidVariant.hashCode && fluid == fluidVariant.fluid && nbtMatches(fluidVariant.nbt);
	}

	@Override
	public int hashCode() {
		return hashCode;
	}
}
