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

package net.fabricmc.fabric.mixin.datafixer.v1;

import java.util.Map;
import java.util.function.Supplier;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.datafixer.schema.Schema1906;

import net.fabricmc.fabric.api.datafixer.v1.DataFixerEvents;

@Mixin(Schema1906.class)
public class Schema1906Mixin {
	@ModifyReturnValue(method = "registerBlockEntities", at = @At("RETURN"))
	private Map<String, Supplier<TypeTemplate>> registerModdedBlockEntities(Map<String, Supplier<TypeTemplate>> original, Schema schema) {
		DataFixerEvents.REGISTER_BLOCK_ENTITIES.invoker().onRegisterBlockEntities(original, schema);
		return original;
	}
}
