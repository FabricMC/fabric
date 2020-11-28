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

package net.fabricmc.fabric.api.tool.attribute.v1;

import net.minecraft.item.ToolMaterial;
import net.minecraft.util.math.MathHelper;

/**
 * Fabric extensions to {@link ToolMaterial} to provide access to returning {@link ToolLevel} as the mining level.
 *
 * @see DynamicAttributeTool to set tool levels according to the {@link net.minecraft.item.ItemStack} instance.
 */
public interface FabricToolMaterial extends ToolMaterial {
	static float getFrom(ToolMaterial material) {
		if (material instanceof FabricToolMaterial) {
			return ((FabricToolMaterial) material).getToolLevel().getLevel();
		}

		return material.getMiningLevel();
	}

	ToolLevel getToolLevel();

	@Override
	default int getMiningLevel() {
		return MathHelper.floor(getToolLevel().getLevel());
	}
}
