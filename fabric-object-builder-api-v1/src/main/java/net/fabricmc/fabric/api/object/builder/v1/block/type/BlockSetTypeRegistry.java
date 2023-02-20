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

package net.fabricmc.fabric.api.object.builder.v1.block.type;

import net.minecraft.block.BlockSetType;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

/**
 * This class allows registering {@link BlockSetType}s.
 *
 * <p>A {@link BlockSetType} is used to tell the game what sounds various related blocks should use.
 *
 * @see WoodTypeRegistry
 */
public final class BlockSetTypeRegistry {
	private BlockSetTypeRegistry() {
	}

	/**
	 * Creates and registers a {@link BlockSetType} with the regular wood sounds.
	 *
	 * @param id the id of this {@link BlockSetType}
	 * @return a new {@link BlockSetType}
	 */
	public static BlockSetType registerWood(Identifier id) {
		return BlockSetType.register(new BlockSetType(id.toString()));
	}

	/**
	 * Creates and registers a {@link BlockSetType}.
	 *
	 * @param id the id of this {@link BlockSetType}
	 * @param soundType the {@link BlockSoundGroup} for this block set
	 * @param doorClose the {@link SoundEvent} for when this block set's door closes
	 * @param doorOpen the {@link SoundEvent} for when this block set's door opens
	 * @param trapdoorClose the {@link SoundEvent} for when this block set's trapdoor closes
	 * @param trapdoorOpen the {@link SoundEvent} for when this block set's trapdoor opens
	 * @param pressurePlateClickOff the {@link SoundEvent} for when this block set's pressure plate is unpressed
	 * @param pressurePlateClickOn the {@link SoundEvent} for when this block set's pressure plate is pressed
	 * @param buttonClickOff the {@link SoundEvent} for when this block set's button is unpressed
	 * @param buttonClickOn the {@link SoundEvent} for when this block set's button is pressed
	 * @return a new {@link BlockSetType}
	 */
	public static BlockSetType register(Identifier id, BlockSoundGroup soundType, SoundEvent doorClose, SoundEvent doorOpen, SoundEvent trapdoorClose, SoundEvent trapdoorOpen, SoundEvent pressurePlateClickOff, SoundEvent pressurePlateClickOn, SoundEvent buttonClickOff, SoundEvent buttonClickOn) {
		return BlockSetType.register(new BlockSetType(id.toString(), soundType, doorClose, doorOpen, trapdoorClose, trapdoorOpen, pressurePlateClickOff, pressurePlateClickOn, buttonClickOff, buttonClickOn));
	}
}
