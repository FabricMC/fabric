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

package net.fabricmc.fabric.api.itemgroup.v1;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.impl.itemgroup.FabricItemGroupBuilderImpl;

public final class FabricItemGroup {
	private FabricItemGroup() {
	}

	/**
	 * Create a new builder for {@link ItemGroup}. Item groups are used to group items in the creative
	 * inventory.
	 *
	 * <p>Each new  {@link ItemGroup} instance of this class is automatically appended to {@link ItemGroups#getGroups()} when
	 * {@link ItemGroup.Builder#build()} is invoked.
	 *
	 * <p>Example:
	 *
	 * <pre>{@code
	 * private static final ItemGroup ITEM_GROUP = FabricItemGroup.builder(new Identifier(MOD_ID, "test_group"))
	 *    .icon(() -> new ItemStack(Items.DIAMOND))
	 *    .entries((enabledFeatures, entries, operatorEnabled) -> {
	 *       entries.add(TEST_ITEM);
	 *    })
	 *    .build();
	 * }</pre>
	 *
	 * @param identifier identifier the id of the ItemGroup, to be used as the default translation key
	 * @return a new {@link ItemGroup} instance
	 */
	public static ItemGroup.Builder builder(Identifier identifier) {
		return new FabricItemGroupBuilderImpl(identifier)
				.displayName(Text.translatable("itemGroup.%s.%s".formatted(identifier.getNamespace(), identifier.getPath())));
	}
}
