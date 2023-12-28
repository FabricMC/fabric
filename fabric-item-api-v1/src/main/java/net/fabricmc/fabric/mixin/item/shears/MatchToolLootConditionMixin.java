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

package net.fabricmc.fabric.mixin.item.shears;

import java.util.Optional;

import net.fabricmc.fabric.impl.item.ShearsHelper;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.MatchToolLootCondition;
import net.minecraft.predicate.item.ItemPredicate;

@Mixin(MatchToolLootCondition.class)
public abstract class MatchToolLootConditionMixin implements LootCondition {
	@Shadow
	public abstract Optional<ItemPredicate> predicate();

	@Inject(at = @At("RETURN"), method = "<init>")
	private void shearsLoot(CallbackInfo ci) {
		// allows anything in fabric:shears to mine grass (and other stuff) and it will drop
		// the list will later be filtered to only contain the ones that have shears
		predicate().flatMap(ItemPredicate::items).ifPresent(ShearsHelper.MATCH_TOOL_REGISTRY_ENTRIES::add);
	}
}
