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

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SignBlock;
import net.minecraft.block.WallSignBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.item.Item;
import net.minecraft.item.SignItem;
import net.minecraft.util.Identifier;
import net.minecraft.util.SignType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.sign.SignTypeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;

public class TealSignTest implements ModInitializer {
	public static final SignType TEAL_TYPE = SignTypeRegistry.registerSignType(ObjectBuilderTestConstants.id("teal"));
	public static final SignBlock TEAL_SIGN = new SignBlock(FabricBlockSettings.copy(Blocks.OAK_SIGN), TEAL_TYPE) {
		@Override
		public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
			return new TealSign(pos, state);
		}
	};
	public static final WallSignBlock TEAL_WALL_SIGN = new WallSignBlock(FabricBlockSettings.copy(Blocks.OAK_SIGN), TEAL_TYPE) {
		@Override
		public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
			return new TealSign(pos, state);
		}
	};
	public static final SignItem TEAL_SIGN_ITEM = new SignItem(new Item.Settings(), TEAL_SIGN, TEAL_WALL_SIGN);
	public static final BlockEntityType<TealSign> TEST_SIGN_BLOCK_ENTITY = FabricBlockEntityTypeBuilder.create(TealSign::new, TEAL_SIGN, TEAL_WALL_SIGN).build();

	@Override
	public void onInitialize() {
		Registry.register(Registry.BLOCK, ObjectBuilderTestConstants.id("teal_sign"), TEAL_SIGN);
		Registry.register(Registry.BLOCK, ObjectBuilderTestConstants.id("teal_wall_sign"), TEAL_WALL_SIGN);
		Registry.register(Registry.ITEM, ObjectBuilderTestConstants.id("teal_sign"), TEAL_SIGN_ITEM);
		Registry.register(Registry.BLOCK_ENTITY_TYPE, ObjectBuilderTestConstants.id("teal_sign"), TEST_SIGN_BLOCK_ENTITY);
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
}
