package net.fabricmc.fabric.mixin.event.interaction;

import net.fabricmc.fabric.api.event.player.ItemPickupEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ItemEntity.class)
public abstract class MixinItemEntity extends Entity {

	public MixinItemEntity(EntityType<?> type, World world) {
		super(type, world);
	}

	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerInventory;insertStack(Lnet/minecraft/item/ItemStack;)Z"), method = "onPlayerCollision")
	public boolean onPlayerCollision(PlayerInventory playerInventory, ItemStack stack) {
		ActionResult result = ItemPickupEvent.EVENT.invoker().interact(playerInventory.player, stack);

		return (result == ActionResult.SUCCESS && playerInventory.insertStack(stack));
	}



}
