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
import net.minecraft.block.WoodType;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

/**
 * This class allows easy creation of {@link BlockSetType}s.
 *
 * <p>A {@link BlockSetType} is used to tell the game various properties of related blocks, such as what sounds they should use.
 *
 * @see WoodTypeBuilder
 */
public final class BlockSetTypeBuilder {
	private boolean openableByHand = true;
	private BlockSoundGroup soundGroup = BlockSoundGroup.WOOD;
	private SoundEvent doorCloseSound = SoundEvents.BLOCK_WOODEN_DOOR_CLOSE;
	private SoundEvent doorOpenSound = SoundEvents.BLOCK_WOODEN_DOOR_OPEN;
	private SoundEvent trapdoorCloseSound = SoundEvents.BLOCK_WOODEN_TRAPDOOR_CLOSE;
	private SoundEvent trapdoorOpenSound = SoundEvents.BLOCK_WOODEN_TRAPDOOR_OPEN;
	private SoundEvent pressurePlateClickOffSound = SoundEvents.BLOCK_WOODEN_PRESSURE_PLATE_CLICK_OFF;
	private SoundEvent pressurePlateClickOnSound = SoundEvents.BLOCK_WOODEN_PRESSURE_PLATE_CLICK_ON;
	private SoundEvent buttonClickOffSound = SoundEvents.BLOCK_WOODEN_BUTTON_CLICK_OFF;
	private SoundEvent buttonClickOnSound = SoundEvents.BLOCK_WOODEN_BUTTON_CLICK_ON;

	/**
	 * Sets whether this block set type's door and trapdoor can be opened by hand.
	 *
	 * <p>Defaults to {@code true}.
	 *
	 * @return this builder for chaining
	 */
	public BlockSetTypeBuilder openableByHand(boolean openableByHand) {
		this.openableByHand = openableByHand;
		return this;
	}

	/**
	 * Sets this block set type's door and trapdoor to be openable by hand.
	 *
	 * @return this builder for chaining
	 */
	public BlockSetTypeBuilder openableByHand() {
		this.openableByHand = true;
		return this;
	}

	/**
	 * Sets this block set type's door and trapdoor to be unopenable by hand.
	 *
	 * @return this builder for chaining
	 */
	public BlockSetTypeBuilder unopenableByHand() {
		this.openableByHand = false;
		return this;
	}

	/**
	 * Sets this block set type's sound group.
	 *
	 * <p>Defaults to {@link BlockSoundGroup#WOOD}.
	 *
	 * @return this builder for chaining
	 */
	public BlockSetTypeBuilder soundGroup(BlockSoundGroup soundGroup) {
		this.soundGroup = soundGroup;
		return this;
	}

	/**
	 * Sets this block set type's door close sound.
	 *
	 * <p>Defaults to {@link SoundEvents#BLOCK_WOODEN_DOOR_CLOSE}.
	 *
	 * @return this builder for chaining
	 */
	public BlockSetTypeBuilder doorCloseSound(SoundEvent doorCloseSound) {
		this.doorCloseSound = doorCloseSound;
		return this;
	}

	/**
	 * Sets this block set type's door open sound.
	 *
	 * <p>Defaults to {@link SoundEvents#BLOCK_WOODEN_DOOR_OPEN}.
	 *
	 * @return this builder for chaining
	 */
	public BlockSetTypeBuilder doorOpenSound(SoundEvent doorOpenSound) {
		this.doorOpenSound = doorOpenSound;
		return this;
	}

	/**
	 * Sets this block set type's trapdoor close sound.
	 *
	 * <p>Defaults to {@link SoundEvents#BLOCK_WOODEN_TRAPDOOR_CLOSE}.
	 *
	 * @return this builder for chaining
	 */
	public BlockSetTypeBuilder trapdoorCloseSound(SoundEvent trapdoorCloseSound) {
		this.trapdoorCloseSound = trapdoorCloseSound;
		return this;
	}

	/**
	 * Sets this block set type's trapdoor open sound.
	 *
	 * <p>Defaults to {@link SoundEvents#BLOCK_WOODEN_TRAPDOOR_OPEN}.
	 *
	 * @return this builder for chaining
	 */
	public BlockSetTypeBuilder trapdoorOpenSound(SoundEvent trapdoorOpenSound) {
		this.trapdoorOpenSound = trapdoorOpenSound;
		return this;
	}

