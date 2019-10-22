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

package net.fabricmc.fabric.api.datafixer.v1;

import java.util.Map;
import java.util.function.Supplier;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;

import net.fabricmc.fabric.impl.datafixer.TypeRefHelperImpl;

/**
 * A helper which deals with registering Entities and BlockEntities along with registering their subTags to be fixed.
 *
 * <p>
 *     This should be used inside of a {@link DataFixerEntrypoint} when registering an (block)entity or TypeReferences, otherwise issues occur with other DataFixers having no access your (block)entities, causing some DataFixes to fail.
 * </p>
 */
public interface TypeReferenceHelper {
	static final TypeReferenceHelper HELPER = TypeRefHelperImpl.INSTANCE;

	/**
	 * Registers a type with a <quote>inTile</quote>
	 * @param schema The Schema to register this Type to.
	 * @param typeMap The Map of all other TypeTemplates within the Schema
	 * @param name The name of the type being registered.
	 */
	void registerTypeInTile(Schema schema, Map<String, Supplier<TypeTemplate>> typeMap, String name);

	/**
	 * Registers a type which can contain equipment (armor and hand items), similarly to a {@link net.minecraft.entity.mob.ZombieEntity}.
	 * @param schema The Schema to register this type to.
	 * @param typeMap The Map of all other TypeTemplates within the Schema.
	 * @param name The name of the type being registered
	 */
	void registerTypeWithEquipment(Schema schema, Map<String, Supplier<TypeTemplate>> typeMap, String name);

	/**
	 * Registers a type with no special attributes, other than being held under a {@link com.mojang.datafixers.DSL.TypeReference}.
	 * @param schema The Schema to register this type to.
	 * @param typeMap The Map of all other TypeTemplates within the Schema.
	 * @param name The name of the type being registered.
	 */
	void registerSimpleType(Schema schema, Map<String, Supplier<TypeTemplate>> typeMap, String name);

	/**
	 * Registers a type which can contain items, normally a BlockEntity such as a {@link net.minecraft.block.entity.ChestBlockEntity} or a {@link net.minecraft.block.entity.FurnaceBlockEntity}.
	 * @param schema The Schema to register this type to.
	 * @param typeMap The Map of all other TypeTemplates within the Schema
	 * @param name The name of the type being registered
	 */
	void registerTypeWithItems(Schema schema, Map<String, Supplier<TypeTemplate>> typeMap, String name);

	/**
	 * Registers a type which can contain HandItems and ArmorSlots, such as a zombie, a skeleton and most living entities.
	 * @param schema The Schema to register this type to.
	 * @param typeMap The Map of all other TypeTemplates within the Schema.
	 * @param name The name of the type being registered.
	 */
	void registerTypeWithArmorAndToolSlots(Schema schema, Map<String, Supplier<TypeTemplate>> typeMap, String name);

	/**
	 * Registers a type which uses a custom {@link TypeTemplate}.
	 * @param schema The Schema to register this type to.
	 * @param typeMap The Map of all other TypeTemplates within the Schema.
	 * @param typeTemplateSupplier A Supplier which contains the {@link TypeTemplate} of the type being registered.
	 * @param name The name of the type being registered.
	 */
	void registerTypeWithTemplate(Schema schema, Map<String, Supplier<TypeTemplate>> typeMap, Supplier<TypeTemplate> typeTemplateSupplier, String name);
}
