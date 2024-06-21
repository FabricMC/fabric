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

package net.fabricmc.fabric.api.object.builder.v1.block;

import java.util.function.Function;
import java.util.function.ToIntFunction;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.MapColor;
import net.minecraft.block.enums.NoteBlockInstrument;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.EntityType;
import net.minecraft.loot.LootTable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.resource.featuretoggle.FeatureFlag;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.DyeColor;

import net.fabricmc.fabric.mixin.object.builder.AbstractBlockAccessor;
import net.fabricmc.fabric.mixin.object.builder.AbstractBlockSettingsAccessor;

/**
 * @deprecated replace with {@link AbstractBlock.Settings}
 */
@Deprecated
public class FabricBlockSettings extends AbstractBlock.Settings {
	protected FabricBlockSettings() {
		super();
	}

	protected FabricBlockSettings(AbstractBlock.Settings settings) {
		this();
		// Mostly Copied from vanilla's copy method
		// Note: If new methods are added to Block settings, an accessor must be added here
		AbstractBlockSettingsAccessor thisAccessor = (AbstractBlockSettingsAccessor) this;
		AbstractBlockSettingsAccessor otherAccessor = (AbstractBlockSettingsAccessor) settings;

		// Copied in vanilla: sorted by vanilla copy order
		this.hardness(otherAccessor.getHardness());
		this.resistance(otherAccessor.getResistance());
		this.collidable(otherAccessor.getCollidable());
		thisAccessor.setRandomTicks(otherAccessor.getRandomTicks());
		this.luminance(otherAccessor.getLuminance());
		thisAccessor.setMapColorProvider(otherAccessor.getMapColorProvider());
		this.sounds(otherAccessor.getSoundGroup());
		this.slipperiness(otherAccessor.getSlipperiness());
		this.velocityMultiplier(otherAccessor.getVelocityMultiplier());
		thisAccessor.setDynamicBounds(otherAccessor.getDynamicBounds());
		thisAccessor.setOpaque(otherAccessor.getOpaque());
		thisAccessor.setIsAir(otherAccessor.getIsAir());
		thisAccessor.setBurnable(otherAccessor.getBurnable());
		thisAccessor.setLiquid(otherAccessor.getLiquid());
		thisAccessor.setForceNotSolid(otherAccessor.getForceNotSolid());
		thisAccessor.setForceSolid(otherAccessor.getForceSolid());
		this.pistonBehavior(otherAccessor.getPistonBehavior());
		thisAccessor.setToolRequired(otherAccessor.isToolRequired());
		thisAccessor.setOffsetter(otherAccessor.getOffsetter());
		thisAccessor.setBlockBreakParticles(otherAccessor.getBlockBreakParticles());
		thisAccessor.setRequiredFeatures(otherAccessor.getRequiredFeatures());
		this.emissiveLighting(otherAccessor.getEmissiveLightingPredicate());
		this.instrument(otherAccessor.getInstrument());
		thisAccessor.setReplaceable(otherAccessor.getReplaceable());

		// Vanilla did not copy those fields until 23w45a, which introduced
		// copyShallow method (maintaining the behavior previously used by the copy method)
		// and the copy method that copies those fields as well. copyShallow is now
		// deprecated. To maintain compatibility and since this behavior seems to be the
		// more proper way, this copies all the fields, not just the shallow ones.
		// Fields are added by field definition order.
		this.jumpVelocityMultiplier(otherAccessor.getJumpVelocityMultiplier());
		this.drops(otherAccessor.getLootTableKey());
		this.allowsSpawning(otherAccessor.getAllowsSpawningPredicate());
		this.solidBlock(otherAccessor.getSolidBlockPredicate());
		this.suffocates(otherAccessor.getSuffocationPredicate());
		this.blockVision(otherAccessor.getBlockVisionPredicate());
		this.postProcess(otherAccessor.getPostProcessPredicate());
	}

	/**
	 * @deprecated replace with {@link AbstractBlock.Settings#create()}
	 */
	@Deprecated
	public static FabricBlockSettings create() {
		return new FabricBlockSettings();
	}

