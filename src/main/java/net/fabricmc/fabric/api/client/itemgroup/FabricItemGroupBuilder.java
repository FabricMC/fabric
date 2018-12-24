/*
 * Copyright (c) 2016, 2017, 2018 FabricMC
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

package net.fabricmc.fabric.api.client.itemgroup;

import net.fabricmc.fabric.client.itemgroup.ItemGroupExtensions;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.function.Supplier;

public final class FabricItemGroupBuilder {

	private  Supplier<ItemStack> stackSupplier = () -> ItemStack.EMPTY;

	public FabricItemGroupBuilder icon(Supplier<ItemStack> stackSupplier){
		this.stackSupplier = stackSupplier;
		return this;
	}

	public ItemGroup create(Identifier identifier){
		((ItemGroupExtensions)ItemGroup.BUILDING_BLOCKS).fabric_expandArray();
		return new ItemGroup(ItemGroup.GROUPS.length -1, identifier.toString()) {
			@Override
			public ItemStack getIconItem() {
				return stackSupplier.get();
			}
		};
	}


}
