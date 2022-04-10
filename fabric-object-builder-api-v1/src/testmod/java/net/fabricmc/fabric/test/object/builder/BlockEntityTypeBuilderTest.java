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
import net.minecraft.item.ItemGroup;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;

public class BlockEntityTypeBuilderTest implements ModInitializer {
	private static final Identifier INITIAL_BETRAYAL_BLOCK_ID = ObjectBuilderTestConstants.id("initial_betrayal_block");
	private static final Block INITIAL_BETRAYAL_BLOCK = new BetrayalBlock(MapColor.BLUE);

	private static final Identifier ADDED_BETRAYAL_BLOCK_ID = ObjectBuilderTestConstants.id("added_betrayal_block");
	private static final Block ADDED_BETRAYAL_BLOCK = new BetrayalBlock(MapColor.GREEN);

	private static final Identifier FIRST_MULTI_BETRAYAL_BLOCK_ID = ObjectBuilderTestConstants.id("first_multi_betrayal_block");
	private static final Block FIRST_MULTI_BETRAYAL_BLOCK = new BetrayalBlock(MapColor.RED);

	private static final Identifier SECOND_MULTI_BETRAYAL_BLOCK_ID = ObjectBuilderTestConstants.id("second_multi_betrayal_block");
	private static final Block SECOND_MULTI_BETRAYAL_BLOCK = new BetrayalBlock(MapColor.YELLOW);

	private static final Identifier BLOCK_ENTITY_TYPE_ID = ObjectBuilderTestConstants.id("betrayal_block");
	public static final BlockEntityType<?> BLOCK_ENTITY_TYPE = FabricBlockEntityTypeBuilder.create(BetrayalBlockEntity::new, INITIAL_BETRAYAL_BLOCK)
			.addBlock(ADDED_BETRAYAL_BLOCK)
			.addBlocks(FIRST_MULTI_BETRAYAL_BLOCK, SECOND_MULTI_BETRAYAL_BLOCK)
			.build();

	@Override
	public void onInitialize() {
		register(INITIAL_BETRAYAL_BLOCK_ID, INITIAL_BETRAYAL_BLOCK);
		register(ADDED_BETRAYAL_BLOCK_ID, ADDED_BETRAYAL_BLOCK);
		register(FIRST_MULTI_BETRAYAL_BLOCK_ID, FIRST_MULTI_BETRAYAL_BLOCK);
		register(SECOND_MULTI_BETRAYAL_BLOCK_ID, SECOND_MULTI_BETRAYAL_BLOCK);

		Registry.register(Registry.BLOCK_ENTITY_TYPE, BLOCK_ENTITY_TYPE_ID, BLOCK_ENTITY_TYPE);
	}

	private static void register(Identifier id, Block block) {
		Registry.register(Registry.BLOCK, id, block);

		Item item = new BlockItem(block, new Item.Settings().group(ItemGroup.MISC));
		Registry.register(Registry.ITEM, id, item);
	}

	private static class BetrayalBlock extends Block implements BlockEntityProvider {
		private BetrayalBlock(MapColor color) {
			super(AbstractBlock.Settings.copy(Blocks.STONE).mapColor(color));
		}

		@Override
		public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
			if (!world.isClient()) {
				BlockEntity blockEntity = world.getBlockEntity(pos);

				if (blockEntity == null) {
					throw new AssertionError("Missing block entity for betrayal block at " + pos);
				} else if (!BLOCK_ENTITY_TYPE.equals(blockEntity.getType())) {
					Identifier id = BlockEntityType.getId(blockEntity.getType());
					throw new AssertionError("Incorrect block entity for betrayal block at " + pos + ": " + id);
				}

				Text posText = new TranslatableText("chat.coordinates", pos.getX(), pos.getY(), pos.getZ());
				Text message = new TranslatableText("text.fabric-object-builder-api-v1-testmod.block_entity_type_success", posText, BLOCK_ENTITY_TYPE_ID);

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
