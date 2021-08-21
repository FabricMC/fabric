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

import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;
import java.util.function.Consumer;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import net.minecraft.advancement.Advancement;
import net.minecraft.data.DataCache;
import net.minecraft.data.DataProvider;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.resource.conditions.v1.ConditionJsonProvider;
import net.fabricmc.fabric.impl.datagen.FabricDataGenHelper;

/**
 * Extend this class and implement {@link FabricAdvancementsProvider#generateAdvancement}.
 *
 * <p>Register an instance of the class with {@link FabricDataGenerator#addProvider} in a {@link net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint}
 */
public abstract class FabricAdvancementsProvider implements DataProvider {
	private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();

	protected final FabricDataGenerator dataGenerator;

	protected FabricAdvancementsProvider(FabricDataGenerator dataGenerator) {
		this.dataGenerator = dataGenerator;
	}

	/**
	 * Implement this method to register advancements to generate use the consumer callback to register advancements.
	 *
	 * <p>Use {@link Advancement.Task#build(Consumer, String)} to help build advancements.
	 */
	public abstract void generateAdvancement(Consumer<Advancement> consumer);

	/**
	 * Return a new exporter that applies the specified conditions to any advancement it receives.
	 */
	protected Consumer<Advancement> withConditions(Consumer<Advancement> exporter, ConditionJsonProvider... conditions) {
		Preconditions.checkArgument(conditions.length > 0, "Must add at least one condition.");
		return advancement -> {
			FabricDataGenHelper.addConditions(advancement, conditions);
			exporter.accept(advancement);
		};
	}

	@Override
	public void run(DataCache cache) throws IOException {
		final Set<Identifier> identifiers = Sets.newHashSet();
		final Set<Advancement> advancements = Sets.newHashSet();

		generateAdvancement(advancements::add);

		for (Advancement advancement : advancements) {
			if (!identifiers.add(advancement.getId())) {
				throw new IllegalStateException("Duplicate advancement " + advancement.getId());
			}

			JsonObject advancementJson = advancement.createTask().toJson();
			ConditionJsonProvider.write(advancementJson, FabricDataGenHelper.consumeConditions(advancement));

			DataProvider.writeToPath(GSON, cache, advancementJson, getOutputPath(advancement));
		}
	}

	private Path getOutputPath(Advancement advancement) {
		return dataGenerator.getOutput().resolve("data/%s/advancements/%s.json".formatted(advancement.getId().getNamespace(), advancement.getId().getPath()));
	}

	@Override
	public String getName() {
		return "Advancements";
	}
}
