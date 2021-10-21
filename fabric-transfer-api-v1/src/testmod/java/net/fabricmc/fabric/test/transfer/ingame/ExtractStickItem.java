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

package net.fabricmc.fabric.test.transfer.ingame;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;

public class ExtractStickItem extends Item {
	public ExtractStickItem() {
		super(new Settings().group(ItemGroup.MISC));
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		Storage<FluidVariant> storage = FluidStorage.SIDED.find(context.getWorld(), context.getBlockPos(), context.getSide());

		try (Transaction transaction = Transaction.openOuter()) {
			// Find something to extract
			FluidVariant stored = StorageUtil.findExtractableResource(storage, transaction);
			if (stored == null) return ActionResult.PASS;

			// By now, storage can't be null :P
			long extracted = storage.extract(stored, FluidConstants.BUCKET, transaction);
			// If sneaking, we require exact extraction (can be tested on cauldrons)
			boolean requireExact = context.getPlayer() != null && context.getPlayer().isSneaking();

			if (!requireExact || extracted == FluidConstants.BUCKET) {
				transaction.commit();
				return ActionResult.success(context.getWorld().isClient());
			}
		}

		return ActionResult.FAIL;
	}
}
