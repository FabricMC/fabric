package net.fabricmc.fabric.api.fluids.v1.properties;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.fluids.v1.minecraft.properties.CustomPotionColorProperty;
import net.fabricmc.fabric.api.fluids.v1.minecraft.properties.CustomPotionEffectsFluidProperty;
import net.fabricmc.fabric.api.fluids.v1.minecraft.properties.PotionFluidProperty;
import net.fabricmc.fabric.api.fluids.v1.minecraft.properties.SuspiciousStewEffectsProperty;
import net.fabricmc.fabric.api.util.NbtType;

public class FluidPropertyMerger {
	public static final FluidPropertyMerger INSTANCE = new FluidPropertyMerger();
	@SuppressWarnings ("rawtypes") private final Map<NbtIdentifier, FluidProperty> data = new HashMap<>();

	public FluidPropertyMerger() {
		this.register(new NbtIdentifier("CustomPotionEffects", NbtType.LIST), new CustomPotionEffectsFluidProperty());
		this.register(new NbtIdentifier("Potion", NbtType.STRING), new PotionFluidProperty());
		this.register(new NbtIdentifier("CustomPotionColor", NbtType.INT), new CustomPotionColorProperty());
		this.register(new NbtIdentifier("Effects", NbtType.LIST), new SuspiciousStewEffectsProperty());
	}

	public void register(NbtIdentifier identifier, FluidProperty<?> property) {
		this.data.put(identifier, property);
	}

	public boolean has(NbtIdentifier identifier) {
		return this.data.containsKey(identifier);
	}

	public CompoundTag merge(Identifier fluid, CompoundTag aData, long aAmount, CompoundTag bData, long bAmount) {
		if (aData == null) return bData == null ? null : bData.copy();
		if (bData == null) return aData.copy();

		HashSet<String> iterate = new HashSet<>(aData.getKeys());
		iterate.addAll(bData.getKeys());
		CompoundTag merged = new CompoundTag();

		for (String key : iterate) {
			Tag aType = aData.get(key);
			Tag bType = bData.get(key);

			if (type(aType) > type(bType)) {
				merged.put(key, aType.copy());
			} else if (type(aType) < type(bType)) {
				merged.put(key, bType.copy());
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
