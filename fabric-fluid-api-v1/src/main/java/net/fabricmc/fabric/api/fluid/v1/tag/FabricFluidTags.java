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

package net.fabricmc.fabric.api.fluid.v1.tag;

import net.minecraft.fluid.Fluid;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.tag.TagFactory;

/**
 * Provides tags for all fluid customizations.
 */
public class FabricFluidTags {
	//region TAGS

	/**
	 * fabric:breathable -> Identifies a fabric fluid that is breathable by the land mobs and the player.
	 */
	public static final Tag<Fluid> BREATHABLE;

	/**
	 * fabric:breathable_by_aquatic -> Identifies a fabric fluid that is breathable by the aquatic mobs
	 * (will be like water for them).
	 */
	public static final Tag<Fluid> BREATHABLE_BY_AQUATIC;

	/**
	 * fabric:can_extinguish_fire -> Identifies a fabric fluid that can extinguish fire.
	 */
	public static final Tag<Fluid> CAN_EXTINGUISH_FIRE;

	/**
	 * fabric:can_light_fire -> Identifies a fabric fluid that can light fire.
	 */
	public static final Tag<Fluid> CAN_LIGHT_FIRE;

	/**
	 * fabric:ignore_depth_strider -> Identifies a fabric fluid in which depth strider has no effect.
	 */
	public static final Tag<Fluid> IGNORE_DEPTH_STRIDER;

	/**
	 * fabric:navigable -> Identifies a fabric fluid that is navigable.
	 */
	public static final Tag<Fluid> NAVIGABLE;

	/**
	 * fabric:prevent_fall_damage -> Identifies a fabric fluid that can prevent fall damage.
	 */
	public static final Tag<Fluid> PREVENT_FALL_DAMAGE;

	/**
	 * fabric:swimmable -> Identifies a fabric fluid that is swimmable.
	 */
	public static final Tag<Fluid> SWIMMABLE;

	/**
	 * fabric:wet -> Identifies a fabric fluid that can wet.
	 */
	public static final Tag<Fluid> WET;

	//endregion

	static {
		BREATHABLE = TagFactory.FLUID.create(new Identifier("fabric", "breathable"));
		BREATHABLE_BY_AQUATIC = TagFactory.FLUID.create(new Identifier("fabric", "breathable_by_aquatic"));
		CAN_EXTINGUISH_FIRE = TagFactory.FLUID.create(new Identifier("fabric", "can_extinguish_fire"));
		CAN_LIGHT_FIRE = TagFactory.FLUID.create(new Identifier("fabric", "can_light_fire"));
		IGNORE_DEPTH_STRIDER = TagFactory.FLUID.create(new Identifier("fabric", "ignore_depth_strider"));
		NAVIGABLE = TagFactory.FLUID.create(new Identifier("fabric", "navigable"));
		PREVENT_FALL_DAMAGE = TagFactory.FLUID.create(new Identifier("fabric", "prevent_fall_damage"));
		SWIMMABLE = TagFactory.FLUID.create(new Identifier("fabric", "swimmable"));
		WET = TagFactory.FLUID.create(new Identifier("fabric", "wet"));
	}

	/**
	 * Initializes a new FabricFluidTags instance.
	 */
	private FabricFluidTags() {
	}
}
