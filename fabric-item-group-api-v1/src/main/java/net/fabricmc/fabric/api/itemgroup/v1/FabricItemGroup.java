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

import java.util.Objects;

import net.minecraft.item.ItemGroup;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.impl.itemgroup.ItemGroupHelper;

public abstract class FabricItemGroup extends ItemGroup implements IdentifiableItemGroup {
	private final Identifier identifier;

	public FabricItemGroup(Identifier identifier) {
		super(-1, getText(Objects.requireNonNull(identifier, "identifier")));
		this.identifier = identifier;
		ItemGroupHelper.appendItemGroup(this);
	}

	private static Text getText(Identifier identifier) {
		return Text.translatable("itemGroup.%s.%s".formatted(identifier.getNamespace(), identifier.getPath()));
	}

	@Override
	public Identifier getId() {
		return identifier;
	}
}
