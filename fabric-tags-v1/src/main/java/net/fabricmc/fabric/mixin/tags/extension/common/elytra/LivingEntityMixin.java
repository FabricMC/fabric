package net.fabricmc.fabric.mixin.tags.extension.common.elytra;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvents;

import net.fabricmc.fabric.api.tags.v1.FabricItemTags;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
	private LivingEntityMixin() {
		super(null, null);
	}

	@Inject(
			method = "onEquipStack",
			at = @At(
				value = "INVOKE",
				target = "Lnet/minecraft/entity/LivingEntity;playSound(Lnet/minecraft/sound/SoundEvent;FF)V"
			),
			cancellable = true
	)
	private void onEquipStack$playSound(ItemStack stack, CallbackInfo ci) {
		if (FabricItemTags.ELYTRA.contains(stack.getItem())) {
			playSound(SoundEvents.ITEM_ARMOR_EQUIP_ELYTRA, 1.0F, 1.0F);
			ci.cancel();
		}
	}

	@Redirect(
			method = "initAi",
			at = @At(
				value = "INVOKE",
				target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;"
			)
	)
	private Item initAi$getItem(ItemStack stack) {
		return FabricItemTags.ELYTRA.contains(stack.getItem()) ? Items.ELYTRA : Items.AIR;
	}
}
