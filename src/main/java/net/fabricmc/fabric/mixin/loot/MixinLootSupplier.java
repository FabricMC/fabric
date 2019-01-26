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

import net.fabricmc.fabric.impl.loot.LootSupplierHooks;
import net.minecraft.world.loot.LootPool;
import net.minecraft.world.loot.LootSupplier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(LootSupplier.class)
public class MixinLootSupplier implements LootSupplierHooks {
	@Shadow @Final @Mutable private LootPool[] pools;

	@Override
	public void fabric_addPools(LootPool[] pools) {
		LootPool[] oldPools = this.pools;
		this.pools = new LootPool[oldPools.length + pools.length];
		System.arraycopy(oldPools, 0, this.pools, 0, oldPools.length);
		System.arraycopy(pools, 0, this.pools, oldPools.length, pools.length);
	}
}
