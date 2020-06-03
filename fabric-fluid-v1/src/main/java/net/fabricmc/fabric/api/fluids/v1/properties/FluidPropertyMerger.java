package net.fabricmc.fabric.api.fluids.v1.properties;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Sets;

import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

import net.fabricmc.fabric.api.util.NbtIdentifier;

public final class FluidPropertyMerger {
	public static final FluidPropertyMerger INSTANCE = new FluidPropertyMerger();
	@SuppressWarnings ("rawtypes") private final Map<NbtIdentifier, FluidProperty> data = new HashMap<>();

	private FluidPropertyMerger() {}

	public void register(NbtIdentifier identifier, FluidProperty<?> property) {
		this.data.put(identifier, property);
	}

	public boolean has(NbtIdentifier identifier) {
		return this.data.containsKey(identifier);
	}

	public CompoundTag merge(Fluid fluid, CompoundTag aData, long aAmount, CompoundTag bData, long bAmount) {
		if (aData == null) return bData == null ? null : bData.copy();
		if (bData == null) return aData.copy();

		Set<String> iterate = Sets.union(aData.getKeys(), bData.getKeys());
		CompoundTag merged = new CompoundTag();

		for (String key : iterate) {
			Tag aType = aData.get(key);
			Tag bType = bData.get(key);

			if (type(aType) > type(bType)) {
				merged.put(key, aType == null ? null : aType.copy());
			} else if (type(aType) < type(bType)) {
				merged.put(key, bType == null ? null : bType.copy());
			} else {
				NbtIdentifier identifier = new NbtIdentifier(key, aData.getType(key));
				Tag add = this.data.getOrDefault(identifier, FluidProperty.DEFAULT).merge(fluid, aType, aAmount, bType, bAmount);
				merged.put(key, add);
			}
		}

		return merged;
	}

	private static int type(Tag tag) {
		return tag == null ? 0 : tag.getType();
	}
}
