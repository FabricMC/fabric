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

package net.fabricmc.fabric.api.block.v1;

import net.minecraft.block.Block;
import net.minecraft.block.MaterialColor;
import net.minecraft.item.Item;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.mixin.object.builder.BlockSettingsAccessor;

public final class BlockSettingsExtensions {
	private BlockSettingsExtensions() {
	}

	public static void breakByHand(Block.Settings settings, boolean breakByHand) {
		FabricBlockSettings.computeExtraData(settings).breakByHand(breakByHand);
	}

	public static void breakByTool(Block.Settings settings, Tag<Item> tag, int miningLevel) {
		FabricBlockSettings.computeExtraData(settings).addMiningLevel(tag, miningLevel);
	}

	public static void hardness(Block.Settings settings, float hardness) {
		((BlockSettingsAccessor) settings).setHardness(hardness);
	}

	public static void resistance(Block.Settings settings, float resistance) {
		((BlockSettingsAccessor) settings).setResistance(Math.max(0.0F, resistance));
	}

	public static void collidable(Block.Settings settings, boolean collidable) {
		((BlockSettingsAccessor) settings).setCollidable(collidable);
	}

	public static void materialColor(Block.Settings settings, MaterialColor materialColor) {
		((BlockSettingsAccessor) settings).setMaterialColor(materialColor);
	}

	public static void drops(Block.Settings settings, Identifier dropTableId) {
		((BlockSettingsAccessor) settings).setDropTableId(dropTableId);
	}

	public static void sounds(Block.Settings settings, BlockSoundGroup soundGroup) {
		((BlockSettingsAccessor) settings).invokeSounds(soundGroup);
	}

	public static void lightLevel(Block.Settings settings, int lightLevel) {
		((BlockSettingsAccessor) settings).invokeLightLevel(lightLevel);
	}

	public static void breakInstantly(Block.Settings settings) {
		((BlockSettingsAccessor) settings).invokeBreakInstantly();
	}

	public static void strength(Block.Settings settings, float strength) {
		((BlockSettingsAccessor) settings).invokeStrength(strength);
	}

	public static void ticksRandomly(Block.Settings settings) {
		((BlockSettingsAccessor) settings).invokeTicksRandomly();
	}

	public static void dynamicBounds(Block.Settings settings) {
		((BlockSettingsAccessor) settings).invokeHasDynamicBounds();
	}

	public static void dropsNothing(Block.Settings settings) {
		((BlockSettingsAccessor) settings).invokeDropsNothing();
	}
}
