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

import java.util.List;
import java.util.function.BiFunction;

import com.mojang.datafixers.DataFixerBuilder;
import com.mojang.datafixers.schemas.Schema;
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
							args = "intValue=3820", // match latest schema version
							shift = At.Shift.BEFORE
					)
			)
	)
	private static void addFabricFixers(DataFixerBuilder builder, CallbackInfo ci) {
		FabricSubSchema schema = (FabricSubSchema) builder.addSchema(3820, 1, FabricSubSchema::new);

		for (String id : schema.registeredBlockEntities.getKeys()) {
			builder.addFixer(new ChoiceTypesFix(schema, "Add Fabric block entity " + id, TypeReferences.BLOCK_ENTITY));
		}

		for (String id : schema.registeredEntities.getKeys()) {
			builder.addFixer(new ChoiceTypesFix(schema, "Add Fabric entity " + id, TypeReferences.ENTITY));
		}

		int registeredBlockEntitiesSize = schema.registeredBlockEntities.get().size();
		List<BiFunction<Integer, Schema, Schema>> subSchemas = schema.registeredEntities.getFutureSchemas();

		for (int i = 0; i < registeredBlockEntitiesSize; i++) {
			BiFunction<Integer, Schema, Schema> subSchema = subSchemas.get(i);
			builder.addSchema(3820, i + 2, subSchema);
		}

		schema.registeredBlockEntities = null;
		schema.registeredEntities = null;
	}
}
