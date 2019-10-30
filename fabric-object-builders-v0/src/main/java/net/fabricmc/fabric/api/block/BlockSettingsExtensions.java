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

import net.minecraft.block.Block.Settings;
import net.minecraft.block.MaterialColor;
import net.minecraft.item.Item;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.mixin.object.builder.BlockSettingsHooks;

public final class BlockSettingsExtensions {
	private BlockSettingsExtensions() {
	}

	public static void breakByHand(Settings settings, boolean breakByHand) {
		FabricBlockSettings.computeExtraData(settings).breakByHand(breakByHand);
	}

	public static void breakByTool(Settings settings, Tag<Item> tag, int miningLevel) {
		FabricBlockSettings.computeExtraData(settings).addMiningLevel(tag, miningLevel);
	}

	public static void hardness(Settings settings, float hardness) {
		((BlockSettingsHooks) settings).setHardness(hardness);
	}

	public static void resistance(Settings settings, float resistance) {
		((BlockSettingsHooks) settings).setResistance(Math.max(0.0F, resistance));
	}

	public static void collidable(Settings settings, boolean collidable) {
		((BlockSettingsHooks) settings).setCollidable(collidable);
	}

	public static void materialColor(Settings settings, MaterialColor materialColor) {
		((BlockSettingsHooks) settings).setMaterialColor(materialColor);
	}

	public static void drops(Settings settings, Identifier dropTableId) {
		((BlockSettingsHooks) settings).setDropTableId(dropTableId);
	}

	public static void sounds(Settings settings, BlockSoundGroup soundGroup) {
		((BlockSettingsHooks) settings).invokeSounds(soundGroup);
	}

	public static void lightLevel(Settings settings, int lightLevel) {
		((BlockSettingsHooks) settings).invokeLightLevel(lightLevel);
	}

	public static void breakInstantly(Settings settings) {
		((BlockSettingsHooks) settings).invokeBreakInstantly();
	}

	public static void strength(Settings settings, float strength) {
		((BlockSettingsHooks) settings).invokeStrength(strength);
	}

	public static void ticksRandomly(Settings settings) {
		((BlockSettingsHooks) settings).invokeTicksRandomly();
	}

	public static void dynamicBounds(Settings settings) {
		((BlockSettingsHooks) settings).invokeHasDynamicBounds();
	}

	public static void dropsNothing(Settings settings) {
		((BlockSettingsHooks) settings).invokeDropsNothing();
	}
}
