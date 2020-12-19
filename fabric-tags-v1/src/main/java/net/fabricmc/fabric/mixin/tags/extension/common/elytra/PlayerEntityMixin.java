package net.fabricmc.fabric.mixin.tags.extension.common.elytra;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import net.fabricmc.fabric.api.tags.v1.FabricItemTags;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {
	@Redirect(
			method = "checkFallFlying",
			at = @At(
				value = "INVOKE",
				target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;"
			)
	)
	private Item checkFallFlying$getItem(ItemStack stack) {
		return FabricItemTags.ELYTRA.contains(stack.getItem()) ? Items.ELYTRA : Items.AIR;
	}
}
