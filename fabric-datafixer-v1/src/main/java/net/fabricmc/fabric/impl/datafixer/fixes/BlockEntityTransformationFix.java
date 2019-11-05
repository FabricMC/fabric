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

package net.fabricmc.fabric.impl.datafixer.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import net.fabricmc.fabric.api.datafixer.v1.SimpleFixes;
import net.minecraft.datafixers.TypeReferences;
import net.minecraft.datafixers.fixes.ChoiceFix;
import net.minecraft.nbt.Tag;

public class BlockEntityTransformationFix extends ChoiceFix {
	private SimpleFixes.BlockEntityTransformation transformation;

	public BlockEntityTransformationFix(Schema schema, String name, String blockEntityName, SimpleFixes.BlockEntityTransformation transformation) {
		super(schema, false, name, TypeReferences.BLOCK_ENTITY, blockEntityName);
		// Choice Fixes work differently.
		// We specify a TypeReference and name, then it will find the type we are refering to and then invoke transform to update the type.
		this.transformation = transformation;
	}

	@Override
	protected Typed<?> transform(Typed<?> typed) {
		return typed.update(DSL.remainderFinder(), (dynamic) -> {
			return transformation.transform((Dynamic<Tag>) dynamic);
		});
	}
}