	/**
	 * @deprecated replace with {@link AbstractBlock.Settings#create()}
	 */
	@Deprecated
	public static FabricBlockSettings of() {
		return create();
	}

	/**
	 * @deprecated replace with {@link AbstractBlock.Settings#copy(AbstractBlock)}
	 */
	@Deprecated
	public static FabricBlockSettings copyOf(AbstractBlock block) {
		return new FabricBlockSettings(((AbstractBlockAccessor) block).getSettings());
	}

	/**
	 * @deprecated replace with {@link AbstractBlock.Settings#copy(AbstractBlock)}
	 */
	@Deprecated
	public static FabricBlockSettings copyOf(AbstractBlock.Settings settings) {
		return new FabricBlockSettings(settings);
	}

	@Deprecated
	@Override
	public FabricBlockSettings noCollision() {
		super.noCollision();
		return this;
	}

	@Deprecated
	@Override
	public FabricBlockSettings nonOpaque() {
		super.nonOpaque();
		return this;
	}

	@Deprecated
	@Override
	public FabricBlockSettings slipperiness(float value) {
		super.slipperiness(value);
		return this;
	}

	@Deprecated
	@Override
	public FabricBlockSettings velocityMultiplier(float velocityMultiplier) {
		super.velocityMultiplier(velocityMultiplier);
		return this;
	}

	@Deprecated
	@Override
	public FabricBlockSettings jumpVelocityMultiplier(float jumpVelocityMultiplier) {
		super.jumpVelocityMultiplier(jumpVelocityMultiplier);
		return this;
	}

	@Deprecated
	@Override
	public FabricBlockSettings sounds(BlockSoundGroup group) {
		super.sounds(group);
		return this;
	}

	/**
	 * @deprecated Please use {@link FabricBlockSettings#luminance(ToIntFunction)}.
	 */
	@Deprecated
	public FabricBlockSettings lightLevel(ToIntFunction<BlockState> levelFunction) {
		return this.luminance(levelFunction);
	}

	@Deprecated
	@Override
	public FabricBlockSettings luminance(ToIntFunction<BlockState> luminanceFunction) {
		super.luminance(luminanceFunction);
		return this;
	}

	@Deprecated
	@Override
	public FabricBlockSettings strength(float hardness, float resistance) {
		super.strength(hardness, resistance);
		return this;
	}

	@Deprecated
	@Override
	public FabricBlockSettings breakInstantly() {
		super.breakInstantly();
		return this;
	}

	@Deprecated
	@Override
	public FabricBlockSettings strength(float strength) {
		super.strength(strength);
		return this;
	}

	@Deprecated
	@Override
	public FabricBlockSettings ticksRandomly() {
		super.ticksRandomly();
		return this;
	}

	@Deprecated
	@Override
	public FabricBlockSettings dynamicBounds() {
		super.dynamicBounds();
		return this;
	}

	@Deprecated
	@Override
	public FabricBlockSettings dropsNothing() {
		super.dropsNothing();
		return this;
	}

	@Deprecated
	@Override
	public FabricBlockSettings dropsLike(Block block) {
		super.dropsLike(block);
		return this;
	}

	@Deprecated
	@Override
	public FabricBlockSettings air() {
		super.air();
		return this;
	}

	@Deprecated
	@Override
	public FabricBlockSettings allowsSpawning(AbstractBlock.TypedContextPredicate<EntityType<?>> predicate) {
		super.allowsSpawning(predicate);
		return this;
	}

	@Deprecated
	@Override
	public FabricBlockSettings solidBlock(AbstractBlock.ContextPredicate predicate) {
		super.solidBlock(predicate);
		return this;
	}

	@Deprecated
	@Override
	public FabricBlockSettings suffocates(AbstractBlock.ContextPredicate predicate) {
		super.suffocates(predicate);
		return this;
	}

	@Deprecated
	@Override
	public FabricBlockSettings blockVision(AbstractBlock.ContextPredicate predicate) {
		super.blockVision(predicate);
		return this;
	}

	@Deprecated
	@Override
	public FabricBlockSettings postProcess(AbstractBlock.ContextPredicate predicate) {
		super.postProcess(predicate);
		return this;
	}

