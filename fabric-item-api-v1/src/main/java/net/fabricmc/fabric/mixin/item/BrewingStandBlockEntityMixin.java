package net.fabricmc.fabric.mixin.item;

import net.minecraft.block.entity.BrewingStandBlockEntity;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(BrewingStandBlockEntity.class)
public class BrewingStandBlockEntityMixin {

	private static ItemStack capturedItemStack;

	@Inject(method = "craft", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;decrement(I)V"), locals = LocalCapture.CAPTURE_FAILHARD)
	private static void captureItemStack(World world, BlockPos pos, DefaultedList<ItemStack> slots, CallbackInfo ci, ItemStack itemStack) {
		capturedItemStack = itemStack;
	}

	@Redirect(method = "craft", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item;hasRecipeRemainder()Z"))
	private static boolean hasStackRecipeRemainder(Item instance) {
		return capturedItemStack.getItem().hasRecipeRemainder(capturedItemStack);
	}

	@ModifyVariable(method = "craft", at = @At(value = "STORE"), index = 4)
	private static ItemStack createStackRecipeRemainder(ItemStack old) {
		return capturedItemStack.getItem().getRecipeRemainder(capturedItemStack);
	}
}
