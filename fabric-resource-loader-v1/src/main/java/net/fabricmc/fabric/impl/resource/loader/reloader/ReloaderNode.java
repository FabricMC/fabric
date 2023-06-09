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

package net.fabricmc.fabric.impl.resource.loader.reloader;

import org.jetbrains.annotations.Nullable;

import net.minecraft.resource.ResourceReloader;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.impl.base.event.SortableNode;

public class ReloaderNode extends SortableNode<ReloaderNode> {
	static final int BEFORE_MINECRAFT = -1;
	static final int AFTER_MINECRAFT = 1000;
	static final int MODDED = 10000;

	/**
	 * Index to ensure that vanilla's reloader order is always respected.
	 * Vanilla reloaders have consecutive indexes starting at 0.
	 * BEFORE_MINECRAFT has index -1, AFTER_MINECRAFT has index 1000.
	 * Mod reloaders have 10000 to run them after vanilla reloaders by default.
	 */
	final int vanillaIndex;
	final Identifier identifier;
	@Nullable
	ResourceReloader reloader = null;

	public ReloaderNode(int vanillaIndex, Identifier identifier) {
		this.vanillaIndex = vanillaIndex;
		this.identifier = identifier;
	}

	@Override
	protected String getDescription() {
		return "%s (vanillaIndex = %s)".formatted(identifier, vanillaIndex);
	}

	@Override
	public int compareTo(ReloaderNode o) {
		if (vanillaIndex != o.vanillaIndex) {
			return Integer.compare(vanillaIndex, o.vanillaIndex);
		}

		return identifier.compareTo(o.identifier);
	}
}
