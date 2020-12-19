package net.fabricmc.fabric.mixin.tags.extension.client.totem;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.tags.v1.FabricItemTags;

@Mixin(ClientPlayNetworkHandler.class)
@Environment(EnvType.CLIENT)
public abstract class ClientPlayNetworkHandlerMixin {
	@Redirect(
			method = "getActiveTotemOfUndying",
			at = @At(
				value = "INVOKE",
				target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;"
			)
	)
	private static Item getActiveTotemOfUndying$getItem(ItemStack stack) {
		return FabricItemTags.TOTEMS_OF_UNDYING.contains(stack.getItem()) ? Items.TOTEM_OF_UNDYING : Items.AIR;
	}
}
