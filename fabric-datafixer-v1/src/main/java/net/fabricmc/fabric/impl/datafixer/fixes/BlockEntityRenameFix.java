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

import java.util.Objects;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.TaggedChoice.TaggedChoiceType;
import com.mojang.datafixers.util.Pair;

import net.minecraft.datafixers.TypeReferences;

/**
 * @deprecated Untested
 * TODO: Untested
 */
public abstract class BlockEntityRenameFix extends DataFix {

	private String name;

	public BlockEntityRenameFix(Schema outputSchema, boolean changesType, String name) {
		super(outputSchema, changesType);
		this.name = name;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected TypeRewriteRule makeRule() {
		TaggedChoiceType<String> taggedChoice$TaggedChoiceType_1 = (TaggedChoiceType<String>) this.getInputSchema().findChoiceType(TypeReferences.BLOCK_ENTITY);
		TaggedChoiceType<String> taggedChoice$TaggedChoiceType_2 = (TaggedChoiceType<String>) this.getOutputSchema().findChoiceType(TypeReferences.BLOCK_ENTITY);
		Type<Pair<String, String>> type_1 = DSL.named(TypeReferences.BLOCK_ENTITY.typeName(), DSL.namespacedString());

		if (!Objects.equals(this.getOutputSchema().getType(TypeReferences.BLOCK_ENTITY), type_1)) {
			throw new IllegalStateException("BlockEntity name type is not what was expected.");
		} else {
			return TypeRewriteRule.seq(this.fixTypeEverywhere(this.name, taggedChoice$TaggedChoiceType_1, taggedChoice$TaggedChoiceType_2, (dynamicOps) -> {
				return (pair) -> {
					return pair.mapFirst((originalValue) -> {
						String newValue = this.rename(originalValue);
						Type<?> originalType = taggedChoice$TaggedChoiceType_1.types().get(originalValue);
						Type<?> newType = taggedChoice$TaggedChoiceType_2.types().get(newValue);

						if (!newType.equals(originalType, true, true)) {
							throw new IllegalStateException(String.format("Dynamic type check failed: %s not equal to %s", newType, originalType));
						} else {
							return newValue;
						}
					});
				};
			}), this.fixTypeEverywhere(this.name + " for blockentity name", type_1, (dynamicOps) -> {
				return (pair) -> {
					return pair.mapSecond(this::rename);
				};
			}));

		}
	}

	protected abstract String rename(String s);
}
