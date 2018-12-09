/*
 * Copyright (c) 2016, 2017, 2018 FabricMC
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

package net.fabricmc.fabric.mixin.block;

import net.fabricmc.fabric.block.FabricBlockBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.MaterialColor;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Block.Builder.class)
public class MixinBlockBuilder implements FabricBlockBuilder.Delegate {
	@Shadow
	private Material material;
	@Shadow
	private MaterialColor materialColor;
	@Shadow
	private boolean collidable;
	@Shadow
	private BlockSoundGroup soundGroup;
	@Shadow
	private int luminance;
	@Shadow
	private float resistance;
	@Shadow
	private float hardness;
	@Shadow
	private boolean randomTicks;
	@Shadow
	private float friction;
	@Shadow
	private Identifier dropTableId;

	@Override
	public void fabric_setMapColor(MaterialColor color) {
		materialColor = color;
	}

	@Override
	public void fabric_setCollidable(boolean value) {
		collidable = value;
	}

	@Override
	public void fabric_setSoundGroup(BlockSoundGroup group) {
		soundGroup = group;
	}

	@Override
	public void fabric_setLuminance(int value) {
		luminance = value;
	}

	@Override
	public void fabric_setHardness(float value) {
		hardness = value;
	}

	@Override
	public void fabric_setResistance(float value) {
		resistance = Math.max(0.0f, value);
	}

	@Override
	public void fabric_setRandomTicks(boolean value) {
		randomTicks = value;
	}

	@Override
	public void fabric_setFriction(float value) {
		friction = value;
	}

	@Override
	public void fabric_setDropTable(Identifier id) {
		dropTableId = id;
	}
}
