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

package net.fabricmc.fabric.impl.resource.loader;

import java.util.Objects;

import net.minecraft.resource.ResourcePackSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class BuiltinModResourcePackSource implements ResourcePackSource {
	private final String modId;

	public BuiltinModResourcePackSource(String modId) {
		this.modId = modId;
	}

	@Override
	public boolean canBeEnabledLater() {
		return true;
	}

	@Override
	public Text decorate(Text packName) {
		return Text.translatable("pack.nameAndSource", packName, Text.translatable("pack.source.builtinMod", modId)).formatted(Formatting.GRAY);
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}

		if (other instanceof BuiltinModResourcePackSource otherSources) {
			return Objects.equals(this.modId, otherSources.modId);
		}

		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(modId);
	}
}
