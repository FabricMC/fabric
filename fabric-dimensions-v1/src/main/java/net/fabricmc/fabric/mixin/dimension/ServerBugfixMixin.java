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

package net.fabricmc.fabric.mixin.dimension;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.server.Main;

/**
 * This Mixin aims to solve a Minecraft Vanilla bug where datapacks are ignored during creation of the
 * initial LevelProperties when a dedicated server creates a completely new level.
 *
 * <p>This also includes the datapacks of loaded Fabric mods, and results in modded dimensions only
 * being available after restarting the server, once the world has been created.
 *
 * <p>This Mixin aims to solve this problem by saving and loading the level.dat file once, after
 * a new set of level properties is created. This will apply the same logic as reloading the
 * level.dat after a restart, now including all datapack dimensions.
 *
 * <p>See https://bugs.mojang.com/browse/MC-195468 for a related bug report.
 *
 * <p>In 1.18: Retest if this bug still occurs without this Mixin by launching a dedicated server with the
 * dimension testmod, and no world directory. If the dimension is available (i.e. in /execute in, or via
 * the testmod's commands), then the bug is fixed and this Mixin can be removed.
 */
@Mixin(value = Main.class)
public class ServerBugfixMixin {
	// TODO fix me 22w06a
}
