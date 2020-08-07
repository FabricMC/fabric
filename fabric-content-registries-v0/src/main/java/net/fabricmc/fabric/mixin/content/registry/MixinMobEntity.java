package net.fabricmc.fabric.mixin.content.registry;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.ItemCooldownManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import net.fabricmc.fabric.api.registry.ShieldRegistry;

@Mixin(MobEntity.class)
public abstract class MixinMobEntity {
	/**
	 * Also disable modded shields.
	 */
	@Redirect(method = "disablePlayerShield", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;"))
	private Item disableFabricShields(ItemStack itemStack) {
		if (itemStack.getItem() == Items.SHIELD || ShieldRegistry.INSTANCE.isShield(itemStack.getItem())) {
			return Items.SHIELD;
		}

		return itemStack.getItem();
	}

	/**
	 * Add cooldown for the modded shield instead of the vanilla one.
	 */
	@Redirect(method = "disablePlayerShield", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/ItemCooldownManager;set(Lnet/minecraft/item/Item;I)V"))
	private void setCooldownForShields(ItemCooldownManager cooldownManager, Item item, int duration, PlayerEntity player, ItemStack mobStack, ItemStack playerStack) {
		if (playerStack.getItem() == Items.SHIELD) {
			cooldownManager.set(item, duration);
		}

		if (ShieldRegistry.INSTANCE.isShield(playerStack.getItem())) {
			cooldownManager.set(playerStack.getItem(), 100);
		}
	}

	/**
	 * Sets the preferred equipment slot for modded shields to offhand.
	 */
	@Inject(method = "getPreferredEquipmentSlot", at = @At("HEAD"), cancellable = true)
	private static void addPreferredShieldsSlot(ItemStack stack, CallbackInfoReturnable<EquipmentSlot> info) {
		if (ShieldRegistry.INSTANCE.isShield(stack.getItem())) {
			info.setReturnValue(EquipmentSlot.OFFHAND);
		}
	}
}
