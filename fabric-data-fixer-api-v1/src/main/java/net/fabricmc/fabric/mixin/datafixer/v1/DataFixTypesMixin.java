package net.fabricmc.fabric.mixin.datafixer.v1;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import com.mojang.datafixers.DataFixer;
import com.mojang.serialization.Dynamic;

import net.fabricmc.fabric.impl.datafixer.v1.FabricDataFixesInternals;

import net.minecraft.datafixer.DataFixTypes;

import net.minecraft.nbt.NbtElement;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(DataFixTypes.class)
public class DataFixTypesMixin {
	// From QSL.
	@SuppressWarnings({"rawtypes", "unchecked"})
	@ModifyReturnValue(
			method = "update(Lcom/mojang/datafixers/DataFixer;Lcom/mojang/serialization/Dynamic;II)Lcom/mojang/serialization/Dynamic;",
			at = @At("RETURN")
	)
	private Dynamic updateDataWithFixers(Dynamic original, DataFixer fixer, Dynamic dynamic, int oldVersion, int targetVersion) {
		DataFixTypes type = DataFixTypes.class.cast(this);
		Object value = original.getValue();
		if (type != DataFixTypes.WORLD_GEN_SETTINGS && value instanceof NbtElement) {
			return FabricDataFixesInternals.get().updateWithAllFixers(type, (Dynamic<NbtElement>) original);
		}
		return original;
	}
}
