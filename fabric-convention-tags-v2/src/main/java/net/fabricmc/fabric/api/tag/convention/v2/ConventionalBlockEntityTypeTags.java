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

package net.fabricmc.fabric.api.tag.convention.v2;

import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.tag.TagKey;

import net.fabricmc.fabric.impl.tag.convention.v2.TagRegistration;

public final class ConventionalBlockEntityTypeTags {
	private ConventionalBlockEntityTypeTags() {
	}

	/**
	 * Blocks should be included in this tag if their movement can cause serious issues such as world corruption
	 * upon being moved, such as chunk loaders or pipes, for mods that move block entities.
	 *
	 * <p>See also the equivalent block tag: {@link ConventionalBlockTags#RELOCATION_NOT_SUPPORTED}
	 */
	public static final TagKey<BlockEntityType<?>> RELOCATION_NOT_SUPPORTED = register("relocation_not_supported");

	private static TagKey<BlockEntityType<?>> register(String tagID) {
		return TagRegistration.BLOCK_ENTITY_TYPE_TAG_REGISTRATION.registerC(tagID);
	}
}
