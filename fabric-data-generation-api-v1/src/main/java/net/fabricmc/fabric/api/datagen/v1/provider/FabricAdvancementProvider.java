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

package net.fabricmc.fabric.api.datagen.v1.provider;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import com.google.gson.JsonObject;

import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.resource.conditions.v1.ConditionJsonProvider;
import net.fabricmc.fabric.impl.datagen.FabricDataGenHelper;

/**
 * Extend this class and implement {@link FabricAdvancementProvider#generateAdvancement}.
 *
 * <p>Register an instance of the class with {@link FabricDataGenerator.Pack#addProvider} in a {@link net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint}.
 */
public abstract class FabricAdvancementProvider implements DataProvider {
	protected final FabricDataOutput output;
	private final DataOutput.PathResolver pathResolver;

	protected FabricAdvancementProvider(FabricDataOutput output) {
		this.output = output;
		this.pathResolver = output.getResolver(DataOutput.OutputType.DATA_PACK, "advancements");
	}

	/**
	 * Implement this method to register advancements to generate use the consumer callback to register advancements.
	 *
	 * <p>Use {@link Advancement.Builder#build(Consumer, String)} to help build advancements.
	 */
	public abstract void generateAdvancement(Consumer<AdvancementEntry> consumer);

	/**
	 * Return a new exporter that applies the specified conditions to any advancement it receives.
	 */
	protected Consumer<AdvancementEntry> withConditions(Consumer<AdvancementEntry> exporter, ConditionJsonProvider... conditions) {
		Preconditions.checkArgument(conditions.length > 0, "Must add at least one condition.");
		return advancement -> {
			FabricDataGenHelper.addConditions(advancement, conditions);
			exporter.accept(advancement);
		};
	}

	@Override
	public CompletableFuture<?> run(DataWriter writer) {
		final Set<Identifier> identifiers = Sets.newHashSet();
		final Set<AdvancementEntry> advancements = Sets.newHashSet();

		generateAdvancement(advancements::add);

		final List<CompletableFuture<?>> futures = new ArrayList<>();

		for (AdvancementEntry advancement : advancements) {
			if (!identifiers.add(advancement.id())) {
				throw new IllegalStateException("Duplicate advancement " + advancement.id());
			}

			JsonObject advancementJson = advancement.value().toJson();
			ConditionJsonProvider.write(advancementJson, FabricDataGenHelper.consumeConditions(advancement));

			futures.add(DataProvider.writeToPath(writer, advancementJson, getOutputPath(advancement)));
		}

		return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
	}

	private Path getOutputPath(AdvancementEntry advancement) {
		return pathResolver.resolveJson(advancement.id());
	}

	@Override
	public String getName() {
		return "Advancements";
	}
}
