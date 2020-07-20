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

import java.util.function.Function;

import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.MaterialColor;
import net.minecraft.item.Item;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.tag.Tag;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;

/**
 * @deprecated Please migrate to v1. Please use {@link net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings} instead
 */
@Deprecated
public class FabricBlockSettings {
	protected final net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings delegate;

	protected FabricBlockSettings(Material material, MaterialColor color) {
		this(Block.Settings.of(material, color));
	}

	protected FabricBlockSettings(Block base) {
		this(Block.Settings.copy(base));
	}

	protected FabricBlockSettings(final Block.Settings delegate) {
		this.delegate = net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings.copyOf(delegate);
	}

	public static FabricBlockSettings of(Material material) {
		return of(material, material.getColor());
	}

	public static FabricBlockSettings of(Material material, MaterialColor color) {
		return new FabricBlockSettings(material, color);
	}

	public static FabricBlockSettings of(Material material, DyeColor color) {
		return new FabricBlockSettings(material, color.getMaterialColor());
	}

	public static FabricBlockSettings copy(Block base) {
		return new FabricBlockSettings(base);
	}

	public static FabricBlockSettings copyOf(Block.Settings settings) {
		return new FabricBlockSettings(settings);
	}

	/* FABRIC HELPERS */

	/**
	 * Makes the block breakable by any tool if {@code breakByHand} is set to true.
	 */
	public FabricBlockSettings breakByHand(boolean breakByHand) {
		this.delegate.breakByHand(breakByHand);
		return this;
	}

	/**
	 * Please make the block require a tool if you plan to disable drops and slow the breaking down using the
	 * incorrect tool by using {@link net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings#requiresTool()}.
	 */
	public FabricBlockSettings breakByTool(Tag<Item> tag, int miningLevel) {
		this.delegate.breakByTool(tag, miningLevel);
		return this;
	}

	/**
	 * Please make the block require a tool if you plan to disable drops and slow the breaking down using the
	 * incorrect tool by using {@link net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings#requiresTool()}.
	 */
	public FabricBlockSettings breakByTool(Tag<Item> tag) {
		this.delegate.breakByTool(tag);
		return this;
	}

	/* DELEGATE WRAPPERS */

	public FabricBlockSettings materialColor(MaterialColor color) {
		this.delegate.materialColor(color);
		return this;
	}

	public FabricBlockSettings materialColor(DyeColor color) {
		this.delegate.materialColor(color.getMaterialColor());
		return this;
	}

	public FabricBlockSettings collidable(boolean collidable) {
		this.delegate.collidable(collidable);
		return this;
	}

	public FabricBlockSettings noCollision() {
		this.delegate.noCollision();
		return this;
	}

	public FabricBlockSettings nonOpaque() {
		this.delegate.nonOpaque();
		return this;
	}

	public FabricBlockSettings sounds(BlockSoundGroup group) {
		this.delegate.sounds(group);
		return this;
	}

	public FabricBlockSettings ticksRandomly() {
		this.delegate.ticksRandomly();
		return this;
	}

	public FabricBlockSettings lightLevel(int lightLevel) {
		this.delegate.lightLevel(lightLevel);
		return this;
	}

	public FabricBlockSettings hardness(float hardness) {
		this.delegate.hardness(hardness);
		return this;
	}

	public FabricBlockSettings resistance(float resistance) {
		this.delegate.resistance(resistance);
		return this;
	}

	public FabricBlockSettings strength(float hardness, float resistance) {
		this.delegate.strength(hardness, resistance);
		return this;
	}

	public FabricBlockSettings breakInstantly() {
		this.delegate.breakInstantly();
		return this;
	}

	public FabricBlockSettings dropsNothing() {
		this.delegate.dropsNothing();
		return this;
	}

	public FabricBlockSettings dropsLike(Block block) {
		this.delegate.dropsLike(block);
		return this;
	}

	public FabricBlockSettings drops(Identifier dropTableId) {
		this.delegate.drops(dropTableId);
		return this;
	}

	@Deprecated
	public FabricBlockSettings friction(float friction) {
		this.delegate.slipperiness(friction);
		return this;
	}

	public FabricBlockSettings slipperiness(float value) {
		this.delegate.slipperiness(value);
		return this;
	}

	public FabricBlockSettings dynamicBounds() {
		this.delegate.dynamicBounds();
		return this;
	}

	/* BUILDING LOGIC */

	public Block.Settings build() {
		return this.delegate;
	}

	public <T> T build(Function<Block.Settings, T> function) {
		return function.apply(this.delegate);
	}
}
