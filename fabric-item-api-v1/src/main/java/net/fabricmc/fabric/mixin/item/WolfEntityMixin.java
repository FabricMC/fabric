package net.fabricmc.fabric.mixin.item;

import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(WolfEntity.class)
class WolfEntityMixin {

    @Redirect(method = "interactMob", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item;getFoodComponent()Lnet/minecraft/item/FoodComponent;"))
    private @Nullable FoodComponent getStackAwareFoodComponent(Item instance, PlayerEntity player, Hand hand) {
        return player.getStackInHand(hand).getFoodComponent();
    }

    @Redirect(method = "isBreedingItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item;getFoodComponent()Lnet/minecraft/item/FoodComponent;"))
    private @Nullable FoodComponent getStackAwareFoodComponent(Item instance, ItemStack stack) {
        return stack.getFoodComponent();
    }

}
