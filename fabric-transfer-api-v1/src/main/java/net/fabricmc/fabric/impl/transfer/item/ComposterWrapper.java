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

package net.fabricmc.fabric.impl.transfer.item;

import static net.minecraft.util.math.Direction.UP;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

import com.google.common.collect.MapMaker;
import org.jetbrains.annotations.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.block.ComposterBlock;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.ExtractionOnlyStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.InsertionOnlyStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;

/**
 * Implementation of {@code Storage<ItemVariant>} for composters.
 */
public class ComposterWrapper extends SnapshotParticipant<Float> {
	// Record is used for convenient constructor, hashcode and equals implementations.
	// Record is used for convenient constructor, hashcode and equals implementations.
	private static final class WorldLocation {
		private final World world;
		private final BlockPos pos;

		WorldLocation(World world, BlockPos pos) {
			this.world = world;
			this.pos = pos;
		}

		public World world() {
			return world;
		}

		public BlockPos pos() {
			return pos;
		}

		private BlockState getBlockState() {
			return world.getBlockState(pos);
		}

		private void setBlockState(BlockState state) {
			world.setBlockState(pos, state);
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			WorldLocation that = (WorldLocation) o;
			return Objects.equals(world, that.world) && Objects.equals(pos, that.pos);
		}

		@Override
		public int hashCode() {
			return Objects.hash(world, pos);
		}
	}

	// Weak values to make sure wrappers are cleaned up after use, thread-safe.
	// The two storages strongly reference the containing wrapper, so we are alright with weak values.
	private static final Map<WorldLocation, ComposterWrapper> COMPOSTERS = new MapMaker().concurrencyLevel(1).weakValues().makeMap();

	@Nullable
	public static Storage<ItemVariant> get(World world, BlockPos pos, Direction direction) {
		Objects.requireNonNull(direction);

		if (direction.getAxis().isVertical()) {
			WorldLocation location = new WorldLocation(world, pos.toImmutable());
			ComposterWrapper composterWrapper = COMPOSTERS.computeIfAbsent(location, ComposterWrapper::new);
			return direction == UP ? composterWrapper.upStorage : composterWrapper.downStorage;
		} else {
			return null;
		}
	}

	private static final float DO_NOTHING = 0f;
	private static final float EXTRACT_BONEMEAL = -1f;

	private final WorldLocation location;
	// -1 if bonemeal was extracted, otherwise the composter increase probability of the (pending) inserted item.
	private Float increaseProbability = DO_NOTHING;
	private final TopStorage upStorage = new TopStorage();
	private final BottomStorage downStorage = new BottomStorage();

	private ComposterWrapper(WorldLocation location) {
		this.location = location;
	}

	@Override
	protected Float createSnapshot() {
		return increaseProbability;
	}

	@Override
	protected void readSnapshot(Float snapshot) {
		// Reset after unsuccessful commit.
		increaseProbability = snapshot;
	}

	@Override
	protected void onFinalCommit() {
		// Apply pending action
		if (increaseProbability == EXTRACT_BONEMEAL) {
			// Mimic ComposterBlock#emptyComposter logic.
			location.setBlockState(location.getBlockState().with(ComposterBlock.LEVEL, 0));
			// Play the sound
		} else if (increaseProbability > 0) {
			boolean increaseSuccessful = location.world.getRandom().nextDouble() < increaseProbability;

			if (increaseSuccessful) {
				// Mimic ComposterBlock#addToComposter logic.
				BlockState state = location.getBlockState();
				int newLevel = state.get(ComposterBlock.LEVEL) + 1;
				BlockState newState = state.with(ComposterBlock.LEVEL, newLevel);
				location.setBlockState(newState);

				if (newLevel == 7) {
					location.world.getBlockTickScheduler().schedule(location.pos, state.getBlock(), 20);
				}
			}

			location.world.syncWorldEvent(1500, location.pos, increaseSuccessful ? 1 : 0);
		}

		// Reset after successful commit.
		increaseProbability = DO_NOTHING;
	}

	private class TopStorage implements InsertionOnlyStorage<ItemVariant> {
		@Override
		public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
			StoragePreconditions.notBlankNotNegative(resource, maxAmount);

			// Check amount.
			if (maxAmount < 1) return 0;
			// Check that no action is scheduled.
			if (increaseProbability != DO_NOTHING) return 0;
			// Check that the composter can accept items.
			if (location.getBlockState().get(ComposterBlock.LEVEL) >= 7) return 0;
			// Check that the item is compostable.
			float insertedIncreaseProbability = ComposterBlock.ITEM_TO_LEVEL_INCREASE_CHANCE.getFloat(resource.getItem());
			if (insertedIncreaseProbability <= 0) return 0;

			// Schedule insertion.
			updateSnapshots(transaction);
			increaseProbability = insertedIncreaseProbability;
			return 1;
		}

		@Override
		public Iterator<StorageView<ItemVariant>> iterator(TransactionContext transaction) {
			return Collections.emptyIterator();
		}
	}

	private static final ItemVariant BONE_MEAL = ItemVariant.of(Items.BONE_MEAL);

	private class BottomStorage implements ExtractionOnlyStorage<ItemVariant>, SingleSlotStorage<ItemVariant> {
		private boolean hasBoneMeal() {
			// We only have bone meal if the level is 8 and no action was scheduled.
			return increaseProbability == DO_NOTHING && location.getBlockState().get(ComposterBlock.LEVEL) == 8;
		}

		@Override
		public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
			StoragePreconditions.notBlankNotNegative(resource, maxAmount);

			// Check amount.
			if (maxAmount < 1) return 0;
			// Check that the resource is bone meal.
			if (!BONE_MEAL.equals(resource)) return 0;
			// Check that there is bone meal to extract.
			if (!hasBoneMeal()) return 0;

			updateSnapshots(transaction);
			increaseProbability = EXTRACT_BONEMEAL;
			return 1;
		}

		@Override
		public boolean isResourceBlank() {
			return getResource().isBlank();
		}

		@Override
		public ItemVariant getResource() {
			return BONE_MEAL;
		}

		@Override
		public long getAmount() {
			return hasBoneMeal() ? 1 : 0;
		}

		@Override
		public long getCapacity() {
			return 1;
		}
	}
}