	@Deprecated
	@Override
	public FabricBlockSettings emissiveLighting(AbstractBlock.ContextPredicate predicate) {
		super.emissiveLighting(predicate);
		return this;
	}

	/**
	 * Make the block require tool to drop and slows down mining speed if the incorrect tool is used.
	 */
	@Deprecated
	@Override
	public FabricBlockSettings requiresTool() {
		super.requiresTool();
		return this;
	}

	@Deprecated
	@Override
	public FabricBlockSettings mapColor(MapColor color) {
		super.mapColor(color);
		return this;
	}

	@Deprecated
	@Override
	public FabricBlockSettings hardness(float hardness) {
		super.hardness(hardness);
		return this;
	}

	@Deprecated
	@Override
	public FabricBlockSettings resistance(float resistance) {
		super.resistance(resistance);
		return this;
	}

	@Deprecated
	@Override
	public FabricBlockSettings offset(AbstractBlock.OffsetType offsetType) {
		super.offset(offsetType);
		return this;
	}

	@Deprecated
	@Override
	public FabricBlockSettings noBlockBreakParticles() {
		super.noBlockBreakParticles();
		return this;
	}

	@Deprecated
	@Override
	public FabricBlockSettings requires(FeatureFlag... features) {
		super.requires(features);
		return this;
	}

	@Deprecated
	@Override
	public FabricBlockSettings mapColor(Function<BlockState, MapColor> mapColorProvider) {
		super.mapColor(mapColorProvider);
		return this;
	}

	@Deprecated
	@Override
	public FabricBlockSettings burnable() {
		super.burnable();
		return this;
	}

	@Deprecated
	@Override
	public FabricBlockSettings liquid() {
		super.liquid();
		return this;
	}

	@Deprecated
	@Override
	public FabricBlockSettings solid() {
		super.solid();
		return this;
	}

	@Deprecated
	@Override
	public FabricBlockSettings notSolid() {
		super.notSolid();
		return this;
	}

	@Deprecated
	@Override
	public FabricBlockSettings pistonBehavior(PistonBehavior pistonBehavior) {
		super.pistonBehavior(pistonBehavior);
		return this;
	}

	@Deprecated
	@Override
	public FabricBlockSettings instrument(NoteBlockInstrument instrument) {
		super.instrument(instrument);
		return this;
	}

	@Deprecated
	@Override
	public FabricBlockSettings replaceable() {
		super.replaceable();
		return this;
	}

	/* FABRIC ADDITIONS*/

	/**
	 * @deprecated Please use {@link FabricBlockSettings#luminance(int)}.
	 */
	@Deprecated
	public FabricBlockSettings lightLevel(int lightLevel) {
		this.luminance(lightLevel);
		return this;
	}

	/**
	 * @deprecated replace with {@link AbstractBlock.Settings#luminance(ToIntFunction)}
	 */
	@Deprecated
	public FabricBlockSettings luminance(int luminance) {
		this.luminance(ignored -> luminance);
		return this;
	}

	@Deprecated
	public FabricBlockSettings drops(RegistryKey<LootTable> dropTableId) {
		((AbstractBlockSettingsAccessor) this).setLootTableKey(dropTableId);
		return this;
	}

	/* FABRIC DELEGATE WRAPPERS */

	/**
	 * @deprecated Please migrate to {@link AbstractBlock.Settings#mapColor(MapColor)}
	 */
	@Deprecated
	public FabricBlockSettings materialColor(MapColor color) {
		return this.mapColor(color);
	}

	/**
	 * @deprecated Please migrate to {@link AbstractBlock.Settings#mapColor(DyeColor)}
	 */
	@Deprecated
	public FabricBlockSettings materialColor(DyeColor color) {
		return this.mapColor(color);
	}

	/**
	 * @deprecated Please migrate to {@link AbstractBlock.Settings#mapColor(DyeColor)}
	 */
	@Deprecated
	public FabricBlockSettings mapColor(DyeColor color) {
		return this.mapColor(color.getMapColor());
	}

	@Deprecated
	public FabricBlockSettings collidable(boolean collidable) {
		((AbstractBlockSettingsAccessor) this).setCollidable(collidable);
		return this;
	}
}
