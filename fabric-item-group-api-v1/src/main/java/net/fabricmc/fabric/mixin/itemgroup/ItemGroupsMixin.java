package net.fabricmc.fabric.mixin.itemgroup;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;

@Mixin(ItemGroups.class)
public class ItemGroupsMixin {
	@Inject(method = "asArray", at = @At("HEAD"))
	private static void asArray(ItemGroup[] groups, CallbackInfoReturnable<ItemGroup[]> cir) {
		// Ensure that all item groups have a none null id.
		for (ItemGroup group : groups) {
			if (group.getId() == null) {
				throw new NullPointerException("Item group %s has a null id".formatted(group.getClass()));
			}
		}
	}
}
