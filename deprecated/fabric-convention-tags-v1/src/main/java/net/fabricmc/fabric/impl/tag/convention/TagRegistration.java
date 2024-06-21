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

package net.fabricmc.fabric.impl.tag.convention;

import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;

@Deprecated
public class TagRegistration<T> {
	public static final TagRegistration<Item> ITEM_TAG_REGISTRATION = new TagRegistration<>(RegistryKeys.ITEM);
	public static final TagRegistration<Block> BLOCK_TAG_REGISTRATION = new TagRegistration<>(RegistryKeys.BLOCK);
	public static final TagRegistration<Biome> BIOME_TAG_REGISTRATION = new TagRegistration<>(RegistryKeys.BIOME);
	public static final TagRegistration<Fluid> FLUID_TAG_REGISTRATION = new TagRegistration<>(RegistryKeys.FLUID);
	public static final TagRegistration<EntityType<?>> ENTITY_TYPE_TAG_REGISTRATION = new TagRegistration<>(RegistryKeys.ENTITY_TYPE);
	public static final TagRegistration<Enchantment> ENCHANTMENT_TAG_REGISTRATION = new TagRegistration<>(RegistryKeys.ENCHANTMENT);
	private final RegistryKey<Registry<T>> registryKey;

	private TagRegistration(RegistryKey<Registry<T>> registry) {
		registryKey = registry;
	}

	@Deprecated
	public TagKey<T> registerFabric(String tagId) {
		return TagKey.of(registryKey, Identifier.of("fabric", tagId));
	}

	@Deprecated
	public TagKey<T> registerC(String tagId) {
		return TagKey.of(registryKey, Identifier.of("c", tagId));
	}
}
