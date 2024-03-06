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

package net.fabricmc.fabric.test.object.builder;

import net.minecraft.block.BlockSetType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HangingSignBlock;
import net.minecraft.block.SignBlock;
import net.minecraft.block.WallHangingSignBlock;
import net.minecraft.block.WallSignBlock;
import net.minecraft.block.WoodType;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.HangingSignBlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.item.HangingSignItem;
import net.minecraft.item.Item;
import net.minecraft.item.SignItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.object.builder.v1.block.type.BlockSetTypeBuilder;
import net.fabricmc.fabric.api.object.builder.v1.block.type.WoodTypeBuilder;

public class TealSignTest implements ModInitializer {
	public static final Identifier TEAL_TYPE_ID = ObjectBuilderTestConstants.id("teal");
	public static final BlockSetType TEAL_BLOCK_SET_TYPE = BlockSetTypeBuilder.copyOf(BlockSetType.OAK).build(TEAL_TYPE_ID);
	public static final WoodType TEAL_WOOD_TYPE = WoodTypeBuilder.copyOf(WoodType.OAK).build(TEAL_TYPE_ID, TEAL_BLOCK_SET_TYPE);
	public static final SignBlock TEAL_SIGN = new SignBlock(TEAL_WOOD_TYPE, FabricBlockSettings.copy(Blocks.OAK_SIGN)) {
		@Override
		public TealSign createBlockEntity(BlockPos pos, BlockState state) {
			return new TealSign(pos, state);
		}
	};
	public static final WallSignBlock TEAL_WALL_SIGN = new WallSignBlock(TEAL_WOOD_TYPE, FabricBlockSettings.copy(Blocks.OAK_SIGN)) {
		@Override
		public TealSign createBlockEntity(BlockPos pos, BlockState state) {
			return new TealSign(pos, state);
		}
	};
	public static final HangingSignBlock TEAL_HANGING_SIGN = new HangingSignBlock(TEAL_WOOD_TYPE, FabricBlockSettings.copy(Blocks.OAK_HANGING_SIGN)) {
		@Override
		public TealHangingSign createBlockEntity(BlockPos pos, BlockState state) {
			return new TealHangingSign(pos, state);
		}
	};
	public static final WallHangingSignBlock TEAL_WALL_HANGING_SIGN = new WallHangingSignBlock(TEAL_WOOD_TYPE, FabricBlockSettings.copy(Blocks.OAK_HANGING_SIGN)) {
		@Override
		public TealHangingSign createBlockEntity(BlockPos pos, BlockState state) {
			return new TealHangingSign(pos, state);
		}
	};
	public static final SignItem TEAL_SIGN_ITEM = new SignItem(new Item.Settings(), TEAL_SIGN, TEAL_WALL_SIGN);
	public static final HangingSignItem TEAL_HANGING_SIGN_ITEM = new HangingSignItem(TEAL_HANGING_SIGN, TEAL_WALL_HANGING_SIGN, new Item.Settings());
	public static final BlockEntityType<TealSign> TEST_SIGN_BLOCK_ENTITY = FabricBlockEntityTypeBuilder.create(TealSign::new, TEAL_SIGN, TEAL_WALL_SIGN).build();
	public static final BlockEntityType<TealHangingSign> TEST_HANGING_SIGN_BLOCK_ENTITY = FabricBlockEntityTypeBuilder.create(TealHangingSign::new, TEAL_HANGING_SIGN, TEAL_WALL_HANGING_SIGN).build();

	@Override
	public void onInitialize() {
		WoodType.register(TEAL_WOOD_TYPE);

		Registry.register(Registries.BLOCK, ObjectBuilderTestConstants.id("teal_sign"), TEAL_SIGN);
		Registry.register(Registries.BLOCK, ObjectBuilderTestConstants.id("teal_wall_sign"), TEAL_WALL_SIGN);
		Registry.register(Registries.BLOCK, ObjectBuilderTestConstants.id("teal_hanging_sign"), TEAL_HANGING_SIGN);
		Registry.register(Registries.BLOCK, ObjectBuilderTestConstants.id("teal_wall_hanging_sign"), TEAL_WALL_HANGING_SIGN);

		Registry.register(Registries.ITEM, ObjectBuilderTestConstants.id("teal_sign"), TEAL_SIGN_ITEM);
		Registry.register(Registries.ITEM, ObjectBuilderTestConstants.id("teal_hanging_sign"), TEAL_HANGING_SIGN_ITEM);

		Registry.register(Registries.BLOCK_ENTITY_TYPE, ObjectBuilderTestConstants.id("teal_sign"), TEST_SIGN_BLOCK_ENTITY);
		Registry.register(Registries.BLOCK_ENTITY_TYPE, ObjectBuilderTestConstants.id("teal_hanging_sign"), TEST_HANGING_SIGN_BLOCK_ENTITY);
	}

	public static class TealSign extends SignBlockEntity {
		public TealSign(BlockPos pos, BlockState state) {
			super(pos, state);
		}

		@Override
		public BlockEntityType<?> getType() {
			return TEST_SIGN_BLOCK_ENTITY;
		}
	}

	public static class TealHangingSign extends HangingSignBlockEntity {
		public TealHangingSign(BlockPos pos, BlockState state) {
			super(pos, state);
		}

		@Override
		public BlockEntityType<?> getType() {
			return TEST_HANGING_SIGN_BLOCK_ENTITY;
		}
	}
}
