package net.fabricmc.fabric.mixin.item;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(AbstractFurnaceBlockEntity.class)
public class AbstractFurnaceBlockEntityMixin {
	private static DefaultedList<ItemStack> capturedInventory;

	@Inject(method = "tick", at = @At("HEAD"))
	private static void getStackCraftingRemainder(World world, BlockPos pos, BlockState state, AbstractFurnaceBlockEntity blockEntity, CallbackInfo ci) {
		capturedInventory = ((AbstractFurnaceBlockEntityAccessor)blockEntity).getInventory();
	}

	@Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isEmpty()Z", ordinal = 2))
	private static boolean setStackCraftingRemainder(ItemStack itemStack) {
		if (itemStack.getItem().hasRecipeRemainder(itemStack)) {
			capturedInventory.set(1, itemStack.getItem().getRecipeRemainder(itemStack));
			return true;
		}
		return itemStack.isEmpty();
	}
}
