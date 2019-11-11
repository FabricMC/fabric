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

package net.fabricmc.fabric.impl.datafixer.mixin;

import java.util.Map;
import java.util.function.Supplier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;

import net.minecraft.datafixers.schemas.Schema99;

@Mixin(Schema99.class)
public interface Schema99Accessor {
	/**
	 * registerTypeWithEquipment.
	 * @param schema
	 * @param entityMap
	 * @param name
	 */
	@Invoker
	static void callMethod_5339(Schema schema, Map<String, Supplier<TypeTemplate>> entityMap, String name) {
		throw new AssertionError("Mixin Dummy");
	}

	/**
	 * registerTypeWithItems.
	 * @param schema
	 * @param typeMap
	 * @param name
	 */
	@Invoker
	static void callMethod_5346(Schema schema, Map<String, Supplier<TypeTemplate>> typeMap, String name) {
		throw new AssertionError("Mixin Dummy");
	}

	/**
	 * registerTypeInTile.
	 * @param schema
	 * @param typeMap
	 * @param name
	 */
	@Invoker
	static void callMethod_5368(Schema schema, Map<String, Supplier<TypeTemplate>> typeMap, String name) {
		throw new AssertionError("Mixin Dummy");
	}
}
