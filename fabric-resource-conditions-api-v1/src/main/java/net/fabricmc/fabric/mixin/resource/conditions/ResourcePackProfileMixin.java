package net.fabricmc.fabric.mixin.resource.conditions;


import net.fabricmc.fabric.impl.resource.conditions.OverlayConditionsMetadata;

import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourcePackProfile;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.llamalad7.mixinextras.sugar.Local;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Mixin(ResourcePackProfile.class)
public class ResourcePackProfileMixin {
	@ModifyVariable(method = "loadMetadata", at = @At("STORE"))
	private static List<String> fabric_applyOverlayConditions(List<String> overlays, @Local ResourcePack resourcePack) throws IOException {
		List<String> appliedOverlays = new ArrayList<>(overlays);
		OverlayConditionsMetadata overlayConditionsMetadata = resourcePack.parseMetadata(OverlayConditionsMetadata.SERIALIZER);

		if (overlayConditionsMetadata != null) {
			appliedOverlays.addAll(overlayConditionsMetadata.getAppliedOverlays());
		}

		return appliedOverlays;
	}
}
