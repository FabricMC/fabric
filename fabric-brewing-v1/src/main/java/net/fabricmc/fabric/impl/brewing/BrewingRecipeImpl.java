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

package net.fabricmc.fabric.impl.brewing;

import net.fabricmc.fabric.api.brewing.BrewingRecipe;
import net.fabricmc.fabric.api.brewing.PotionIngredient;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class BrewingRecipeImpl implements BrewingRecipe {
    private final Identifier id;
    private final String group;
    private final PotionIngredient input;
    private final PotionIngredient basePotion;
    private final PotionIngredient output;
    private List<Integer> matchedSlots = new ArrayList<>();

    BrewingRecipeImpl(Identifier id, String group, PotionIngredient input, PotionIngredient basePotion, PotionIngredient output) {
        this.id = id;
        this.group = group;
        this.input = input;
        this.basePotion = basePotion;
        this.output = output;
    }

    public boolean matches(Inventory inv, World world) {
        boolean matchesPotion = false;
        if(basePotion.test(inv.getInvStack(0))) { matchesPotion = true; this.matchedSlots.add(0); };
        if(basePotion.test(inv.getInvStack(1))) { matchesPotion = true; this.matchedSlots.add(1); };
        if(basePotion.test(inv.getInvStack(2))) { matchesPotion = true; this.matchedSlots.add(2); };
        return input.test(inv.getInvStack(3)) && matchesPotion;
    }

    public ItemStack craft(Inventory inv) { return output.asIngredient().getStackArray()[0].copy(); }
    public boolean fits(int w, int h) { return true; }
    public ItemStack getOutput() { return output.asIngredient().getStackArray()[0]; }
    public RecipeType<?> getType() { return FabricBrewingInit.BREWING_RECIPE_TYPE; }
    public Identifier getId() { return id; }
    public RecipeSerializer<?> getSerializer() { return FabricBrewingInit.BREWING_RECIPE_SERIALIZER; }
    public String getGroup() { return group; }

	public PotionIngredient getInput() { return input; }
	public PotionIngredient getBasePotion() { return basePotion; }
    public PotionIngredient getOutputPotion() { return output; }
    public List<Integer> getMatchedSlots() { return matchedSlots; }

    public DefaultedList<Ingredient> getPreviewInputs() {
        DefaultedList<Ingredient> list = DefaultedList.create();
        list.add(input.asIngredient());
        list.add(basePotion.asIngredient());
        return list;
    }
}
