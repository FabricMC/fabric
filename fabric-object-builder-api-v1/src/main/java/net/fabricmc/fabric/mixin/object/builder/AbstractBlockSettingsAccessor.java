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

import java.util.Optional;
import java.util.function.Function;
import java.util.function.ToIntFunction;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.MapColor;
import net.minecraft.block.enums.NoteBlockInstrument;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.EntityType;
import net.minecraft.loot.LootTable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.sound.BlockSoundGroup;

@Mixin(AbstractBlock.Settings.class)
public interface AbstractBlockSettingsAccessor {
	/* GETTERS */
	@Accessor
	float getHardness();

	@Accessor
	float getResistance();

	@Accessor
	boolean getCollidable();

	@Accessor
	boolean getRandomTicks();

	@Accessor("luminance")
	ToIntFunction<BlockState> getLuminance();

	@Accessor
	Function<BlockState, MapColor> getMapColorProvider();

	@Accessor
	BlockSoundGroup getSoundGroup();

	@Accessor
	float getSlipperiness();

	@Accessor
	float getVelocityMultiplier();

	@Accessor
	float getJumpVelocityMultiplier();

	@Accessor
	boolean getDynamicBounds();

	@Accessor
	boolean getOpaque();

	@Accessor
	boolean getIsAir();

	@Accessor
	boolean isToolRequired();

	@Accessor
	AbstractBlock.TypedContextPredicate<EntityType<?>> getAllowsSpawningPredicate();

	@Accessor
	AbstractBlock.ContextPredicate getSolidBlockPredicate();

	@Accessor
	AbstractBlock.ContextPredicate getSuffocationPredicate();

	@Accessor
	AbstractBlock.ContextPredicate getBlockVisionPredicate();

	@Accessor
	AbstractBlock.ContextPredicate getPostProcessPredicate();

	@Accessor
	AbstractBlock.ContextPredicate getEmissiveLightingPredicate();

	@Accessor
	Optional<AbstractBlock.Offsetter> getOffsetter();

	@Accessor
	RegistryKey<LootTable> getLootTableKey();

	@Accessor
	boolean getBlockBreakParticles();

	@Accessor
	FeatureSet getRequiredFeatures();

	@Accessor
	boolean getBurnable();

	@Accessor
	boolean getLiquid();

	@Accessor
	boolean getForceNotSolid();

	@Accessor
	boolean getForceSolid();

	@Accessor
	PistonBehavior getPistonBehavior();

	@Accessor
	NoteBlockInstrument getInstrument();

	@Accessor
	boolean getReplaceable();

	/* SETTERS */
	@Accessor
	void setCollidable(boolean collidable);

	@Accessor
	void setRandomTicks(boolean ticksRandomly);

	@Accessor
	void setMapColorProvider(Function<BlockState, MapColor> mapColorProvider);

	@Accessor
	void setDynamicBounds(boolean dynamicBounds);

	@Accessor
	void setOpaque(boolean opaque);

	@Accessor
	void setIsAir(boolean isAir);

	@Accessor
	void setLootTableKey(RegistryKey<LootTable> lootTableKey);

	@Accessor
	void setToolRequired(boolean toolRequired);

	@Accessor
	void setBlockBreakParticles(boolean blockBreakParticles);

	@Accessor
	void setRequiredFeatures(FeatureSet requiredFeatures);

	@Accessor
	void setOffsetter(Optional<AbstractBlock.Offsetter> offsetter);

	@Accessor
	void setBurnable(boolean burnable);

	@Accessor
	void setLiquid(boolean liquid);

	@Accessor
	void setForceNotSolid(boolean forceNotSolid);

	@Accessor
	void setForceSolid(boolean forceSolid);

	@Accessor
	void setReplaceable(boolean replaceable);
}
