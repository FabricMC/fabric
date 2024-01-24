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

import com.google.common.collect.ImmutableList;

import net.fabricmc.fabric.impl.item.ItemPredicateExtensions;
import net.fabricmc.fabric.impl.item.ShearsItemPredicate;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.item.Items;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.MatchToolLootCondition;
import net.minecraft.predicate.item.ItemPredicate;

import net.fabricmc.fabric.api.item.v1.CustomItemPredicate;

@Mixin(MatchToolLootCondition.class)
public abstract class MatchToolLootConditionMixin implements LootCondition {
	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	@Shadow
	@Final
	private Optional<ItemPredicate> predicate;

	@SuppressWarnings("deprecation")
	@Inject(at = @At("RETURN"), method = "<init>")
	private void shearsLoot(CallbackInfo ci) {
		// allows anything in fabric:shears to mine grass (and other stuff) and it will drop
		// the list will later be filtered to only contain the ones that have shears
		if (this.predicate.isEmpty()) {
			return;
		}

		ItemPredicate predicate = this.predicate.get();
		if (predicate.items().isPresent() && predicate.items().get().contains(Items.SHEARS.getRegistryEntry())) {
			CustomItemPredicate[] customPredicates = predicate.custom().toArray(new CustomItemPredicate[0]);
			((ItemPredicateExtensions) (Object) predicate).fabric_setCustom(ImmutableList.<CustomItemPredicate>builderWithExpectedSize(customPredicates.length + 1).add(customPredicates).add(ShearsItemPredicate.INSTANCE).build());
		}
	}
}
