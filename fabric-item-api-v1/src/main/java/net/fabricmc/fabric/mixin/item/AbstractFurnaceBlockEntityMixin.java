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

package net.fabricmc.fabric.mixin.item;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.collection.DefaultedList;

import net.fabricmc.fabric.impl.item.ItemExtensions;

@Mixin(AbstractFurnaceBlockEntity.class)
public abstract class AbstractFurnaceBlockEntityMixin extends BlockEntity implements SidedInventory {
	@Shadow
	protected DefaultedList<ItemStack> inventory;

	@Shadow
	@Final
	protected RecipeType<? extends AbstractCookingRecipe> recipeType;

	public AbstractFurnaceBlockEntityMixin(BlockEntityType<?> type) {
		super(type);
	}

	@Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item;getRecipeRemainder()Lnet/minecraft/item/Item;"), locals = LocalCapture.PRINT)
	public void setRemainder(CallbackInfo ci, boolean bl, boolean bl2, ItemStack itemStack, Recipe<?> recipe, Item item) {
		if (((ItemExtensions) item).fabric_getRecipeRemainderProvider() != null) {
			//noinspection ConstantConditions
			this.inventory.set(1, ((ItemExtensions) item).fabric_getRecipeRemainderProvider().getRecipeRemainder(itemStack, this, this.recipeType, this.world, this.pos));
		} else {
			Item recipeRemainder = item.getRecipeRemainder();
			this.inventory.set(1, recipeRemainder != null ? new ItemStack(recipeRemainder) : ItemStack.EMPTY);
		}
	}

	@Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/collection/DefaultedList;set(ILjava/lang/Object;)Ljava/lang/Object;"))
	public <E> E setRemainder(DefaultedList<E> defaultedList, int index, E element) {
		return element;
	}
}
