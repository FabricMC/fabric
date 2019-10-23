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

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.TaggedChoice.TaggedChoiceType;
import com.mojang.datafixers.util.Pair;

import net.fabricmc.fabric.impl.datafixer.FabricDataFixerImpl;
import net.minecraft.datafixers.TypeReferences;

/**
 * This works, although a few issues still exist. First the registry complains the original BlockEntity does not exist, this has no effect as block entity is still fixed.
 * It works but very hacky looking, unless mojang adds their own method into game in the future, then it will be if it ain't broke don't fix it.
 */
public class BlockEntityRenameFix extends DataFix {
	private final String oldName;
	private final String newName;
	private final String fixName;

	public BlockEntityRenameFix(Schema schema, String fixName, String oldName, String newName) {
		super(schema, false);
		this.oldName = oldName;
		this.newName = newName;
		this.fixName = fixName;
	}

	@SuppressWarnings("unchecked")
	@Override
	public TypeRewriteRule makeRule() {
		TaggedChoiceType<String> originalTypeChoice = (TaggedChoiceType<String>) this.getInputSchema().findChoiceType(TypeReferences.BLOCK_ENTITY);
		TaggedChoiceType<String> newTypeChoice = (TaggedChoiceType<String>) this.getOutputSchema().findChoiceType(TypeReferences.BLOCK_ENTITY);
		Type<Pair<String, String>> blockEntityType = DSL.named(TypeReferences.ENTITY_NAME.typeName(), DSL.namespacedString()); // Why on earth does this work with ENTITY_NAME.

		if (!Objects.equals(this.getOutputSchema().getType(TypeReferences.ENTITY_NAME), blockEntityType)) { // I mean seriously I didn't change that field but it works. Then again ENTITY TypeReference does not refer to ENTITY_NAME in any instances I've seen in registerTypes.
			throw new IllegalStateException("BlockEntity name type is not what was expected.");
		} else {
			return TypeRewriteRule.seq(this.fixTypeEverywhere(this.fixName, originalTypeChoice, newTypeChoice, (ops) -> (pair) -> pair.mapFirst((originalName) -> {
				String possiblyNamedString = this.rename(originalName);
				Type<?> originalType = originalTypeChoice.types().get(originalName);
				Type<?> newType = newTypeChoice.types().get(possiblyNamedString);
				if (!newType.equals(originalType, true, true)) {
					throw new IllegalStateException(String.format("Dynamic type check failed: %s not equal to %s", newType, originalType));
				} else {
					return possiblyNamedString;
				}
			})), this.fixTypeEverywhere(this.fixName, blockEntityType, (ops) -> (pair) -> pair.mapSecond(this::rename)));
		}
	}

	private String rename(String originalValue) {
		return Objects.equals(originalValue, oldName) ? newName : originalValue;
	}
}
