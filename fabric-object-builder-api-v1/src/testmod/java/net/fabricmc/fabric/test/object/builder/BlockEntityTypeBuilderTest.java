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

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.MapColor;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;

public class BlockEntityTypeBuilderTest implements ModInitializer {
	private static final RegistryKey<Block> INITIAL_BETRAYAL_BLOCK_ID = ObjectBuilderTestConstants.block("initial_betrayal_block");
	static final Block INITIAL_BETRAYAL_BLOCK = createBetrayalBlock(INITIAL_BETRAYAL_BLOCK_ID, MapColor.BLUE);

	private static final RegistryKey<Block> ADDED_BETRAYAL_BLOCK_ID = ObjectBuilderTestConstants.block("added_betrayal_block");
	static final Block ADDED_BETRAYAL_BLOCK = createBetrayalBlock(ADDED_BETRAYAL_BLOCK_ID, MapColor.GREEN);

	private static final RegistryKey<Block> FIRST_MULTI_BETRAYAL_BLOCK_ID = ObjectBuilderTestConstants.block("first_multi_betrayal_block");
	static final Block FIRST_MULTI_BETRAYAL_BLOCK = createBetrayalBlock(FIRST_MULTI_BETRAYAL_BLOCK_ID, MapColor.RED);

	private static final RegistryKey<Block> SECOND_MULTI_BETRAYAL_BLOCK_ID = ObjectBuilderTestConstants.block("second_multi_betrayal_block");
	static final Block SECOND_MULTI_BETRAYAL_BLOCK = createBetrayalBlock(SECOND_MULTI_BETRAYAL_BLOCK_ID, MapColor.YELLOW);

	private static final RegistryKey<Block> BLOCK_ENTITY_TYPE_ID = ObjectBuilderTestConstants.block("betrayal_block");
	public static final BlockEntityType<?> BLOCK_ENTITY_TYPE = FabricBlockEntityTypeBuilder.create(BetrayalBlockEntity::new, INITIAL_BETRAYAL_BLOCK, ADDED_BETRAYAL_BLOCK, FIRST_MULTI_BETRAYAL_BLOCK, SECOND_MULTI_BETRAYAL_BLOCK).build();

	@Override
	public void onInitialize() {
		register(INITIAL_BETRAYAL_BLOCK_ID, INITIAL_BETRAYAL_BLOCK);
		register(ADDED_BETRAYAL_BLOCK_ID, ADDED_BETRAYAL_BLOCK);
		register(FIRST_MULTI_BETRAYAL_BLOCK_ID, FIRST_MULTI_BETRAYAL_BLOCK);
		register(SECOND_MULTI_BETRAYAL_BLOCK_ID, SECOND_MULTI_BETRAYAL_BLOCK);

		Registry.register(Registries.BLOCK_ENTITY_TYPE, BLOCK_ENTITY_TYPE_ID.getValue(), BLOCK_ENTITY_TYPE);
	}

	private static Block createBetrayalBlock(RegistryKey<Block> key, MapColor color) {
		return new BetrayalBlock(AbstractBlock.Settings.copy(Blocks.STONE).mapColor(color).registryKey(key));
	}

	private static void register(RegistryKey<Block> id, Block block) {
		Registry.register(Registries.BLOCK, id, block);

		Item item = new BlockItem(block, new Item.Settings().registryKey(RegistryKey.of(RegistryKeys.ITEM, id.getValue())));
		Registry.register(Registries.ITEM, id.getValue(), item);
	}

	private static class BetrayalBlock extends Block implements BlockEntityProvider {
		private BetrayalBlock(AbstractBlock.Settings settings) {
			super(settings);
		}

		@Override
		public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
			if (!world.isClient()) {
				BlockEntity blockEntity = world.getBlockEntity(pos);

				if (blockEntity == null) {
					throw new AssertionError("Missing block entity for betrayal block at " + pos);
				} else if (!BLOCK_ENTITY_TYPE.equals(blockEntity.getType())) {
					Identifier id = BlockEntityType.getId(blockEntity.getType());
					throw new AssertionError("Incorrect block entity for betrayal block at " + pos + ": " + id);
				}

				Text posText = Text.translatable("chat.coordinates", pos.getX(), pos.getY(), pos.getZ());
				Text message = Text.stringifiedTranslatable("text.fabric-object-builder-api-v1-testmod.block_entity_type_success", posText, BLOCK_ENTITY_TYPE_ID);

				player.sendMessage(message, false);
			}

			return ActionResult.SUCCESS;
		}

		@Override
		public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
			return new BetrayalBlockEntity(pos, state);
		}
	}

	private static class BetrayalBlockEntity extends BlockEntity {
		private BetrayalBlockEntity(BlockPos pos, BlockState state) {
			super(BLOCK_ENTITY_TYPE, pos, state);
		}
	}
}
