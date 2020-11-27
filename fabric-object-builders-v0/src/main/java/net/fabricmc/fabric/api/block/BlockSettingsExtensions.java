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

import net.minecraft.block.AbstractBlock.Settings;
import net.minecraft.block.MaterialColor;
import net.minecraft.item.Item;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.impl.object.builder.FabricBlockInternals;
import net.fabricmc.fabric.mixin.object.builder.AbstractBlockSettingsAccessor;

/**
 * @deprecated Please migrate to v1. Please use methods in {@link net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings} instead.
 */
@Deprecated
public final class BlockSettingsExtensions {
	private BlockSettingsExtensions() {
	}

	public static void breakByHand(Settings settings, boolean breakByHand) {
		FabricBlockInternals.computeExtraData(settings).breakByHand(breakByHand);
	}

	public static void breakByTool(Settings settings, Tag<Item> tag, int miningLevel) {
		FabricBlockInternals.computeExtraData(settings).addMiningLevel(tag, miningLevel);
	}

	public static void hardness(Settings settings, float hardness) {
		((AbstractBlockSettingsAccessor) settings).setHardness(hardness);
	}

	public static void resistance(Settings settings, float resistance) {
		((AbstractBlockSettingsAccessor) settings).setResistance(Math.max(0.0F, resistance));
	}

	public static void collidable(Settings settings, boolean collidable) {
		((AbstractBlockSettingsAccessor) settings).setCollidable(collidable);
	}

	public static void materialColor(Settings settings, MaterialColor materialColor) {
		((AbstractBlockSettingsAccessor) settings).setMaterialColorFactory(ignored -> materialColor);
	}

	public static void drops(Settings settings, Identifier dropTableId) {
		((AbstractBlockSettingsAccessor) settings).setLootTableId(dropTableId);
	}

	public static void sounds(Settings settings, BlockSoundGroup soundGroup) {
		((AbstractBlockSettingsAccessor) settings).invokeSounds(soundGroup);
	}

	public static void lightLevel(Settings settings, int lightLevel) {
		((AbstractBlockSettingsAccessor) settings).setLuminanceFunction(ignored -> lightLevel);
	}

	public static void breakInstantly(Settings settings) {
		((AbstractBlockSettingsAccessor) settings).invokeBreakInstantly();
	}

	public static void strength(Settings settings, float strength) {
		((AbstractBlockSettingsAccessor) settings).invokeStrength(strength);
	}

	public static void ticksRandomly(Settings settings) {
		((AbstractBlockSettingsAccessor) settings).invokeTicksRandomly();
	}

	public static void dynamicBounds(Settings settings) {
		// Thanks Mixin
		((AbstractBlockSettingsAccessor) settings).setDynamicBounds(true);
	}

	public static void dropsNothing(Settings settings) {
		((AbstractBlockSettingsAccessor) settings).invokeDropsNothing();
	}
}
