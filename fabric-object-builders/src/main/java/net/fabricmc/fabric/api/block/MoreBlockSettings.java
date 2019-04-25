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

import net.fabricmc.fabric.mixin.builders.BlockSettingsHooks;
import net.minecraft.block.Block.Settings;
import net.minecraft.block.MaterialColor;
import net.minecraft.item.Item;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

public final class MoreBlockSettings {
    private MoreBlockSettings() {
    }

    public static Settings breakByHand(Settings settings, boolean breakByHand) {
        FabricBlockSettings.computeExtraData(settings).breakByHand(breakByHand);
        return settings;
    }

    public static Settings breakByTool(Settings settings, Tag<Item> tag, int miningLevel) {
        FabricBlockSettings.computeExtraData(settings).addMiningLevel(tag, miningLevel);
        return settings;
    }

    public static Settings hardness(Settings settings, float hardness) {
        ((BlockSettingsHooks) settings).setHardness(hardness);
        return settings;
    }

    public static Settings resistance(Settings settings, float resistance) {
        ((BlockSettingsHooks) settings).setResistance(resistance);
        return settings;
    }

    public static Settings collidable(Settings settings, boolean collidable) {
        ((BlockSettingsHooks) settings).setCollidable(collidable);
        return settings;
    }

    public static Settings materialColor(Settings settings, MaterialColor materialColor) {
        ((BlockSettingsHooks) settings).setMaterialColor(materialColor);
        return settings;
    }

    public static Settings drops(Settings settings, Identifier dropTableId) {
        ((BlockSettingsHooks) settings).setDropTableId(dropTableId);
        return settings;
    }

    public static Settings sounds(Settings settings, BlockSoundGroup soundGroup) {
        return ((BlockSettingsHooks) settings).invokeSounds(soundGroup);
    }

    public static Settings lightLevel(Settings settings, int lightLevel) {
        return ((BlockSettingsHooks) settings).invokeLightLevel(lightLevel);
    }

    public static Settings breakInstantly(Settings settings) {
        return ((BlockSettingsHooks) settings).invokeBreakInstantly();
    }

    public static Settings strength(Settings settings, float strength) {
        return ((BlockSettingsHooks) settings).invokeStrength(strength);
    }

    public static Settings ticksRandomly(Settings settings) {
        return ((BlockSettingsHooks) settings).invokeTicksRandomly();
    }

    public static Settings dynamicBounds(Settings settings) {
        return ((BlockSettingsHooks) settings).invokeDynamicBounds();
    }

    public static Settings dropNothing(Settings settings) {
        return ((BlockSettingsHooks) settings).invokeDropNothing();
    }
}
