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

package net.fabricmc.fabric.mixin.level.generator;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.minecraft.client.network.packet.GameJoinS2CPacket;

import net.fabricmc.fabric.impl.level.generator.FabricLevelGeneratorType;

@Mixin(GameJoinS2CPacket.class)
public class MixinGameJoinS2CPacket {
	@ModifyConstant(method = "read", constant = @Constant(intValue = 16))
	private int changeBufferSize(int original) {
		return FabricLevelGeneratorType.getLongestNameLength();
	}
}
