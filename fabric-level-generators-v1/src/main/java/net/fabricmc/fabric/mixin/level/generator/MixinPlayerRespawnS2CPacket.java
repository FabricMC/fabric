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
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.client.network.packet.PlayerRespawnS2CPacket;
import net.minecraft.world.level.LevelGeneratorType;

import net.fabricmc.fabric.impl.level.generator.FabricLevelGeneratorType;

@Mixin(PlayerRespawnS2CPacket.class)
public class MixinPlayerRespawnS2CPacket {
	@Shadow
	private LevelGeneratorType generatorType;

	@Redirect(method = "write", at = @At(value = "FIELD", target = "Lnet/minecraft/client/network/packet/PlayerRespawnS2CPacket;generatorType:Lnet/minecraft/world/level/LevelGeneratorType;"))
	private LevelGeneratorType changeSentLevelGeneratorType(PlayerRespawnS2CPacket playerRespawnS2CPacket) {
		return FabricLevelGeneratorType.checkForFabricLevelGeneratorType(generatorType);
	}
}
