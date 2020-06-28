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

package net.fabricmc.fabric.test.screenhandler;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.fabricmc.fabric.test.screenhandler.block.BoxBlock;
import net.fabricmc.fabric.test.screenhandler.block.BoxBlockEntity;
import net.fabricmc.fabric.test.screenhandler.item.BagItem;
import net.fabricmc.fabric.test.screenhandler.item.PositionedBagItem;
import net.fabricmc.fabric.test.screenhandler.screen.BagScreenHandler;
import net.fabricmc.fabric.test.screenhandler.screen.BoxScreenHandler;
import net.fabricmc.fabric.test.screenhandler.screen.PositionedBagScreenHandler;

public class ScreenHandlerTest implements ModInitializer {
	public static final String ID = "fabric-screen-handler-api-v1-testmod";

	public static final Item BAG = new BagItem(new Item.Settings().group(ItemGroup.TOOLS).maxCount(1));
	public static final Item POSITIONED_BAG = new PositionedBagItem(new Item.Settings().group(ItemGroup.TOOLS).maxCount(1));
	public static final Block BOX = new BoxBlock(AbstractBlock.Settings.copy(Blocks.OAK_WOOD));
	public static final Item BOX_ITEM = new BlockItem(BOX, new Item.Settings().group(ItemGroup.DECORATIONS));
	public static final BlockEntityType<BoxBlockEntity> BOX_ENTITY = BlockEntityType.Builder.create(BoxBlockEntity::new, BOX).build(null);
	public static final ScreenHandlerType<BagScreenHandler> BAG_SCREEN_HANDLER = ScreenHandlerRegistry.registerSimple(id("bag"), BagScreenHandler::new);
	public static final ScreenHandlerType<PositionedBagScreenHandler> POSITIONED_BAG_SCREEN_HANDLER = ScreenHandlerRegistry.registerExtended(id("positioned_bag"), PositionedBagScreenHandler::new);
	public static final ScreenHandlerType<BoxScreenHandler> BOX_SCREEN_HANDLER = ScreenHandlerRegistry.registerExtended(id("box"), BoxScreenHandler::new);

	public static Identifier id(String path) {
		return new Identifier(ID, path);
	}

	@Override
	public void onInitialize() {
		Registry.register(Registry.ITEM, id("bag"), BAG);
		Registry.register(Registry.ITEM, id("positioned_bag"), POSITIONED_BAG);
		Registry.register(Registry.BLOCK, id("box"), BOX);
		Registry.register(Registry.ITEM, id("box"), BOX_ITEM);
		Registry.register(Registry.BLOCK_ENTITY_TYPE, id("box"), BOX_ENTITY);
	}
}
