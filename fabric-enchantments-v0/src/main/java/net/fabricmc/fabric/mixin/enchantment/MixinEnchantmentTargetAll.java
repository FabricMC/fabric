package net.fabricmc.fabric.mixin.enchantment;

import net.fabricmc.fabric.api.enchantment.FabricEnchantmentTarget;
import net.fabricmc.fabric.impl.enchantment.EnchantmentTargetRegistryImpl;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net/minecraft/enchantment/EnchantmentTarget$1")
public class MixinEnchantmentTargetAll {
	@Inject(method = "isAcceptableItem", at = @At("TAIL"), cancellable = true)
	public void isAcceptableItem(Item item, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
		for(FabricEnchantmentTarget enchantmentTarget : EnchantmentTargetRegistryImpl.ENCHANTMENT_TARGETS) {
			if(enchantmentTarget.isAcceptableItem(item)) {
				callbackInfoReturnable.setReturnValue(true);
				return;
			}
		}
	}
}
