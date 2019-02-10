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

import net.fabricmc.fabric.api.loot.FabricLootSupplier;
import net.minecraft.world.loot.LootPool;
import net.minecraft.world.loot.LootSupplier;
import net.minecraft.world.loot.context.LootContextType;
import net.minecraft.world.loot.function.LootFunction;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Arrays;
import java.util.List;

@Mixin(LootSupplier.class)
public abstract class MixinLootSupplier implements FabricLootSupplier {
	@Shadow @Final private LootPool[] pools;
	@Shadow @Final private LootFunction[] functions;

	@Override
	public List<LootPool> getPools() {
		return Arrays.asList(pools);
	}

	@Override
	public List<LootFunction> getFunctions() {
		return Arrays.asList(functions);
	}

	@Accessor
	@Override
	public abstract LootContextType getType();
}
