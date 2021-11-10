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

import net.fabricmc.fabric.api.tag.TagFactory;
import net.minecraft.fluid.Fluid;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

/**
 * Provides tags for all fluid customizations.
 */
public class FabricFluidTags {
	//region TAGS

	/**
	 * fabric:fabric_fluid/fire_extinguisher -> Identifies a fabric_fluid that can extinguish fire.
	 */
	public static final Tag<Fluid> FIRE_EXTINGUISHER;

	/**
	 * fabric:fabric_fluid/fire_lighter -> Identifies a fabric_fluid that can light fire.
	 */
	public static final Tag<Fluid> FIRE_LIGHTER;

	/**
	 * fabric:fabric_fluid/navigable -> Identifies a fabric_fluid that is navigable.
	 */
	public static final Tag<Fluid> NAVIGABLE;

	/**
	 * fabric:fabric_fluid/prevent_fall_damage -> Identifies a fabric_fluid that can prevent fall damage.
	 */
	public static final Tag<Fluid> PREVENT_FALL_DAMAGE;

	/**
	 * fabric:fabric_fluid/respirable -> Identifies a fabric_fluid that is respirable.
	 */
	public static final Tag<Fluid> RESPIRABLE;

	/**
	 * fabric:fabric_fluid/swimmable -> Identifies a fabric_fluid that is swimmable.
	 */
	public static final Tag<Fluid> SWIMMABLE;

	/**
	 * fabric:fabric_fluid/wet -> Identifies a fabric_fluid that can wet.
	 */
	public static final Tag<Fluid> WET;

	//endregion

	static {
		FIRE_EXTINGUISHER =   TagFactory.FLUID.create(new Identifier("fabric", "fire_extinguisher"));
		FIRE_LIGHTER =        TagFactory.FLUID.create(new Identifier("fabric", "fire_lighter"));
		NAVIGABLE =           TagFactory.FLUID.create(new Identifier("fabric", "navigable"));
		PREVENT_FALL_DAMAGE = TagFactory.FLUID.create(new Identifier("fabric", "prevent_fall_damage"));
		RESPIRABLE =          TagFactory.FLUID.create(new Identifier("fabric", "respirable"));
		SWIMMABLE =           TagFactory.FLUID.create(new Identifier("fabric", "swimmable"));
		WET =                 TagFactory.FLUID.create(new Identifier("fabric", "wet"));
	}

	private FabricFluidTags() {}
}
