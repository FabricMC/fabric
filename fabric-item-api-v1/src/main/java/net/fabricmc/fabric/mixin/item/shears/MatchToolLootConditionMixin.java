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

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.MatchToolLootCondition;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.registry.entry.RegistryEntry;

import net.fabricmc.fabric.api.event.lifecycle.v1.CommonLifecycleEvents;
import net.fabricmc.fabric.impl.item.ShearsHelper;
import net.fabricmc.fabric.mixin.item.shears.accessors.DirectRegistryEntryListAccessor;

@Mixin(MatchToolLootCondition.class)
public abstract class MatchToolLootConditionMixin implements LootCondition {
	@Unique
	private static final List<ItemPredicate> MATCH_TOOL_PREDICATES = new ArrayList<>();

	@Inject(at = @At("RETURN"), method = "<init>")
	private void shearsLoot(CallbackInfo ci) {
		// allows anything in fabric:shears to mine grass (and other stuff) and it will drop
		((MatchToolLootCondition) (Object) this).predicate().ifPresent(MATCH_TOOL_PREDICATES::add);
	}

	static {
		// loot is loaded before tags, so this is required
		CommonLifecycleEvents.TAGS_LOADED.register((registries, client) -> {
			if (!client) {
				for (ItemPredicate p : MATCH_TOOL_PREDICATES) {
					//noinspection deprecation
					if (p.items().isPresent() && p.items().get().contains(Items.SHEARS.getRegistryEntry())) {
						@SuppressWarnings("unchecked")
						DirectRegistryEntryListAccessor<Item> accessor = ((DirectRegistryEntryListAccessor<Item>) p.items().get());
						ImmutableList.Builder<RegistryEntry<Item>> builder = new ImmutableList.Builder<>();
						builder.addAll(accessor.getEntries());
						builder.addAll(ShearsHelper.SHEARS);
						accessor.setEntries(builder.build());
						accessor.setEntrySet(null);
					}
				}
			}

			MATCH_TOOL_PREDICATES.clear();
		});
	}
}
