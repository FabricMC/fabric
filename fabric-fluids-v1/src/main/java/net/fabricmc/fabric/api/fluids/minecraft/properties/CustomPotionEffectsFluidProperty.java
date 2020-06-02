package net.fabricmc.fabric.api.fluids.minecraft.properties;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.fluids.minecraft.FluidIds;
import net.fabricmc.fabric.api.fluids.properties.FluidProperty;

public class CustomPotionEffectsFluidProperty implements FluidProperty<ListTag> {
	@Override
	public boolean areCompatible(Identifier fluid, ListTag aData, long aAmount, ListTag bData, long bAmount) {
		if (FluidIds.POTION.equals(fluid)) {
			IntSet set = new IntOpenHashSet();

			for (Tag tag : aData) {
				CompoundTag effect = (CompoundTag) tag;
				set.add(effect.getInt("Id"));
			}

			for (Tag tag : bData) {
				CompoundTag effect = (CompoundTag) tag;

				if (set.contains(effect.getInt("Id"))) {
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

		for (Tag tag : aData) {
			// todo merge status lengths and other attributes?
			tags.add(tag.copy());
		}

		return tags;
	}
}
