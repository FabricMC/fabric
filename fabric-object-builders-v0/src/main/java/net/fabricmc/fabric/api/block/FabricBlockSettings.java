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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.MaterialColor;
import net.minecraft.item.Item;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.tag.Tag;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.event.registry.BlockConstructedCallback;
import net.fabricmc.fabric.impl.mining.level.ToolManager;

/**
 * Fabric's version of Block.Settings. Adds additional methods and hooks
 * not found in the original class.
 *
 * <p>To use it, simply replace Block.Settings.create() with
 * FabricBlockSettings.create() and add .build() at the end to return the
 * vanilla Block.Settings instance beneath.
 */
public class FabricBlockSettings {
	static {
		BlockConstructedCallback.EVENT.register(FabricBlockSettings::onBuild);
	}

	private static final Map<Block.Settings, ExtraData> EXTRA_DATA = new HashMap<>();

	protected final Block.Settings delegate;

	static final class ExtraData {
		private final List<MiningLevel> miningLevels = new ArrayList<>();
		/* @Nullable */ private Boolean breakByHand;

		private ExtraData(Block.Settings settings) {
		}

		void breakByHand(boolean breakByHand) {
			this.breakByHand = breakByHand;
		}

		void addMiningLevel(Tag<Item> tag, int level) {
			miningLevels.add(new MiningLevel(tag, level));
		}
	}

	private static final class MiningLevel {
		private final Tag<Item> tag;
		private final int level;

		MiningLevel(Tag<Item> tag, int level) {
			this.tag = tag;
			this.level = level;
		}
	}

	static ExtraData computeExtraData(Block.Settings settings) {
		return EXTRA_DATA.computeIfAbsent(settings, ExtraData::new);
	}

	private static void onBuild(Block.Settings settings, Block block) {
		// TODO: Load only if fabric-mining-levels present
		ExtraData data = EXTRA_DATA.get(settings);

		if (data != null) {
			if (data.breakByHand != null) {
				ToolManager.entry(block).setBreakByHand(data.breakByHand);
			}

			for (MiningLevel tml : data.miningLevels) {
				ToolManager.entry(block).putBreakByTool(tml.tag, tml.level);
			}
		}
	}

	protected FabricBlockSettings(Material material, MaterialColor color) {
		this(Block.Settings.of(material, color));
	}

	protected FabricBlockSettings(Block base) {
		this(Block.Settings.copy(base));
	}

	protected FabricBlockSettings(final Block.Settings delegate) {
		this.delegate = delegate;
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

	public static FabricBlockSettings copyOf(Block.Settings settings) {
		return new FabricBlockSettings(settings);
	}

	/* FABRIC HELPERS */

	public FabricBlockSettings breakByHand(boolean breakByHand) {
		computeExtraData(delegate).breakByHand(breakByHand);
		return this;
	}

	public FabricBlockSettings breakByTool(Tag<Item> tag, int miningLevel) {
		computeExtraData(delegate).addMiningLevel(tag, miningLevel);
		return this;
	}

	public FabricBlockSettings breakByTool(Tag<Item> tag) {
		return breakByTool(tag, 0);
	}

	/* DELEGATE WRAPPERS */

	public FabricBlockSettings materialColor(MaterialColor color) {
		BlockSettingsExtensions.materialColor(delegate, color);
		return this;
	}

	public FabricBlockSettings materialColor(DyeColor color) {
		return materialColor(color.getMaterialColor());
	}

	public FabricBlockSettings collidable(boolean collidable) {
		BlockSettingsExtensions.collidable(delegate, collidable);
		return this;
	}

	public FabricBlockSettings noCollision() {
		delegate.noCollision();
		return this;
	}

	public FabricBlockSettings nonOpaque() {
		delegate.nonOpaque();
		return this;
	}

	public FabricBlockSettings sounds(BlockSoundGroup group) {
		BlockSettingsExtensions.sounds(delegate, group);
		return this;
	}

	public FabricBlockSettings ticksRandomly() {
		BlockSettingsExtensions.ticksRandomly(delegate);
		return this;
	}

	public FabricBlockSettings lightLevel(int lightLevel) {
		BlockSettingsExtensions.lightLevel(delegate, lightLevel);
		return this;
	}

	public FabricBlockSettings hardness(float hardness) {
		BlockSettingsExtensions.hardness(delegate, hardness);
		return this;
	}

	public FabricBlockSettings resistance(float resistance) {
		BlockSettingsExtensions.resistance(delegate, resistance);
		return this;
	}

	public FabricBlockSettings strength(float hardness, float resistance) {
		delegate.strength(hardness, resistance);
		return this;
	}

	public FabricBlockSettings breakInstantly() {
		BlockSettingsExtensions.breakInstantly(delegate);
		return this;
	}

	public FabricBlockSettings dropsNothing() {
		BlockSettingsExtensions.dropsNothing(delegate);
		return this;
	}

	public FabricBlockSettings dropsLike(Block block) {
		delegate.dropsLike(block);
		return this;
	}

	public FabricBlockSettings drops(Identifier dropTableId) {
		BlockSettingsExtensions.drops(delegate, dropTableId);
		return this;
	}

	@Deprecated
	public FabricBlockSettings friction(float friction) {
		delegate.slipperiness(friction);
		return this;
	}

	public FabricBlockSettings slipperiness(float value) {
		delegate.slipperiness(value);
		return this;
	}

	public FabricBlockSettings dynamicBounds() {
		BlockSettingsExtensions.dynamicBounds(delegate);
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
