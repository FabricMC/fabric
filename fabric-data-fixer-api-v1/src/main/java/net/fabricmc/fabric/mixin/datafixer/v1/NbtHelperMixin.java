package net.fabricmc.fabric.mixin.datafixer.v1;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import net.fabricmc.fabric.impl.datafixer.v1.FabricDataFixesInternals;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(NbtHelper.class)
public class NbtHelperMixin {

	@ModifyReturnValue(method = "putDataVersion(Lnet/minecraft/nbt/NbtCompound;)Lnet/minecraft/nbt/NbtCompound;", at = @At("RETURN"))
	private static NbtCompound addModDataVersions(NbtCompound original) {
		return FabricDataFixesInternals.get().addModDataVersions(original);
	}
}
