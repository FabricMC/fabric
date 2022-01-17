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

package net.fabricmc.fabric.api.resource.conditions.v1;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.minecraft.block.Block;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;

import net.fabricmc.fabric.impl.resource.conditions.ResourceConditionsImpl;

/**
 * Contains {@link ConditionJsonProvider}s for resource conditions provided by fabric itself.
 */
public final class DefaultResourceConditions {
	private static final Identifier NOT = new Identifier("fabric:not");
	private static final Identifier AND = new Identifier("fabric:and");
	private static final Identifier OR = new Identifier("fabric:or");
	private static final Identifier ALL_MODS_LOADED = new Identifier("fabric:all_mods_loaded");
	private static final Identifier ANY_MOD_LOADED = new Identifier("fabric:any_mod_loaded");
	private static final Identifier BLOCK_TAGS_POPULATED = new Identifier("fabric:block_tags_populated");
	private static final Identifier FLUID_TAGS_POPULATED = new Identifier("fabric:fluid_tags_populated");
	private static final Identifier ITEM_TAGS_POPULATED = new Identifier("fabric:item_tags_populated");

	/**
	 * Create a NOT condition: returns true if its child condition is false, and false if its child is true.
	 */
	public static ConditionJsonProvider not(ConditionJsonProvider value) {
		return new ConditionJsonProvider() {
			@Override
			public void writeParameters(JsonObject object) {
				object.add("value", value.toJson());
			}

			@Override
			public Identifier getConditionId() {
				return NOT;
			}
		};
	}

	/**
	 * Create a condition that returns true if all of its child conditions are true.
	 */
	public static ConditionJsonProvider and(ConditionJsonProvider... values) {
		return ResourceConditionsImpl.array(AND, values);
	}

	/**
	 * Create a condition that returns true if at least one of its child conditions is true.
	 */
	public static ConditionJsonProvider or(ConditionJsonProvider... values) {
		return ResourceConditionsImpl.array(OR, values);
	}

	/**
	 * Create a condition that returns true if all the passed mod ids correspond to a loaded mod.
	 */
	public static ConditionJsonProvider allModsLoaded(String... modIds) {
		return ResourceConditionsImpl.mods(ALL_MODS_LOADED, modIds);
	}

	/**
	 * Create a condition that returns true if at least one of the passed mod ids corresponds to a loaded mod.
	 */
	public static ConditionJsonProvider anyModLoaded(String... modIds) {
		return ResourceConditionsImpl.mods(ANY_MOD_LOADED, modIds);
	}

	/**
	 * Create a condition that returns true if each of the passed block tags exists and has at least one element.
	 */
	public static ConditionJsonProvider blockTagsPopulated(Tag.Identified<Block>... tags) {
		return ResourceConditionsImpl.tagsPopulated(BLOCK_TAGS_POPULATED, tags);
	}

	/**
	 * Create a condition that returns true if each of the passed fluid tags exists and has at least one element.
	 */
	public static ConditionJsonProvider fluidTagsPopulated(Tag.Identified<Fluid>... tags) {
		return ResourceConditionsImpl.tagsPopulated(FLUID_TAGS_POPULATED, tags);
	}

	/**
	 * Create a condition that returns true if each of the passed item tags exists and has at least one element.
	 */
	public static ConditionJsonProvider itemTagsPopulated(Tag.Identified<Item>... tags) {
		return ResourceConditionsImpl.tagsPopulated(ITEM_TAGS_POPULATED, tags);
	}

	static void init() {
		// init static
	}

	static {
		ResourceConditions.register(NOT, object -> {
			JsonObject condition = JsonHelper.getObject(object, "value");
			return !ResourceConditions.conditionMatches(condition);
		});
		ResourceConditions.register(AND, object -> {
			JsonArray array = JsonHelper.getArray(object, "values");
			return ResourceConditions.conditionsMatch(array, true);
		});
		ResourceConditions.register(OR, object -> {
			JsonArray array = JsonHelper.getArray(object, "values");
			return ResourceConditions.conditionsMatch(array, false);
		});
		ResourceConditions.register(ALL_MODS_LOADED, object -> ResourceConditionsImpl.modsLoadedMatch(object, true));
		ResourceConditions.register(ANY_MOD_LOADED, object -> ResourceConditionsImpl.modsLoadedMatch(object, false));
		ResourceConditions.register(BLOCK_TAGS_POPULATED, object -> ResourceConditionsImpl.tagsPopulatedMatch(object, Registry.BLOCK_KEY));
		ResourceConditions.register(FLUID_TAGS_POPULATED, object -> ResourceConditionsImpl.tagsPopulatedMatch(object, Registry.FLUID_KEY));
		ResourceConditions.register(ITEM_TAGS_POPULATED, object -> ResourceConditionsImpl.tagsPopulatedMatch(object, Registry.ITEM_KEY));
	}

	private DefaultResourceConditions() {
	}
}
