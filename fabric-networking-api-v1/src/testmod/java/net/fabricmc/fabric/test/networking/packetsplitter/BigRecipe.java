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

package net.fabricmc.fabric.test.networking.packetsplitter;

import com.google.gson.JsonObject;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class BigRecipe implements Recipe<CraftingInventory> {
	public static final Serializer SERIALIZER = new Serializer();

	private final Identifier id;

	public BigRecipe(Identifier id) {
		this.id = id;
	}

	@Override
	public boolean matches(CraftingInventory inventory, World world) {
		return false;
	}

	@Override
	public ItemStack craft(CraftingInventory inventory) {
		return ItemStack.EMPTY;
	}

	@Override
	public boolean fits(int width, int height) {
		return false;
	}

	@Override
	public ItemStack getOutput() {
		return ItemStack.EMPTY;
	}

	@Override
	public Identifier getId() {
		return id;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return SERIALIZER;
	}

	@Override
	public RecipeType<?> getType() {
		return RecipeType.CRAFTING;
	}

	public static class Serializer implements RecipeSerializer<BigRecipe> {
		@Override
		public BigRecipe read(Identifier id, JsonObject json) {
			return new BigRecipe(id);
		}

		@Override
		public BigRecipe read(Identifier id, PacketByteBuf buf) {
			buf.readBytes(0x1000000);
			return new BigRecipe(id);
		}

		@Override
		public void write(PacketByteBuf buf, BigRecipe recipe) {
			buf.writeBytes(new byte[0x1000000]);
		}
	}
}
