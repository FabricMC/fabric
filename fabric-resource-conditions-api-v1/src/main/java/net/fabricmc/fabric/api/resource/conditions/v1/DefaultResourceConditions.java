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
import net.minecraft.tag.TagKey;
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
	private static final Identifier TAGS_POPULATED = new Identifier("fabric:tags_populated");

	/**
	 * Creates a NOT condition that returns true if its child condition is false, and false if its child is true.
	 *
	 * @apiNote This condition's ID is {@code fabric:not}, and takes one property, {@code value},
	 * which is a condition.
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
	 * Creates a condition that returns true if all of its child conditions are true.
	 *
	 * @apiNote This condition's ID is {@code fabric:and}, and takes one property, {@code values},
	 * which is an array of conditions.
	 */
	public static ConditionJsonProvider and(ConditionJsonProvider... values) {
		return ResourceConditionsImpl.array(AND, values);
	}

	/**
	 * Creates a condition that returns true if any of its child conditions are true.
	 *
	 * @apiNote This condition's ID is {@code fabric:or}, and takes one property, {@code values},
	 * which is an array of conditions.
	 */
	public static ConditionJsonProvider or(ConditionJsonProvider... values) {
		return ResourceConditionsImpl.array(OR, values);
	}

	/**
	 * Creates a condition that returns true if all the passed mod ids correspond to a loaded mod.
	 *
	 * @apiNote This condition's ID is {@code fabric:all_mods_loaded}, and takes one property,
	 * {@code values}, which is an array of string mod IDs.
	 */
	public static ConditionJsonProvider allModsLoaded(String... modIds) {
		return ResourceConditionsImpl.mods(ALL_MODS_LOADED, modIds);
	}

	/**
	 * Creates a condition that returns true if at least one of the passed mod ids corresponds to a loaded mod.
	 *
	 * @apiNote This condition's ID is {@code fabric:any_mod_loaded}, and takes one property,
	 * {@code values}, which is an array of string mod IDs.
	 */
	public static ConditionJsonProvider anyModLoaded(String... modIds) {
		return ResourceConditionsImpl.mods(ANY_MOD_LOADED, modIds);
	}

	/**
	 * Create a condition that returns true if each of the passed block tags exists and has at least one element.
	 * @deprecated Use {@link #tagsPopulated} instead.
	 */
	@SafeVarargs
	@Deprecated
	public static ConditionJsonProvider blockTagsPopulated(TagKey<Block>... tags) {
		return ResourceConditionsImpl.tagsPopulated(BLOCK_TAGS_POPULATED, false, tags);
	}

	/**
	 * Create a condition that returns true if each of the passed fluid tags exists and has at least one element.
	 * @deprecated Use {@link #tagsPopulated} instead.
	 */
	@SafeVarargs
	@Deprecated
	public static ConditionJsonProvider fluidTagsPopulated(TagKey<Fluid>... tags) {
		return ResourceConditionsImpl.tagsPopulated(FLUID_TAGS_POPULATED, false, tags);
	}

	/**
	 * Create a condition that returns true if each of the passed item tags exists and has at least one element.
	 * @deprecated Use {@link #tagsPopulated} instead.
	 */
	@SafeVarargs
	@Deprecated
	public static ConditionJsonProvider itemTagsPopulated(TagKey<Item>... tags) {
		return ResourceConditionsImpl.tagsPopulated(ITEM_TAGS_POPULATED, false, tags);
	}

	/**
	 * Creates a condition that returns true if each of the passed tags exists and has at least one element.
	 * This works for any registries, and the registry ID of the tags is serialized to JSON as well as the tags.
	 *
	 * @apiNote This condition's ID is {@code fabric:tags_populated}, and takes up to two properties:
	 * {@code values}, which is an array of string tag IDs, and {@code registry}, which is the ID of
	 * the registry of the tags. If {@code registry} is not provided, it defaults to {@code minecraft:item}.
	 */
	@SafeVarargs
	public static <T> ConditionJsonProvider tagsPopulated(TagKey<T>... tags) {
		return ResourceConditionsImpl.tagsPopulated(TAGS_POPULATED, true, tags);
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
		ResourceConditions.register(TAGS_POPULATED, ResourceConditionsImpl::tagsPopulatedMatch);
	}

	private DefaultResourceConditions() {
	}
}
