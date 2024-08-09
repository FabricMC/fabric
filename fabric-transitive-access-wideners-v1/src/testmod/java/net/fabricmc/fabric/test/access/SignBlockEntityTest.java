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

package net.fabricmc.fabric.test.access;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SignBlock;
import net.minecraft.block.WallSignBlock;
import net.minecraft.block.WoodType;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.item.Item;
import net.minecraft.item.SignItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;

public final class SignBlockEntityTest implements ModInitializer {
	public static final String MOD_ID = "fabric-transitive-access-wideners-v1-testmod";
	public static final SignBlock TEST_SIGN = new SignBlock(WoodType.OAK, AbstractBlock.Settings.copy(Blocks.OAK_SIGN)) {
		@Override
		public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
			return new TestSign(pos, state);
		}
	};
	public static final WallSignBlock TEST_WALL_SIGN = new WallSignBlock(WoodType.OAK, AbstractBlock.Settings.copy(Blocks.OAK_SIGN)) {
		@Override
		public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
			return new TestSign(pos, state);
		}
	};
	public static final SignItem TEST_SIGN_ITEM = new SignItem(new Item.Settings(), TEST_SIGN, TEST_WALL_SIGN);
	public static final BlockEntityType<TestSign> TEST_SIGN_BLOCK_ENTITY = FabricBlockEntityTypeBuilder.create(TestSign::new, TEST_SIGN, TEST_WALL_SIGN).build();

	@Override
	public void onInitialize() {
		Registry.register(Registries.BLOCK, Identifier.of(MOD_ID, "test_sign"), TEST_SIGN);
		Registry.register(Registries.BLOCK, Identifier.of(MOD_ID, "test_wall_sign"), TEST_WALL_SIGN);
		Registry.register(Registries.ITEM, Identifier.of(MOD_ID, "test_sign"), TEST_SIGN_ITEM);
		Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of(MOD_ID, "test_sign"), TEST_SIGN_BLOCK_ENTITY);
	}

	public static class TestSign extends SignBlockEntity {
		public TestSign(BlockPos pos, BlockState state) {
			super(TEST_SIGN_BLOCK_ENTITY, pos, state);
		}

		@Override
		public BlockEntityType<?> getType() {
			return TEST_SIGN_BLOCK_ENTITY;
		}
	}
}
