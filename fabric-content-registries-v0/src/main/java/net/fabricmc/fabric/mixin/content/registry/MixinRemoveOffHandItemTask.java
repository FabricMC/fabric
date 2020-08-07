package net.fabricmc.fabric.mixin.content.registry;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.entity.ai.brain.task.RemoveOffHandItemTask;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import net.fabricmc.fabric.api.registry.ShieldRegistry;

@Mixin(RemoveOffHandItemTask.class)
public abstract class MixinRemoveOffHandItemTask {
	/**
	 * Piglins should not drop modded shields either.
	 */
	@Redirect(method = "shouldRun", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;"))
	private Item dontDropShields(ItemStack stack) {
		if (stack.getItem() == Items.SHIELD || ShieldRegistry.INSTANCE.isShield(stack.getItem())) {
			return Items.SHIELD;
		}

		return stack.getItem();
	}
}
