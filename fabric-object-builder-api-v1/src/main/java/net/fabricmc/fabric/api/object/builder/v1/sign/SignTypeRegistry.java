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

package net.fabricmc.fabric.api.object.builder.v1.sign;

import net.minecraft.block.BlockSetType;
import net.minecraft.block.WoodType;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.object.builder.v1.block.type.WoodTypeRegistry;

/**
 * @deprecated Use {@link WoodTypeRegistry}.
 */
@Deprecated
public final class SignTypeRegistry {
	private SignTypeRegistry() {
	}

	/**
	 * Creates and registers a {@link WoodType}.
	 *
	 * @param id the id of this {@link WoodType}
	 * @param setType the {@link BlockSetType} for this wood type
	 * @return a new {@link WoodType}.
	 * @deprecated Use {@link WoodTypeRegistry#registerWoodType(Identifier, BlockSetType)}
	 */
	@Deprecated
	public static WoodType registerSignType(Identifier id, BlockSetType type) {
		return WoodTypeRegistry.registerWoodType(id, type);
	}

	/**
	 * Creates and registers a {@link WoodType}.
	 *
	 * @param id the id of this {@link WoodType}
	 * @param setType the {@link BlockSetType} for this wood type
	 * @param soundType the {@link BlockSoundGroup} for this wood type
	 * @param hangingSignSoundType the {@link BlockSoundGroup} for this wood type's hanging sign
	 * @param fenceGateClose the {@link SoundEvent} for when this wood type's fence gate closes
	 * @param fenceGateOpen the {@link SoundEvent} for when this wood type's fence gate opens
	 * @return a new {@link WoodType}.
	 * @deprecated Use {@link WoodTypeRegistry#registerWoodType(Identifier, BlockSetType, BlockSoundGroup, BlockSoundGroup, SoundEvent, SoundEvent)}
	 */
	@Deprecated
	public static WoodType registerSignType(Identifier id, BlockSetType type, BlockSoundGroup soundType, BlockSoundGroup hangingSignSoundType, SoundEvent fenceGateClose, SoundEvent fenceGateOpen) {
		return WoodTypeRegistry.registerWoodType(id, type, soundType, hangingSignSoundType, fenceGateClose, fenceGateOpen);
	}
}
