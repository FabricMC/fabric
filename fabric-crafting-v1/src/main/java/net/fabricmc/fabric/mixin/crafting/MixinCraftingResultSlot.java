package net.fabricmc.fabric.mixin.crafting;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.container.CraftingResultSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DefaultedList;

import net.fabricmc.fabric.api.crafting.v1.FabricRecipeRemainder;
import net.fabricmc.fabric.api.crafting.v1.ItemCraftCallback;

@Mixin(CraftingResultSlot.class)
public abstract class MixinCraftingResultSlot {
	@Shadow
	@Final
	private CraftingInventory craftingInv;

	@Shadow
	@Final
	private PlayerEntity player;

	@ModifyVariable(method = "onTakeItem", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/recipe/RecipeManager;getRemainingStacks(Lnet/minecraft/recipe/RecipeType;Lnet/minecraft/inventory/Inventory;Lnet/minecraft/world/World;)Lnet/minecraft/util/DefaultedList;"))
	private DefaultedList<ItemStack> defaultedList(DefaultedList<ItemStack> list) {
		for (int i = 0; i < craftingInv.getInvSize(); i++) {
			ItemStack invStack = craftingInv.getInvStack(i);

			if (invStack.getItem() instanceof FabricRecipeRemainder) {
				ItemStack remainder = ((FabricRecipeRemainder) invStack.getItem()).getRemainder(invStack.copy(), craftingInv, player);
				list.set(i, remainder);
			}
		}

		return list;
	}

	@Inject(method = "onCrafted(Lnet/minecraft/item/ItemStack;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;onCraft(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/PlayerEntity;I)V", shift = At.Shift.AFTER))
	private void onCrafted(ItemStack itemStack, CallbackInfo info) {
		ItemCraftCallback.EVENT.invoker().onCraft(itemStack, craftingInv, player);
	}
}
