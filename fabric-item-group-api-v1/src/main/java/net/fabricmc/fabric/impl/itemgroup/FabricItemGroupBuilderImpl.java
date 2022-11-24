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

package net.fabricmc.fabric.impl.itemgroup;

import java.util.Objects;

import org.jetbrains.annotations.ApiStatus;

import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;

@ApiStatus.Internal
public final class FabricItemGroupBuilderImpl extends ItemGroup.Builder {
	private final Identifier identifier;

	public FabricItemGroupBuilderImpl(Identifier identifier) {
		// Set when building.
		super(null, -1);
		this.identifier = Objects.requireNonNull(identifier);
	}

	@Override
	public ItemGroup build() {
		final ItemGroup itemGroup = super.build();
		final FabricItemGroup fabricItemGroup = (FabricItemGroup) itemGroup;
		fabricItemGroup.setId(identifier);
		ItemGroupHelper.appendItemGroup(itemGroup);
		return itemGroup;
	}
}
