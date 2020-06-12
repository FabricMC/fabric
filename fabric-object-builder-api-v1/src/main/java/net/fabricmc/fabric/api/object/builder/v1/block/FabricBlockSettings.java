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

import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.MaterialColor;
import net.minecraft.item.Item;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.tag.Tag;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.impl.object.builder.BlockSettingsInternals;
import net.fabricmc.fabric.impl.object.builder.FabricBlockInternals;
import net.fabricmc.fabric.mixin.object.builder.BlockSettingsAccessor;
import net.fabricmc.fabric.mixin.object.builder.BlockAccessor;

/**
 * Fabric's version of Block.Settings. Adds additional methods and hooks
 * not found in the original class.
 *
 * <p>To use it, simply replace Block.Settings.of() with
 * FabricBlockSettings.of().
 */
public class FabricBlockSettings extends Block.Settings {
	protected FabricBlockSettings(Material material, MaterialColor color) {
		super(material, color);
	}

	protected FabricBlockSettings(Block.Settings settings) {
		super(((BlockSettingsAccessor) settings).getMaterial(), ((BlockSettingsAccessor) settings).getMaterialColor());
		// Mostly Copied from vanilla's copy method
		BlockSettingsAccessor thisAccessor = (BlockSettingsAccessor) this;
		BlockSettingsAccessor otherAccessor = (BlockSettingsAccessor) settings;

		thisAccessor.setMaterial(otherAccessor.getMaterial());
		this.hardness(otherAccessor.getHardness());
		this.resistance(otherAccessor.getResistance());
		this.collidable(otherAccessor.getCollidable());
		thisAccessor.setRandomTicks(otherAccessor.getRandomTicks());
		this.lightLevel(otherAccessor.getLuminance());
		thisAccessor.setMaterialColor(otherAccessor.getMaterialColor());
		this.sounds(otherAccessor.getSoundGroup());
		this.slipperiness(otherAccessor.getSlipperiness());
		thisAccessor.setDynamicBounds(otherAccessor.getDynamicBounds());

		// Now attempt to copy fabric specific data
		BlockSettingsInternals otherInternals = (BlockSettingsInternals) settings;
		FabricBlockInternals.ExtraData extraData = otherInternals.getExtraData();

		if (extraData != null) { // If present, populate the extra data on our new settings
			((BlockSettingsInternals) this).setExtraData(extraData);
		}
	}

	public static FabricBlockSettings of(Material material) {
		return of(material, material.getColor());
	}

	public static FabricBlockSettings of(Material material, MaterialColor color) {
		return new FabricBlockSettings(material, color);
	}

	public static FabricBlockSettings of(Material material, DyeColor color) {
		return new FabricBlockSettings(material, color.getMaterialColor());
	}

	public static FabricBlockSettings copyOf(Block block) {
		BlockAccessor sourceAccessor = (BlockAccessor) block;

		FabricBlockSettings settings = of(sourceAccessor.getMaterial(), sourceAccessor.getMaterialColor());
		BlockSettingsAccessor settingsAccessor = (BlockSettingsAccessor) settings;

		settingsAccessor.setMaterial(sourceAccessor.getMaterial());
		settings.hardness(sourceAccessor.getHardness());
		settings.resistance(sourceAccessor.getResistance());
		settings.collidable(sourceAccessor.getCollidable());
		settingsAccessor.setRandomTicks(sourceAccessor.getRandomTicks());
		settings.lightLevel(sourceAccessor.getLightLevel());
		settingsAccessor.setMaterialColor(sourceAccessor.getMaterialColor());
		settings.sounds(sourceAccessor.getSoundGroup());
		settings.slipperiness(block.getSlipperiness());
		settingsAccessor.setDynamicBounds(sourceAccessor.getDynamicBounds());

		// Now attempt to copy fabric specific data
		BlockSettingsInternals otherInternals = (BlockSettingsInternals) settings;
		FabricBlockInternals.ExtraData extraData = otherInternals.getExtraData();

		if (extraData != null) { // If present, populate the extra data on our new settings
			((BlockSettingsInternals) settings).setExtraData(extraData);
		}

		return settings;
	}

	public static FabricBlockSettings copyOf(Block.Settings settings) {
		return new FabricBlockSettings(settings);
	}

	@Override
	public FabricBlockSettings noCollision() {
		super.noCollision();
		return this;
	}

	@Override
	public FabricBlockSettings slipperiness(float value) {
		super.slipperiness(value);
		return this;
	}

	@Override
	public FabricBlockSettings sounds(BlockSoundGroup group) {
		super.sounds(group);
		return this;
	}

	@Override
	public FabricBlockSettings lightLevel(int luminance) {
		super.lightLevel(luminance);
		return this;
	}

	@Override
	public FabricBlockSettings strength(float hardness, float resistance) {
		super.strength(hardness, resistance);
		return this;
	}

	@Override
	public FabricBlockSettings breakInstantly() {
		super.breakInstantly();
		return this;
	}

	public FabricBlockSettings strength(float strength) {
		super.strength(strength);
		return this;
	}

	@Override
	public FabricBlockSettings ticksRandomly() {
		super.ticksRandomly();
		return this;
	}

	@Override
	public FabricBlockSettings dropsNothing() {
		super.dropsNothing();
		return this;
	}

	@Override
	public FabricBlockSettings dropsLike(Block block) {
		super.dropsLike(block);
		return this;
	}

	/* FABRIC ADDITIONS*/

	public FabricBlockSettings hardness(float hardness) {
		((BlockSettingsAccessor) this).setHardness(hardness);
		return this;
	}

	public FabricBlockSettings resistance(float resistance) {
		((BlockSettingsAccessor) this).setResistance(Math.max(0.0F, resistance));
		return this;
	}

	public FabricBlockSettings drops(Identifier dropTableId) {
		((BlockSettingsAccessor) this).setDropTableId(dropTableId);
		return this;
	}

	public FabricBlockSettings dynamicBounds() {
		((BlockSettingsAccessor) this).setDynamicBounds(true);
		return this;
	}

	/* FABRIC DELEGATE WRAPPERS */

	public FabricBlockSettings materialColor(MaterialColor color) {
		((BlockSettingsAccessor) this).setMaterialColor(color);
		return this;
	}

	public FabricBlockSettings materialColor(DyeColor color) {
		return this.materialColor(color.getMaterialColor());
	}

	public FabricBlockSettings collidable(boolean collidable) {
		((BlockSettingsAccessor) this).setCollidable(collidable);
		return this;
	}

	/* FABRIC HELPERS */

	public FabricBlockSettings breakByHand(boolean breakByHand) {
		FabricBlockInternals.computeExtraData(this).breakByHand(breakByHand);
		return this;
	}

	public FabricBlockSettings breakByTool(Tag<Item> tag, int miningLevel) {
		FabricBlockInternals.computeExtraData(this).addMiningLevel(tag, miningLevel);
		return this;
	}

	public FabricBlockSettings breakByTool(Tag<Item> tag) {
		return this.breakByTool(tag, 0);
	}
}
