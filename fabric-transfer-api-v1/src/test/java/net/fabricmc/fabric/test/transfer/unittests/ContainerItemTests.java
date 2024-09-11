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

package net.fabricmc.fabric.test.transfer.unittests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.base.SingleStackStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.SlottedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;

class ContainerItemTests extends AbstractTransferApiTest {
	@BeforeAll
	static void beforeAll() {
		bootstrap();
	}

	@Test
	public void emptyShulkerBox() {
		ItemStack stack = new ItemStack(Items.SHULKER_BOX);
		Storage<ItemVariant> storage = ItemStorage.ITEM.find(stack, ContainerItemContext.withConstant(stack));

		Assertions.assertInstanceOf(SlottedStorage.class, storage);
		Assertions.assertEquals(27, ((SlottedStorage<ItemVariant>) storage).getSlotCount());
	}

	@Test
	public void insertAndExtractShulkerBox() {
		var sourceStorage = new SingleStackStorage() {
			public ItemStack stack = new ItemStack(Items.SHULKER_BOX);

			@Override
			protected ItemStack getStack() {
				return stack;
			}

			@Override
			public void setStack(ItemStack stack) {
				this.stack = stack;
			}
		};

		Storage<ItemVariant> storage = ItemStorage.ITEM.find(sourceStorage.stack, ContainerItemContext.ofSingleSlot(sourceStorage));

		Assertions.assertNotNull(storage, "Shulker Box didn't have a Storage<ItemVariant>");

		try (var tx = Transaction.openOuter()) {
			Assertions.assertEquals(20, storage.insert(ItemVariant.of(Items.NETHER_STAR), 20, tx));
			tx.commit();
		}

		try (var tx = Transaction.openOuter()) {
			Assertions.assertEquals(20, storage.extract(ItemVariant.of(Items.NETHER_STAR), 64, tx));
			tx.commit();
		}
	}
}
