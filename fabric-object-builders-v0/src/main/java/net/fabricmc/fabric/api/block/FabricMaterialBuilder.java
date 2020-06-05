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

/**
 * @deprecated Please migrate to v1. Please use {@link net.fabricmc.fabric.api.object.builder.v1.block.FabricMaterialBuilder} instead.
 */
@Deprecated
public class FabricMaterialBuilder extends Material.Builder {
	private net.fabricmc.fabric.api.object.builder.v1.block.FabricMaterialBuilder delegate;

	public FabricMaterialBuilder(MaterialColor color) {
		super(color);
		this.delegate = new net.fabricmc.fabric.api.object.builder.v1.block.FabricMaterialBuilder(color);
	}

	public FabricMaterialBuilder(DyeColor color) {
		this(color.getMaterialColor());
	}

	@Override
	public FabricMaterialBuilder burnable() {
		this.delegate.burnable();
		return this;
	}

	public FabricMaterialBuilder pistonBehavior(PistonBehavior behavior) {
		this.delegate.pistonBehavior(behavior);
		return this;
	}

	public FabricMaterialBuilder lightPassesThrough() {
		this.delegate.lightPassesThrough();
		return this;
	}

	@Override
	public FabricMaterialBuilder destroyedByPiston() {
		this.delegate.destroyedByPiston();
		return this;
	}

	@Override
	public FabricMaterialBuilder blocksPistons() {
		this.delegate.blocksPistons();
		return this;
	}

	@Override
	public FabricMaterialBuilder allowsMovement() {
		this.delegate.allowsMovement();
		return this;
	}

	@Override
	public FabricMaterialBuilder liquid() {
		this.delegate.liquid();
		return this;
	}

	@Override
	public FabricMaterialBuilder notSolid() {
		this.delegate.notSolid();
		return this;
	}

	@Override
	public FabricMaterialBuilder replaceable() {
		this.delegate.replaceable();
		return this;
	}

	@Override
	public Material build() {
		return this.delegate.build();
	}
}
