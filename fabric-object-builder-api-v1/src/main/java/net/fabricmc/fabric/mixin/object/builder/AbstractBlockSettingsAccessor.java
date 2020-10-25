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

import java.util.function.Function;
import java.util.function.ToIntFunction;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.block.MaterialColor;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

@Mixin(AbstractBlock.Settings.class)
public interface AbstractBlockSettingsAccessor {
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
	ToIntFunction<BlockState> getLuminance();

	@Accessor
	Function<BlockState, MaterialColor> getMaterialColorFactory();

	@Accessor
	BlockSoundGroup getSoundGroup();

	@Accessor
	float getSlipperiness();

	@Accessor
	float getVelocityMultiplier();

	@Accessor
	boolean getDynamicBounds();

	@Accessor
	boolean getOpaque();

	@Accessor
	boolean getIsAir();

	@Accessor
	boolean isToolRequired();

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
	void setMaterialColorFactory(Function<BlockState, MaterialColor> materialColorFunction);

	@Accessor
	void setDynamicBounds(boolean dynamicBounds);

	@Accessor
	void setOpaque(boolean opaque);

	@Accessor
	void setIsAir(boolean isAir);

	@Accessor
	void setLootTableId(Identifier lootTableId);

	@Accessor
	void setToolRequired(boolean toolRequired);

	/* INVOKERS */
	@Invoker
	Block.Settings invokeSounds(BlockSoundGroup group);

	@Invoker
	Block.Settings invokeLightLevel(ToIntFunction<BlockState> lightLevelFunction);

	@Invoker
	Block.Settings invokeBreakInstantly();

	@Invoker
	Block.Settings invokeStrength(float strength);

	@Invoker
	Block.Settings invokeTicksRandomly();

	@Invoker
	Block.Settings invokeDropsNothing();
}
