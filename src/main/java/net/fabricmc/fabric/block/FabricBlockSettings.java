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

package net.fabricmc.fabric.block;

import net.fabricmc.fabric.tools.ToolManager;
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
	public interface Delegate {
		void fabric_setMaterialColor(MaterialColor color);
		void fabric_setCollidable(boolean value);
		void fabric_setSoundGroup(BlockSoundGroup group);
		void fabric_setLightLevel(int value);
		void fabric_setHardness(float value);
		void fabric_setResistance(float value);
		void fabric_setRandomTicks(boolean value);
		void fabric_setFriction(float value);
		void fabric_setDropTable(Identifier id);
	}

	protected final Block.Settings delegate;
	private final FabricBlockSettings.Delegate castDelegate;

	protected FabricBlockSettings(Material material, MaterialColor color) {
		this(Block.Settings.create(material, color));
	}

	protected FabricBlockSettings(Block base) {
		this(Block.Settings.copy(base));
	}

	protected FabricBlockSettings(final Block.Settings delegate) {
		this.delegate = delegate;
		castDelegate = (FabricBlockSettings.Delegate) delegate;
	}

	public static FabricBlockSettings of(Material material) {
		return of(material, material.getColor());
	}

	public static FabricBlockSettings of(Material material, MaterialColor color) {
		return new FabricBlockSettings(material, color);
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

	/* BUILDING LOGIC */

	public Block.Settings build() {
		return delegate;
	}

	public <T> T build(Function<Block.Settings, T> function) {
		return function.apply(delegate);
	}

	/* DEPRECATIONS */

	/**
	 * @deprecated Use {@link #of(Material) of} instead.
	 */
	@Deprecated
	public static FabricBlockSettings create(Material material) {
		return of(material);
	}

	/**
	 * @deprecated Use {@link #breakByHand(boolean) breakByHand} instead.
	 */
	@Deprecated
	public FabricBlockSettings setBreakByHand(boolean value) {
		return this.breakByHand(value);
	}

	/**
	 * @deprecated Use {@link #breakByTool(Tag) breakByTool} instead.
	 */
	@Deprecated
	public FabricBlockSettings setBreakByTool(Tag<Item> tag) {
		return this.breakByTool(tag);
	}

	/**
	 * @deprecated Use {@link #breakByTool(Tag, int) breakByTool} instead.
	 */
	@Deprecated
	public FabricBlockSettings setBreakByTool(Tag<Item> tag, int miningLevel) {
		return this.breakByTool(tag, miningLevel);
	}

	/**
	 * @deprecated Use {@link #setMaterialColor(MaterialColor) setMaterialColor} instead.
	 */
	@Deprecated
	public FabricBlockSettings setMapColor(MaterialColor color) {
		return this.materialColor(color);
	}

	/**
	 * @deprecated Use {@link #setMaterialColor(DyeColor) setMaterialColor} instead.
	 */
	@Deprecated
	public FabricBlockSettings setMapColor(DyeColor color) {
		return this.materialColor(color);
	}

	/**
	 * @deprecated Use {@link #materialColor(DyeColor) materialColor} instead.
	 */
	@Deprecated
	public FabricBlockSettings setMaterialColor(MaterialColor color) {
		return this.materialColor(color);
	}

	/**
	 * @deprecated Use {@link #materialColor(DyeColor) materialColor} instead.
	 */
	@Deprecated
	public FabricBlockSettings setMaterialColor(DyeColor color) {
		return this.materialColor(color);
	}

	/**
	 * @deprecated Use {@link #collidable(boolean) collidable} instead.
	 */
	@Deprecated
	public FabricBlockSettings setCollidable(boolean value) {
		castDelegate.fabric_setCollidable(value);
		return this;
	}

	/**
	 * @deprecated Use {@link #sounds(BlockSoundGroup) sounds} instead.
	 */
	@Deprecated
	public FabricBlockSettings setSoundGroup(BlockSoundGroup group) {
		return this.sounds(group);
	}

	/**
	 * @deprecated Use {@link #ticksRandomly() ticksRandomly} instead.
	 */
	@Deprecated
	public FabricBlockSettings acceptRandomTicks() {
		return this.ticksRandomly();
	}

	/**
	 * @deprecated Use {@link #lightLevel(int) lightLevel} instead.
	 */
	@Deprecated
	public FabricBlockSettings setLuminance(int value) {
		return this.lightLevel(value);
	}

	/**
	 * @deprecated Use {@link #hardness(float) hardness} instead.
	 */
	@Deprecated
	public FabricBlockSettings setHardness(float value) {
		return this.hardness(value);
	}

	/**
	 * @deprecated Use {@link #resistance(float) resistance} instead.
	 */
	@Deprecated
	public FabricBlockSettings setResistance(float value) {
		return this.resistance(value);
	}

	/**
	 * @deprecated Use {@link #strength(float, float) strength} instead.
	 */
	@Deprecated
	public FabricBlockSettings setStrength(float hardness, float resistance) {
		return this.strength(hardness, resistance);
	}

	/**
	 * @deprecated Use {@link #dropsNothing() dropsNothing} instead.
	 */
	@Deprecated
	public FabricBlockSettings noDropTable() {
		return this.dropsNothing();
	}

	/**
	 * @deprecated Use {@link #dropsLike(Block) dropsLike} instead.
	 */
	@Deprecated
	public FabricBlockSettings copyDropTable(Block block) {
		return this.dropsLike(block);
	}

	/**
	 * @deprecated Use {@link #drops(Identifier) drops} instead.
	 */
	@Deprecated
	public FabricBlockSettings setDropTable(Identifier id) {
		return this.drops(id);
	}

	/**
	 * @deprecated Use {@link #friction(float) friction} instead.
	 */
	@Deprecated
	public FabricBlockSettings setFrictionCoefficient(float value) {
		return this.friction(value);
	}
}
