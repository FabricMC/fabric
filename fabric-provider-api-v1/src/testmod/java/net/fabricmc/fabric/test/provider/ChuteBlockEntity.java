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

package net.fabricmc.fabric.test.provider;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.Direction;

import net.fabricmc.fabric.test.provider.api.ItemApis;
import net.fabricmc.fabric.test.provider.api.ItemExtractable;
import net.fabricmc.fabric.test.provider.api.ItemInsertable;
import net.fabricmc.fabric.test.provider.api.ItemUtils;

public class ChuteBlockEntity extends BlockEntity implements Tickable {
	int moveDelay = 0;
	public ChuteBlockEntity() {
		super(FabricProviderTest.CHUTE_BLOCK_ENTITY_TYPE);
	}

	@Override
	public void tick() {
		if (world.isClient) return;

		if (moveDelay == 0) {
			ItemExtractable from = ItemApis.EXTRACTABLE.get(world, pos.offset(Direction.UP), Direction.DOWN);
			ItemInsertable to = ItemApis.INSERTABLE.get(world, pos.offset(Direction.DOWN), Direction.UP);

			if (from != null && to != null) {
				ItemUtils.move(from, to, 1);
			}

			moveDelay = 20;
		}

		--moveDelay;
	}
}
