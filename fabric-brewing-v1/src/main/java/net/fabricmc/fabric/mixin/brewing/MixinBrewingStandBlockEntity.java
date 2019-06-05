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

import net.fabricmc.fabric.api.brewing.BrewingRecipe;
import net.fabricmc.fabric.api.brewing.BrewingRecipes;
import net.fabricmc.fabric.api.brewing.PotionTypeRegistry;
import net.fabricmc.fabric.impl.brewing.FabricBrewingInit;
import net.minecraft.block.entity.BrewingStandBlockEntity;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PotionItem;
import net.minecraft.potion.PotionUtil;
import net.minecraft.recipe.BrewingRecipeRegistry;
import net.minecraft.recipe.RecipeFinder;
import net.minecraft.recipe.RecipeInputProvider;
import net.minecraft.util.ItemScatterer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Mixin(BrewingStandBlockEntity.class)
public abstract class MixinBrewingStandBlockEntity extends LockableContainerBlockEntity implements RecipeInputProvider, SidedInventory {
    private MixinBrewingStandBlockEntity() { super(null); }

    public void provideRecipeInputs(RecipeFinder recipeFinder) {
        for(int i = 0; i < 4; i++) recipeFinder.addItem(getInvStack(i));
    }

    @Inject(method = "canCraft", at = @At("HEAD"), cancellable = true)
    private void checkCanCraft(CallbackInfoReturnable<Boolean> cbi) {
        Optional<BrewingRecipe> recipe = this.world.getRecipeManager().getFirstMatch(FabricBrewingInit.BREWING_RECIPE_TYPE, this, this.world);
        if(recipe.isPresent()) cbi.setReturnValue(true);
    }

    @Inject(method = "craft", at = @At("HEAD"), cancellable = true)
    private void doCraft(CallbackInfo cbi) {
        List<BrewingRecipe> recipes = this.world.getRecipeManager().getAllMatches(FabricBrewingInit.BREWING_RECIPE_TYPE, this, this.world);
        if(recipes.size() > 0) {
            List<Integer> matchedSlots = new ArrayList<>();

            for(BrewingRecipe recipe : recipes) {
                ItemStack output = recipe.getOutput();

                if(output.getItem() instanceof PotionItem) {
                    output = new ItemStack(PotionTypeRegistry.INSTANCE.getItem(recipe.getOutputPotion().getPotionType()));
                    PotionUtil.setPotion(output, PotionUtil.getPotion(recipe.getOutput()));
                }

                for(int slot : recipe.getMatchedSlots()) {
                    setInvStack(slot, output.copy());
                    matchedSlots.add(slot);
                }
            }

            for(int i = 0; i < 3; i++) if(!matchedSlots.contains(i))
                setInvStack(i, BrewingRecipeRegistry.craft(getInvStack(3), getInvStack(i)));

            Item remainder = getInvStack(3).getItem().getRecipeRemainder();
            getInvStack(3).subtractAmount(1);
            if(remainder != null) {
                if(getInvStack(3).isEmpty()) setInvStack(3, new ItemStack(remainder));
                else ItemScatterer.spawn(this.world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(remainder));
            }

            cbi.cancel();
        }
    }

    @Inject(method = "isValidInvStack", at = @At("HEAD"), cancellable = true)
    public void checkValidInvStack(int slot, ItemStack stack, CallbackInfoReturnable<Boolean> cbi) {
        if(!BrewingRecipes.INSTANCE.getRelevantRecipes(this.world, stack, slot == 3).isEmpty()) cbi.setReturnValue(true);
    }
}
