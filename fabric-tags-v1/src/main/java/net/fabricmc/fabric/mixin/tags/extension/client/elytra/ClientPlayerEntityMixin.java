package net.fabricmc.fabric.mixin.tags.extension.client.elytra;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.tags.v1.FabricItemTags;

@Mixin(ClientPlayerEntity.class)
@Environment(EnvType.CLIENT)
public abstract class ClientPlayerEntityMixin {
	@Redirect(
			method = "tickMovement",
			at = @At(
				value = "INVOKE",
				target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;"
			)
	)
	private Item tickMovement$getItem(ItemStack stack) {
		return FabricItemTags.ELYTRA.contains(stack.getItem()) ? Items.ELYTRA : Items.AIR;
	}
}
