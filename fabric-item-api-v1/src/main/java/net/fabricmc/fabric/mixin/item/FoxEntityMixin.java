package net.fabricmc.fabric.mixin.item;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

@Mixin(FoxEntity.class)
abstract class FoxEntityMixin extends AnimalEntity {
	protected FoxEntityMixin(EntityType<? extends AnimalEntity> entityType, World world) {
		super(entityType, world);
	}

	@Redirect(method = {"canEat", "canPickupItem"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item;isFood()Z",ordinal = 0))
	private boolean isStackAwareFood(Item instance, ItemStack stack) {
		return stack.isFood();
	}
	@Redirect(method = {"canPickupItem"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item;isFood()Z",ordinal = 1))
	private boolean isEquippedStackAwareFood(Item instance) {
		return this.getEquippedStack(EquipmentSlot.MAINHAND).isFood();
	}
}
