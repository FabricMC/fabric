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

package net.fabricmc.fabric.helpers;

import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.MapColor;
import net.minecraft.world.loot.LootTables;

import java.util.function.Function;

public class FabricBlockBuilder {
	public interface Delegate {
		void fabric_setMapColor(MapColor color);
		void fabric_setCollidable(boolean value);
		void fabric_setSoundGroup(BlockSoundGroup group);
		void fabric_setLuminance(int value);
		void fabric_setHardness(float value);
		void fabric_setResistance(float value);
		void fabric_setRandomTicks(boolean value);
		void fabric_setFriction(float value);
		void fabric_setDropTable(Identifier id);
	}

	private final Block.Builder delegate;
	private final FabricBlockBuilder.Delegate castDelegate;

	protected FabricBlockBuilder(Material material) {
		delegate = Block.Builder.create(material);
		castDelegate = (FabricBlockBuilder.Delegate) delegate;
	}

	protected FabricBlockBuilder(Block base) {
		delegate = Block.Builder.copy(base);
		castDelegate = (FabricBlockBuilder.Delegate) delegate;
	}

	public static FabricBlockBuilder create(Material material) {
		return new FabricBlockBuilder(material);
	}

	public static FabricBlockBuilder copy(Block base) {
		return new FabricBlockBuilder(base);
	}

	public FabricBlockBuilder setMapColor(MapColor color) {
		castDelegate.fabric_setMapColor(color);
		return this;
	}

	public FabricBlockBuilder setMapColor(DyeColor color) {
		castDelegate.fabric_setMapColor(color.getMapColor());
		return this;
	}

	public FabricBlockBuilder setCollidable(boolean value) {
		castDelegate.fabric_setCollidable(value);
		return this;
	}

	public FabricBlockBuilder setSoundGroup(BlockSoundGroup group) {
		castDelegate.fabric_setSoundGroup(group);
		return this;
	}

	public FabricBlockBuilder acceptRandomTicks() {
		castDelegate.fabric_setRandomTicks(true);
		return this;
	}

	public FabricBlockBuilder setLuminance(int value) {
		castDelegate.fabric_setLuminance(value);
		return this;
	}

	public FabricBlockBuilder setHardness(float value) {
		castDelegate.fabric_setHardness(value);
		castDelegate.fabric_setResistance(value);
		return this;
	}

	public FabricBlockBuilder setResistance(float value) {
		castDelegate.fabric_setResistance(value);
		return this;
	}

	public FabricBlockBuilder setStrength(float hardness, float resistance) {
		castDelegate.fabric_setHardness(hardness);
		castDelegate.fabric_setResistance(resistance);
		return this;
	}

	public FabricBlockBuilder noDropTable() {
		castDelegate.fabric_setDropTable(LootTables.EMPTY);
		return this;
	}

	public FabricBlockBuilder copyDropTable(Block block) {
		castDelegate.fabric_setDropTable(block.getDropTableId());
		return this;
	}

	public FabricBlockBuilder setDropTable(Identifier id) {
		castDelegate.fabric_setDropTable(id);
		return this;
	}

	public FabricBlockBuilder setFrictionCoefficient(float value) {
		castDelegate.fabric_setFriction(value);
		return this;
	}

	public Block.Builder build() {
		return delegate;
	}

	public <T> T build(Function<Block.Builder, T> function) {
		return function.apply(delegate);
	}
}
