/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.fabricmc.fabric.mixin.brewing;

import net.fabricmc.fabric.impl.brewing.BrewingRecipe;
import net.minecraft.container.Slot;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = {
	"net.minecraft.container.BrewingStandContainer$SlotPotion",
	"net.minecraft.container.BrewingStandContainer$SlotIngredient"
})
public abstract class MixinBrewingStandSlot extends Slot {
    MixinBrewingStandSlot(Inventory inv, int id, int x, int y) { super(inv, id, x, y); }

    @Inject(method = "canInsert(Lnet/minecraft/item/ItemStack;)Z", at = @At("HEAD"), cancellable = true)
    private void checkCanInsert(ItemStack stack, CallbackInfoReturnable<Boolean> cbi) {
        if(!BrewingRecipe.getRelevantRecipes(inventory, stack, id).isEmpty()) cbi.setReturnValue(true);
    }
}
