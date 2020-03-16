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

/**
 * @deprecated Please migrate to v1.
 */
@Deprecated
public final class BlockSettingsExtensions {
	private BlockSettingsExtensions() {
	}

	public static void breakByHand(Settings settings, boolean breakByHand) {
		net.fabricmc.fabric.api.block.v1.BlockSettingsExtensions.breakByHand(settings, breakByHand);
	}

	public static void breakByTool(Settings settings, Tag<Item> tag, int miningLevel) {
		net.fabricmc.fabric.api.block.v1.BlockSettingsExtensions.breakByTool(settings, tag, miningLevel);
	}

	public static void hardness(Settings settings, float hardness) {
		net.fabricmc.fabric.api.block.v1.BlockSettingsExtensions.hardness(settings, hardness);
	}

	public static void resistance(Settings settings, float resistance) {
		net.fabricmc.fabric.api.block.v1.BlockSettingsExtensions.resistance(settings, resistance);
	}

	public static void collidable(Settings settings, boolean collidable) {
		net.fabricmc.fabric.api.block.v1.BlockSettingsExtensions.collidable(settings, collidable);
	}

	public static void materialColor(Settings settings, MaterialColor materialColor) {
		net.fabricmc.fabric.api.block.v1.BlockSettingsExtensions.materialColor(settings, materialColor);
	}

	public static void drops(Settings settings, Identifier dropTableId) {
		net.fabricmc.fabric.api.block.v1.BlockSettingsExtensions.drops(settings, dropTableId);
	}

	public static void sounds(Settings settings, BlockSoundGroup soundGroup) {
		net.fabricmc.fabric.api.block.v1.BlockSettingsExtensions.sounds(settings, soundGroup);
	}

	public static void lightLevel(Settings settings, int lightLevel) {
		net.fabricmc.fabric.api.block.v1.BlockSettingsExtensions.lightLevel(settings, lightLevel);
	}

	public static void breakInstantly(Settings settings) {
		net.fabricmc.fabric.api.block.v1.BlockSettingsExtensions.breakInstantly(settings);
	}

	public static void strength(Settings settings, float strength) {
		net.fabricmc.fabric.api.block.v1.BlockSettingsExtensions.strength(settings, strength);
	}

	public static void ticksRandomly(Settings settings) {
		net.fabricmc.fabric.api.block.v1.BlockSettingsExtensions.ticksRandomly(settings);
	}

	public static void dynamicBounds(Settings settings) {
		net.fabricmc.fabric.api.block.v1.BlockSettingsExtensions.dynamicBounds(settings);
	}

	public static void dropsNothing(Settings settings) {
		net.fabricmc.fabric.api.block.v1.BlockSettingsExtensions.dropsNothing(settings);
	}
}
