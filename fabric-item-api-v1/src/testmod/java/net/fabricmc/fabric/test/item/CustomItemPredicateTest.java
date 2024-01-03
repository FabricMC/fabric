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

package net.fabricmc.fabric.test.item;

import java.util.function.Consumer;

import com.mojang.serialization.Codec;

import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.util.Identifier;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.fabricmc.fabric.api.item.v1.CustomItemPredicate;
import net.fabricmc.fabric.api.item.v1.CustomItemPredicateRegistry;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;

public class CustomItemPredicateTest implements ModInitializer, DataGeneratorEntrypoint {
	@Override
	public void onInitialize() {
		CustomItemPredicateRegistry.register(new Identifier("fabric-item-api-v1-testmod", "tier"), TierItemPredicate.CODEC);
		CustomItemPredicateRegistry.register(new Identifier("fabric-item-api-v1-testmod", "noop_1"), NoOpItemPredicate.INSTANCE_1.codec);
		CustomItemPredicateRegistry.register(new Identifier("fabric-item-api-v1-testmod", "noop_2"), NoOpItemPredicate.INSTANCE_2.codec);
	}

	@Override
	public void onInitializeDataGenerator(FabricDataGenerator generator) {
		FabricDataGenerator.Pack pack = generator.createPack();
		pack.addProvider(AdvancementProvider::new);
	}

	public static class TieredItem extends Item {
		private final int tier;

		public TieredItem(int tier) {
			super(new FabricItemSettings());
			this.tier = tier;
		}

		int getTier() {
			return tier;
		}
	}

	public enum NoOpItemPredicate implements CustomItemPredicate {
		INSTANCE_1, INSTANCE_2;

		final Codec<NoOpItemPredicate> codec = Codec.unit(this);

		@Override
		public boolean test(ItemStack stack) {
			return true;
		}

		@Override
		public Codec<? extends CustomItemPredicate> getCodec() {
			return codec;
		}
	}

	public record TierItemPredicate(int tier) implements CustomItemPredicate {
		static final Codec<TierItemPredicate> CODEC = Codec.INT.xmap(TierItemPredicate::new, TierItemPredicate::tier);

		@Override
		public boolean test(ItemStack stack) {
			return stack.getItem() instanceof TieredItem tiered && tiered.getTier() == tier;
		}

		@Override
		public Codec<? extends CustomItemPredicate> getCodec() {
			return CODEC;
		}
	}

	private static class AdvancementProvider extends FabricAdvancementProvider {
		AdvancementProvider(FabricDataOutput output) {
			super(output);
		}

		@Override
		public void generateAdvancement(Consumer<AdvancementEntry> consumer) {
			consumer.accept(Advancement.Builder.create()
					.criterion("with_custom", InventoryChangedCriterion.Conditions.items(ItemPredicate.Builder.create()
							.count(NumberRange.IntRange.atLeast(3))
							.custom(new TierItemPredicate(1), NoOpItemPredicate.INSTANCE_1, NoOpItemPredicate.INSTANCE_2)
							.build()))
					.build(new Identifier("fabric-item-api-v1-testmod", "with_custom")));

			consumer.accept(Advancement.Builder.create()
					.criterion("vanilla_only", InventoryChangedCriterion.Conditions.items(ItemPredicate.Builder.create()
							.count(NumberRange.IntRange.atLeast(3))
							.build()))
					.build(new Identifier("fabric-item-api-v1-testmod", "vanilla_only")));
		}
	}
}
