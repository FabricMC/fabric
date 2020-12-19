package net.fabricmc.fabric.mixin.tags.extension.common.totem;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import net.fabricmc.fabric.api.tags.v1.FabricItemTags;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
	// Required because @Redirect doesn't support locals
	private ItemStack fabric_tags$stack;

	@Redirect(
			method = "tryUseTotem",
			at = @At(
				value = "INVOKE",
				target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;"
			)
	)
	private Item tryUseTotem$getItem(ItemStack stack) {
		fabric_tags$stack = stack;
		return FabricItemTags.TOTEMS_OF_UNDYING.contains(stack.getItem()) ? Items.TOTEM_OF_UNDYING : Items.AIR;
	}

	@ModifyArg(
			method = "tryUseTotem",
			at = @At(
				value = "INVOKE",
				target = "Lnet/minecraft/stat/StatType;getOrCreateStat(Ljava/lang/Object;)Lnet/minecraft/stat/Stat;"
			),
			index = 0
	)
	private Object tryUseTotem$getTotemItem(Object totem) {
		return fabric_tags$stack.getItem();
	}
}
