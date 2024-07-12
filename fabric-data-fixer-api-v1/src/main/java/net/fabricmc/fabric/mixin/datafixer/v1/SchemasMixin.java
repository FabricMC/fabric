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

import com.mojang.datafixers.DataFixerBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.datafixer.Schemas;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.fix.ChoiceTypesFix;

import net.fabricmc.fabric.impl.datafixer.v1.FabricSubSchema;

@Mixin(Schemas.class)
public class SchemasMixin {
	@Inject(
			method = "build",
			at = @At(
					value = "INVOKE",
					target = "Lcom/mojang/datafixers/DataFixerBuilder;addSchema(ILjava/util/function/BiFunction;)Lcom/mojang/datafixers/schemas/Schema;",
					ordinal = 0
			),
			slice = @Slice(
					from = @At(
							value = "CONSTANT",
							args = "intValue=1803"
					)
			)
	)
	private static void addFabricFixers(DataFixerBuilder builder, CallbackInfo ci) {
		FabricSubSchema schema = (FabricSubSchema) builder.addSchema(1903, FabricSubSchema::new);

		if (!schema.registeredBlockEntities.getKeys().isEmpty()) {
			builder.addFixer(new ChoiceTypesFix(schema, "Add Fabric block entities.", TypeReferences.BLOCK_ENTITY));
		}

		if (!schema.registeredEntities.getKeys().isEmpty()) {
			builder.addFixer(new ChoiceTypesFix(schema, "Add Fabric entities.", TypeReferences.ENTITY));
		}

		schema.registeredBlockEntities = null;
		schema.registeredEntities = null;
	}
}
