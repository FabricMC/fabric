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

package net.fabricmc.fabric.api.block;

import net.fabricmc.fabric.mixin.builders.MaterialBuilderHooks;
import net.minecraft.block.Material;
import net.minecraft.block.MaterialColor;
import net.minecraft.block.piston.PistonBehavior;

public class FabricMaterialBuilder extends Material.Builder {
	public FabricMaterialBuilder(MaterialColor materialColor_1) {
		super(materialColor_1);
	}

	@Override
	public Material.Builder burnable() {
		return super.burnable();
	}

	public Material.Builder pistonBehavior(PistonBehavior behavior) {
		((MaterialBuilderHooks) this).setPistonBehavior(behavior);
		return this;
	}

	public Material.Builder lightPassesThrough() {
		((MaterialBuilderHooks) this).invokeLightPassesThrough();
		return this;
	}

	@Override
	public Material.Builder destroyedByPiston() {
		return super.destroyedByPiston();
	}

	@Override
	public Material.Builder blocksPistons() {
		return super.blocksPistons();
	}
}