	/**
	 * Sets this block set type's pressure plate click off sound.
	 *
	 * <p>Defaults to {@link SoundEvents#BLOCK_WOODEN_PRESSURE_PLATE_CLICK_OFF}.
	 *
	 * @return this builder for chaining
	 */
	public BlockSetTypeBuilder pressurePlateClickOffSound(SoundEvent pressurePlateClickOffSound) {
		this.pressurePlateClickOffSound = pressurePlateClickOffSound;
		return this;
	}

	/**
	 * Sets this block set type's pressure plate click on sound.
	 *
	 * <p>Defaults to {@link SoundEvents#BLOCK_WOODEN_PRESSURE_PLATE_CLICK_ON}.
	 *
	 * @return this builder for chaining
	 */
	public BlockSetTypeBuilder pressurePlateClickOnSound(SoundEvent pressurePlateClickOnSound) {
		this.pressurePlateClickOnSound = pressurePlateClickOnSound;
		return this;
	}

	/**
	 * Sets this block set type's button click off sound.
	 *
	 * <p>Defaults to {@link SoundEvents#BLOCK_WOODEN_BUTTON_CLICK_OFF}.
	 *
	 * @return this builder for chaining
	 */
	public BlockSetTypeBuilder buttonClickOffSound(SoundEvent buttonClickOffSound) {
		this.buttonClickOffSound = buttonClickOffSound;
		return this;
	}

	/**
	 * Sets this block set type's button click on sound.
	 *
	 * <p>Defaults to {@link SoundEvents#BLOCK_WOODEN_BUTTON_CLICK_ON}.
	 *
	 * @return this builder for chaining
	 */
	public BlockSetTypeBuilder buttonClickOnSound(SoundEvent buttonClickOnSound) {
		this.buttonClickOnSound = buttonClickOnSound;
		return this;
	}

	/**
	 * Creates a new {@link BlockSetTypeBuilder} that copies all of another builder's values.
	 *
	 * @param builder the {@link BlockSetTypeBuilder} whose values are to be copied
	 *
	 * @return the created copy
	 */
	public static BlockSetTypeBuilder copyOf(BlockSetTypeBuilder builder) {
		BlockSetTypeBuilder copy = new BlockSetTypeBuilder();
		copy.openableByHand(builder.openableByHand);
		copy.soundGroup(builder.soundGroup);
		copy.doorCloseSound(builder.doorCloseSound);
		copy.doorOpenSound(builder.doorOpenSound);
		copy.trapdoorCloseSound(builder.trapdoorCloseSound);
		copy.trapdoorOpenSound(builder.trapdoorOpenSound);
		copy.pressurePlateClickOffSound(builder.pressurePlateClickOffSound);
		copy.pressurePlateClickOnSound(builder.pressurePlateClickOnSound);
		copy.buttonClickOffSound(builder.buttonClickOffSound);
		copy.buttonClickOnSound(builder.buttonClickOnSound);
		return copy;
	}

	/**
	 * Creates a new {@link BlockSetTypeBuilder} that copies all of another block set type's values.
	 *
	 * @param setType the {@link BlockSetType} whose values are to be copied
	 *
	 * @return the created copy
	 */
	public static BlockSetTypeBuilder copyOf(BlockSetType setType) {
		BlockSetTypeBuilder copy = new BlockSetTypeBuilder();
		copy.openableByHand(setType.canOpenByHand());
		copy.soundGroup(setType.soundType());
		copy.doorCloseSound(setType.doorClose());
		copy.doorOpenSound(setType.doorOpen());
		copy.trapdoorCloseSound(setType.trapdoorClose());
		copy.trapdoorOpenSound(setType.trapdoorOpen());
		copy.pressurePlateClickOffSound(setType.pressurePlateClickOff());
		copy.pressurePlateClickOnSound(setType.pressurePlateClickOn());
		copy.buttonClickOffSound(setType.buttonClickOff());
		copy.buttonClickOnSound(setType.buttonClickOn());
		return copy;
	}

	/**
	 * Builds and registers a {@link BlockSetType} from this builder's values.
	 *
	 * @param id the id for the built {@link BlockSetType}
	 *
	 * @return the built and registered {@link BlockSetType}
	 */
	public BlockSetType build(Identifier id) {
		return BlockSetType.register(new BlockSetType(id.toString(), openableByHand, soundGroup, doorCloseSound, doorOpenSound, trapdoorCloseSound, trapdoorOpenSound, pressurePlateClickOffSound, pressurePlateClickOnSound, buttonClickOffSound, buttonClickOnSound));
	}
}
