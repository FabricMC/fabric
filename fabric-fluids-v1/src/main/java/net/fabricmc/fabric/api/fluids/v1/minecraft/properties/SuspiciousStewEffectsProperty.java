package net.fabricmc.fabric.api.fluids.v1.minecraft.properties;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.fluids.v1.minecraft.FluidIds;
import net.fabricmc.fabric.api.fluids.v1.properties.FluidProperty;

public class SuspiciousStewEffectsProperty implements FluidProperty<ListTag> {
	@Override
	public boolean areCompatible(Identifier fluid, ListTag aData, long aAmount, ListTag bData, long bAmount) {
		if (fluid.equals(FluidIds.SUSPICIOUS_STEW)) {
			IntSet ids = new IntOpenHashSet();

			for (Tag datum : aData) {
				CompoundTag tag = (CompoundTag) datum;
				ids.add(tag.getInt("EffectId"));
			}

			for (Tag datum : bData) {
				if (ids.contains(((CompoundTag) datum).getInt("EffectId"))) {
					return false;
				}
			}

			return true;
		}
		return false;
	}

	@Override
	public ListTag merge(Identifier fluid, ListTag aData, long aAmount, ListTag bData, long bAmount) {
		ListTag tags = aData.copy();
		tags.addAll(bData.copy());
		return tags;
	}
}
