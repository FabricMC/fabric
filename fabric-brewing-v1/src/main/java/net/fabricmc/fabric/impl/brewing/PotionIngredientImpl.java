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

import net.fabricmc.fabric.api.brewing.PotionIngredient;
import net.fabricmc.fabric.api.brewing.PotionTypeRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.registry.Registry;

public class PotionIngredientImpl implements PotionIngredient {
	private final Potion potion;
	private final Ingredient delegate;
	private final Identifier type;

	public PotionIngredientImpl(Potion potion, Identifier type) {
		this.potion = potion;
		this.type = type;
		this.delegate = null;
	}

	public PotionIngredientImpl(Ingredient delegate) {
		this.potion = null;
		this.type = null;
		this.delegate = delegate;
	}

	public boolean isIngredient() { return delegate != null; }
	public boolean isPotion() { return potion != null; }

	public Ingredient asIngredient() {
		return this.isIngredient() ? delegate
			: Ingredient.ofStacks(PotionUtil.setPotion(new ItemStack(Items.POTION), potion)); }
	public Potion asPotion() { return potion; }
	public Identifier getPotionType() { return type; }

	public boolean test(ItemStack stack) {
		return this.isIngredient() ? delegate.test(stack) :
			PotionUtil.getPotion(stack) == potion &&
				stack.getItem() == PotionTypeRegistry.INSTANCE.getItem(getPotionType()); }

	public void write(PacketByteBuf buf) {
		buf.writeBoolean(this.isPotion());
		if(!this.isPotion()) this.delegate.write(buf);
		else {
			buf.writeString(Registry.POTION.getId(this.potion).toString());
			buf.writeString(type.toString().toLowerCase());
		}
	}
}
