package net.fabricmc.fabric.mixin.client.rendering;


import net.fabricmc.fabric.api.client.rendering.v1.TooltipDataCallback;
import net.fabricmc.fabric.impl.client.rendering.tooltip.MultiTooltipData;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.item.TooltipData;
import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Optional;

@Mixin(HandledScreen.class)
class HandledScreenMixin {
    @Redirect(method = "drawMouseoverTooltip",at = @At(value = "INVOKE",target = "Lnet/minecraft/item/ItemStack;getTooltipData()Ljava/util/Optional;"))
    Optional<TooltipData> addMultiData(ItemStack stack){
        var original = stack.getTooltipData();
        var mutlidata = new MultiTooltipData(1);
        original.ifPresent(mutlidata::add);
        TooltipDataCallback.EVENT.invoker().getTooltipData(stack,mutlidata);
		if(mutlidata.size() == 0){
			return Optional.empty();
		}
		return Optional.of(mutlidata);
    }
}
