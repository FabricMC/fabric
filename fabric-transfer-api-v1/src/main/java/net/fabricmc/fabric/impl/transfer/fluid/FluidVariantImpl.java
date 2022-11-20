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

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.jetbrains.annotations.Nullable;

import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;

public class FluidVariantImpl implements FluidVariant {
	public static FluidVariant of(Fluid fluid, @Nullable NbtCompound nbt) {
		Objects.requireNonNull(fluid, "Fluid may not be null.");

		if (!fluid.isStill(fluid.getDefaultState()) && fluid != Fluids.EMPTY) {
			// Note: the empty fluid is not still, that's why we check for it specifically.

			if (fluid instanceof FlowableFluid flowable) {
				// Normalize FlowableFluids to their still variants.
				fluid = flowable.getStill();
			} else {
				// If not a FlowableFluid, we don't know how to convert -> crash.
				Identifier id = Registry.FLUID.getId(fluid);
				throw new IllegalArgumentException("Cannot convert flowing fluid %s (%s) into a still fluid.".formatted(id, fluid));
			}
		}

		if (nbt == null || fluid == Fluids.EMPTY) {
			// Use the cached variant inside the fluid
			return ((FluidVariantCache) fluid).fabric_getCachedFluidVariant();
		} else {
			// TODO explore caching fluid variants for non null tags.
			return new FluidVariantImpl(fluid, nbt);
		}
	}

	private static final Logger LOGGER = LoggerFactory.getLogger("fabric-transfer-api-v1/fluid");

	private final Fluid fluid;
	private final @Nullable NbtCompound nbt;
	private final int hashCode;

	public FluidVariantImpl(Fluid fluid, NbtCompound nbt) {
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
	public @Nullable NbtCompound getNbt() {
		return nbt;
	}

	@Override
	public NbtCompound toNbt() {
		NbtCompound result = new NbtCompound();
		result.putString("fluid", Registry.FLUID.getId(fluid).toString());

		if (nbt != null) {
			result.put("tag", nbt.copy());
		}

		return result;
	}

	public static FluidVariant fromNbt(NbtCompound compound) {
		try {
			Fluid fluid = Registry.FLUID.get(new Identifier(compound.getString("fluid")));
			NbtCompound nbt = compound.contains("tag") ? compound.getCompound("tag") : null;
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
			buf.writeNbt(nbt);
		}
	}

	public static FluidVariant fromPacket(PacketByteBuf buf) {
		if (!buf.readBoolean()) {
			return FluidVariant.blank();
		} else {
			Fluid fluid = Registry.FLUID.get(buf.readVarInt());
			NbtCompound nbt = buf.readNbt();
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
