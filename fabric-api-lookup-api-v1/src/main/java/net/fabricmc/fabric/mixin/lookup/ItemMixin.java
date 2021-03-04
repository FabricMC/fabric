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

package net.fabricmc.fabric.mixin.lookup;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import net.minecraft.item.Item;

import net.fabricmc.fabric.impl.lookup.item.ItemItemKeyCache;
import net.fabricmc.fabric.impl.lookup.item.ItemKeyImpl;

@Mixin(Item.class)
public class ItemMixin implements ItemItemKeyCache {
	@Unique
	private volatile ItemKeyImpl fabric_cachedItemKey = null;

	@SuppressWarnings("ConstantConditions")
	@Override
	public ItemKeyImpl fabric_getOrCreateItemKey() {
		// We use a double-checked lock to lazily create and store ItemKeyImpl instances
		// while keeping thread-safety for the creation part
		// and lock-free retrieval once the cache has been initialized.
		// The volatile keyword ensures correct publication of created ItemKeyImpl instances under Java 1.5+.
		if (fabric_cachedItemKey != null) {
			return fabric_cachedItemKey;
		}

		synchronized (this) {
			if (fabric_cachedItemKey == null) {
				fabric_cachedItemKey = new ItemKeyImpl((Item) (Object) this, null);
			}

			return fabric_cachedItemKey;
		}
	}
}
