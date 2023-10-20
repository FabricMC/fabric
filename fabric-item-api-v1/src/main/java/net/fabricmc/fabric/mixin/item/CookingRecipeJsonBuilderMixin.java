package net.fabricmc.fabric.mixin.item;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.data.server.recipe.CookingRecipeJsonBuilder;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;

@Mixin(CookingRecipeJsonBuilder.class)
class CookingRecipeJsonBuilderMixin {
	@Redirect(method = "getSmeltingRecipeCategory", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item;isFood()Z"))
	private static boolean isStackAwareFood(Item instance) {
		return instance.getDefaultStack().isFood();
	}
}
