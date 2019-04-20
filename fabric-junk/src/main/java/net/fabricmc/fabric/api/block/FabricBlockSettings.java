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

package net.fabricmc.fabric.api.block;

import net.fabricmc.fabric.impl.block.FabricBlockSettingDelegate;
import net.fabricmc.fabric.impl.tools.ToolManager;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.MaterialColor;
import net.minecraft.item.Item;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.tag.Tag;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.world.loot.LootTables;

import java.util.function.Function;

/**
 * Fabric's version of Block.Settings. Adds additional methods and hooks
 * not found in the original class.
 *
 * To use it, simply replace Block.Settings.create() with
 * FabricBlockSettings.create() and add .build() at the end to return the
 * vanilla Block.Settings instance beneath.
 */
public class FabricBlockSettings {
	protected final Block.Settings delegate;
	private final FabricBlockSettingDelegate castDelegate;

	protected FabricBlockSettings(Material material, MaterialColor color) {
		this(Block.Settings.of(material, color));
	}

	protected FabricBlockSettings(Block base) {
		this(Block.Settings.copy(base));
	}

	protected FabricBlockSettings(final Block.Settings delegate) {
		this.delegate = delegate;
		castDelegate = (FabricBlockSettingDelegate) delegate;
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

	public static FabricBlockSettings copy(Block base) {
		return new FabricBlockSettings(base);
	}

	/* FABRIC HELPERS */

	public FabricBlockSettings breakByHand(boolean value) {
		ToolManager.entry(delegate).setBreakByHand(value);
		return this;
	}

	public FabricBlockSettings breakByTool(Tag<Item> tag) {
		return breakByTool(tag, 0);
	}

	public FabricBlockSettings breakByTool(Tag<Item> tag, int miningLevel) {
		ToolManager.entry(delegate).putBreakByTool(tag, miningLevel);
		return this;
	}

	/* DELEGATE WRAPPERS */

	public FabricBlockSettings materialColor(MaterialColor color) {
		castDelegate.fabric_setMaterialColor(color);
		return this;
	}

	public FabricBlockSettings materialColor(DyeColor color) {
		return this.materialColor(color.getMaterialColor());
	}

	public FabricBlockSettings collidable(boolean value) {
		castDelegate.fabric_setCollidable(value);
		return this;
	}

	public FabricBlockSettings noCollision() {
		return collidable(false);
	}

	public FabricBlockSettings sounds(BlockSoundGroup group) {
		castDelegate.fabric_setSoundGroup(group);
		return this;
	}

	public FabricBlockSettings ticksRandomly() {
		castDelegate.fabric_setRandomTicks(true);
		return this;
	}

	public FabricBlockSettings lightLevel(int value) {
		castDelegate.fabric_setLightLevel(value);
		return this;
	}

	public FabricBlockSettings hardness(float value) {
		castDelegate.fabric_setHardness(value);
		return this;
	}

	public FabricBlockSettings resistance(float value) {
		castDelegate.fabric_setResistance(value);
		return this;
	}

	public FabricBlockSettings strength(float hardness, float resistance) {
		castDelegate.fabric_setHardness(hardness);
		castDelegate.fabric_setResistance(resistance);
		return this;
	}

	public FabricBlockSettings breakInstantly() {
		return hardness(0.0F);
	}

	public FabricBlockSettings dropsNothing() {
		return this.drops(LootTables.EMPTY);
	}

	public FabricBlockSettings dropsLike(Block block) {
		return this.drops(block.getDropTableId());
	}

	public FabricBlockSettings drops(Identifier id) {
		castDelegate.fabric_setDropTable(id);
		return this;
	}

	public FabricBlockSettings friction(float value) {
		castDelegate.fabric_setFriction(value);
		return this;
	}

	public FabricBlockSettings dynamicBounds() {
		castDelegate.fabric_setDynamicBounds(true);
		return this;
	}

	/* BUILDING LOGIC */

	public Block.Settings build() {
		return delegate;
	}

	public <T> T build(Function<Block.Settings, T> function) {
		return function.apply(delegate);
	}
}
