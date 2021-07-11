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

package net.fabricmc.fabric.test.lookup;

import org.jetbrains.annotations.NotNull;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.Direction;

import net.fabricmc.fabric.api.lookup.v1.block.BlockApiCache;
import net.fabricmc.fabric.test.lookup.api.ItemApis;
import net.fabricmc.fabric.test.lookup.api.ItemExtractable;
import net.fabricmc.fabric.test.lookup.api.ItemInsertable;
import net.fabricmc.fabric.test.lookup.api.ItemUtils;

public class ChuteBlockEntity extends BlockEntity implements Tickable {
	private int moveDelay = 0;
	private BlockApiCache<ItemInsertable, @NotNull Direction> cachedInsertable = null;
	private BlockApiCache<ItemExtractable, @NotNull Direction> cachedExtractable = null;

	public ChuteBlockEntity() {
		super(FabricApiLookupTest.CHUTE_BLOCK_ENTITY_TYPE);
	}

	@Override
	public void tick() {
		//noinspection ConstantConditions - Intellij intrinsics don't know that hasWorld makes getWorld evaluate to non-null
		if (!this.hasWorld() || this.getWorld().isClient()) {
			return;
		}

		if (cachedInsertable == null) {
			cachedInsertable = BlockApiCache.create(ItemApis.INSERTABLE, (ServerWorld) world, pos.offset(Direction.DOWN));
		}

		if (cachedExtractable == null) {
			cachedExtractable = BlockApiCache.create(ItemApis.EXTRACTABLE, (ServerWorld) world, pos.offset(Direction.UP));
		}

		if (moveDelay == 0) {
			ItemExtractable from = cachedExtractable.find(Direction.DOWN);
			ItemInsertable to = cachedInsertable.find(Direction.UP);

			if (from != null && to != null) {
				ItemUtils.move(from, to, 1);
			}

			moveDelay = 20;
		}

		--moveDelay;
	}
}
