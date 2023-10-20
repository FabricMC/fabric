package net.fabricmc.fabric.mixin.item;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemUsageContext;

@Mixin(BlockItem.class)
class BlockItemMixin {
	@Redirect(method = "useOnBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/BlockItem;isFood()Z"))
	private boolean isStackAwareFood(BlockItem instance, ItemUsageContext context) {
		return context.getStack().isFood();
	}
}
