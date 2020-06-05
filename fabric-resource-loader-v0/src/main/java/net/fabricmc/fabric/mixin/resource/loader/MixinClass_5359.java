package net.fabricmc.fabric.mixin.resource.loader;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.class_5359;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

@Mixin(class_5359.class)
public class MixinClass_5359 {
	@Shadow
	@Final
	@Mutable
	private List<String> field_25395;

	/*
	This injection takes all instances of this class with an enabled list that only have the vanilla pack enabled,
	and forcibly enables all mod resource packs. This is probably not the best option, but it's the only one that I can
	think of that will work on both existing and new worlds. Is there a better option?
	 */
	@Inject(method = "<init>", at = @At("RETURN"))
	private void init(List<String> enabled, List<String> disabled, CallbackInfo info) {
		if (enabled.size() == 1 && enabled.get(0).equals("vanilla")) {
			List<String> newEnabled = new ArrayList<>(enabled);

			for (ModContainer container : FabricLoader.getInstance().getAllMods()) {
				if (!container.getMetadata().getType().equals("builtin")) {
					newEnabled.add("fabric/" + container.getMetadata().getId());
				}
			}

			this.field_25395 = newEnabled;
		}
	}
}
