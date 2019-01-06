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

package net.fabricmc.fabric.mixin.loot;

import net.fabricmc.fabric.loot.FabricLootEntries;
import net.minecraft.item.Item;
import net.minecraft.tag.Tag;
import net.minecraft.world.loot.entry.TagEntry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(TagEntry.class)
public class MixinTagEntry implements FabricLootEntries.TagEntryDelegate {
	@Shadow
	@Final
	private Tag<Item> name;

	@Override
	public Tag<Item> fabric_getTag() {
		return name;
	}
}
