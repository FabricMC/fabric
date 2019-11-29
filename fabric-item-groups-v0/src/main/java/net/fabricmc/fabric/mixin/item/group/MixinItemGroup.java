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

package net.fabricmc.fabric.mixin.item.group;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.item.ItemGroup;

import net.fabricmc.fabric.impl.item.group.ItemGroupExtensions;

@Mixin(ItemGroup.class)
public abstract class MixinItemGroup implements ItemGroupExtensions {
	@Shadow
	@Final
	@Mutable
	public static ItemGroup[] GROUPS;

	@Override
	public void fabric_expandArray() {
		ItemGroup[] tempGroups = GROUPS;
		GROUPS = new ItemGroup[GROUPS.length + 1];

		for (int i = 0; i < tempGroups.length; i++) {
			GROUPS[i] = tempGroups[i];
		}
	}
}
