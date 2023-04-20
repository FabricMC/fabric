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

package net.fabricmc.fabric.impl.itemgroup;

import net.minecraft.item.ItemGroup;
import net.minecraft.text.Text;

public final class FabricItemGroupBuilderImpl extends ItemGroup.Builder {
	private boolean hasDisplayName = false;

	public FabricItemGroupBuilderImpl() {
		// Set when building.
		super(null, -1);
	}

	@Override
	public ItemGroup.Builder displayName(Text displayName) {
		hasDisplayName = true;
		return super.displayName(displayName);
	}

	@Override
	public ItemGroup build() {
		if (!hasDisplayName) {
			throw new IllegalStateException("No display name set for ItemGroup");
		}

		return super.build();
	}
}
