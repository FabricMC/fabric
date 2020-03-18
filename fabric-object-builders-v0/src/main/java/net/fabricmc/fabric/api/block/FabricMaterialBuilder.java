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

import net.minecraft.block.Material;
import net.minecraft.block.MaterialColor;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.util.DyeColor;

import net.fabricmc.fabric.mixin.object.builder.MaterialBuilderHooks;

public class FabricMaterialBuilder extends Material.Builder {
	public FabricMaterialBuilder(MaterialColor color) {
		super(color);
	}

	public FabricMaterialBuilder(DyeColor color) {
		this(color.getMaterialColor());
	}

	@Override
	public FabricMaterialBuilder burnable() {
		super.burnable();
		return this;
	}

	public FabricMaterialBuilder pistonBehavior(PistonBehavior behavior) {
		((MaterialBuilderHooks) this).setPistonBehavior(behavior);
		return this;
	}

	public FabricMaterialBuilder lightPassesThrough() {
		((MaterialBuilderHooks) this).invokeLightPassesThrough();
		return this;
	}

	@Override
	public FabricMaterialBuilder destroyedByPiston() {
		super.destroyedByPiston();
		return this;
	}

	@Override
	public FabricMaterialBuilder blocksPistons() {
		super.blocksPistons();
		return this;
	}

	@Override
	public FabricMaterialBuilder allowsMovement() {
		super.allowsMovement();
		return this;
	}

	@Override
	public FabricMaterialBuilder liquid() {
		super.liquid();
		return this;
	}

	@Override
	public FabricMaterialBuilder notSolid() {
		super.notSolid();
		return this;
	}

	@Override
	public FabricMaterialBuilder replaceable() {
		super.replaceable();
		return this;
	}

	@Override
	public FabricMaterialBuilder requiresTool() {
		super.requiresTool();
		return this;
	}
}
