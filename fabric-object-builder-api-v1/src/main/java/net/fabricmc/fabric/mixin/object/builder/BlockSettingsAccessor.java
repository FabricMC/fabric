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

package net.fabricmc.fabric.mixin.object.builder;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.MaterialColor;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

@Mixin(Block.Settings.class)
public interface BlockSettingsAccessor {
	/* GETTERS */
	@Accessor
	Material getMaterial();

	@Accessor
	float getHardness();

	@Accessor
	float getResistance();

	@Accessor
	boolean getCollidable();

	@Accessor
	boolean getRandomTicks();

	@Accessor
	int getLuminance();

	@Accessor
	MaterialColor getMaterialColor();

	@Accessor
	BlockSoundGroup getSoundGroup();

	@Accessor
	float getSlipperiness();

	@Accessor
	float getJumpVelocityMultiplier();

	@Accessor
	boolean getDynamicBounds();

	@Accessor
	boolean getOpaque();

	/* SETTERS */
	@Accessor
	void setMaterial(Material material);

	@Accessor
	void setHardness(float hardness);

	@Accessor
	void setResistance(float resistance);

	@Accessor
	void setCollidable(boolean collidable);

	@Accessor
	void setRandomTicks(boolean ticksRandomly);

	@Accessor
	void setMaterialColor(MaterialColor materialColor);

	@Accessor
	void setDropTableId(Identifier dropTableId);

	@Accessor
	void setDynamicBounds(boolean dynamicBounds);

	@Accessor
	void setOpaque(boolean opaque);

	/* INVOKERS */

	@Invoker
	Block.Settings invokeSounds(BlockSoundGroup group);

	@Invoker
	Block.Settings invokeLightLevel(int lightLevel);

	@Invoker
	Block.Settings invokeBreakInstantly();

	@Invoker
	Block.Settings invokeStrength(float strength);

	@Invoker
	Block.Settings invokeTicksRandomly();

	@Invoker
	Block.Settings invokeHasDynamicBounds();

	@Invoker
	Block.Settings invokeDropsNothing();
}
