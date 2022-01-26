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

import static net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants.BUCKET;
import static net.fabricmc.fabric.test.transfer.unittests.TestUtil.assertEquals;

import java.util.List;

import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantItemStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;

public class SingleVariantItemStorageTests {
	private static final FluidVariant LAVA = FluidVariant.of(Fluids.LAVA);

	public static void run() {
		testWaterTank();
	}

	private static void testWaterTank() {
		SimpleInventory inv = new SimpleInventory(new ItemStack(Items.DIAMOND, 2), ItemStack.EMPTY);
		ContainerItemContext ctx = new InventoryContainerItemContext(inv);

		Storage<FluidVariant> storage = createTankStorage(ctx);

		try (Transaction tx = Transaction.openOuter()) {
			// Insertion should succeed and transfer an item into the second slot.
			assertEquals(BUCKET, storage.insert(LAVA, BUCKET, tx));
			// Insertion should create a new stack.
			assertEquals(1, inv.getStack(0).getCount());
			assertEquals(null, inv.getStack(0).getTag());
			assertEquals(1, inv.getStack(1).getCount());
			assertEquals(LAVA, getFluid(inv.getStack(1)));
			assertEquals(BUCKET, getAmount(inv.getStack(1)));

			// Second insertion should just insert in place as the count is now 1.
			assertEquals(BUCKET, storage.insert(LAVA, BUCKET, tx));

			for (int slot = 0; slot < 2; ++slot) {
				assertEquals(LAVA, getFluid(inv.getStack(slot)));
				assertEquals(BUCKET, getAmount(inv.getStack(slot)));
			}

			tx.commit();
		}

		// Make sure custom NBT is kept.
		Text customName = new LiteralText("Lava-containing diamond!");
		inv.getStack(0).setCustomName(customName);

		try (Transaction tx = Transaction.openOuter()) {
			// Test extract along the way.
			assertEquals(BUCKET, storage.extract(LAVA, 10 * BUCKET, tx));

			tx.commit();
		}

		// Check custom name.
		assertEquals(customName, inv.getStack(0).getName());
		assertEquals(FluidVariant.blank(), getFluid(inv.getStack(0)));
		assertEquals(0L, getAmount(inv.getStack(0)));
	}

	private static FluidVariant getFluid(ItemStack stack) {
		CompoundTag nbt = stack.getTag();

		if (nbt != null && nbt.contains("fluid")) {
			return FluidVariant.fromNbt(nbt.getCompound("fluid"));
		} else {
			return FluidVariant.blank();
		}
	}

	private static long getAmount(ItemStack stack) {
		CompoundTag nbt = stack.getTag();

		if (nbt != null) {
			return nbt.getLong("amount");
		} else {
			return 0;
		}
	}

	private static void setContents(ItemStack stack, FluidVariant newResource, long newAmount) {
		if (newAmount > 0) {
			stack.getOrCreateTag().put("fluid", newResource.toNbt());
			stack.getOrCreateTag().putLong("amount", newAmount);
		} else {
			// Make sure emptied tanks can stack with tanks without NBT.
			stack.removeSubTag("fluid");
			stack.removeSubTag("amount");
		}
	}

	private static Storage<FluidVariant> createTankStorage(ContainerItemContext ctx) {
		return new SingleVariantItemStorage<FluidVariant>(ctx) {
			@Override
			protected FluidVariant getBlankResource() {
				return FluidVariant.blank();
			}

			@Override
			protected FluidVariant getResource(ItemVariant currentVariant) {
				return getFluid(currentVariant.toStack());
			}

			@Override
			protected long getAmount(ItemVariant currentVariant) {
				return SingleVariantItemStorageTests.getAmount(currentVariant.toStack());
			}

			@Override
			protected long getCapacity(FluidVariant variant) {
				return 2 * BUCKET;
			}

			@Override
			protected ItemVariant getUpdatedVariant(ItemVariant currentVariant, FluidVariant newResource, long newAmount) {
				// Operate on the stack directly to keep any other NBT data such as a custom name or enchant.
				ItemStack stack = currentVariant.toStack();
				setContents(stack, newResource, newAmount);
				return ItemVariant.of(stack);
			}
		};
	}

	private static class InventoryContainerItemContext implements ContainerItemContext {
		private final InventoryStorage storage;

		private InventoryContainerItemContext(Inventory inventory) {
			this.storage = InventoryStorage.of(inventory, null);
		}

		@Override
		public SingleSlotStorage<ItemVariant> getMainSlot() {
			return storage.getSlot(0);
		}

		@Override
		public long insertOverflow(ItemVariant itemVariant, long maxAmount, TransactionContext transactionContext) {
			return storage.insert(itemVariant, maxAmount, transactionContext);
		}

		@Override
		public List<SingleSlotStorage<ItemVariant>> getAdditionalSlots() {
			return storage.getSlots();
		}
	}
}
