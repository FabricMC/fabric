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

package net.fabricmc.fabric.api.transfer.v1.storage;

import java.util.List;
import java.util.function.Predicate;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import net.minecraft.inventory.Inventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.math.MathHelper;

import net.fabricmc.fabric.api.transfer.v1.storage.base.ResourceAmount;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;

/**
 * Helper functions to work with {@link Storage}s.
 *
 * <p>Note that the functions that take a predicate iterate over the entire inventory in the worst case.
 * If the resource is known, there will generally be a more performance efficient way.
 *
 * <p><b>Experimental feature</b>, we reserve the right to remove or change it without further notice.
 * The transfer API is a complex addition, and we want to be able to correct possible design mistakes.
 */
@ApiStatus.Experimental
public final class StorageUtil {
	/**
	 * Move resources between two storages, matching the passed filter, and return the amount that was successfully transferred.
	 *
	 * <p>Here is a usage example with fluid variant storages:
	 * <pre>{@code
	 * // Source
	 * Storage<FluidVariant> source;
	 * // Target
	 * Storage<FluidVariant> target;
	 *
	 * // Move up to one bucket in total from source to target, outside of a transaction:
	 * long amountMoved = StorageUtil.move(source, target, variant -> true, FluidConstants.BUCKET, null);
	 * // Move exactly one bucket in total, only of water:
	 * try (Transaction transaction = Transaction.openOuter()) {
	 *     Predicate<FluidVariant> filter = variant -> variant.isOf(Fluids.WATER);
	 *     long waterMoved = StorageUtil.move(source, target, filter, FluidConstants.BUCKET, transaction);
	 *     if (waterMoved == FluidConstants.BUCKET) {
	 *         // Only commit if exactly one bucket was moved (no less!).
	 *         transaction.commit();
	 *     }
	 * }
	 * }</pre>
	 *
	 * @param from The source storage. May be null.
	 * @param to The target storage. May be null.
	 * @param filter The filter for transferred resources.
	 *               Only resources for which this filter returns {@code true} will be transferred.
	 *               This filter will never be tested with a blank resource, and filters are encouraged to throw an
	 *               exception if this guarantee is violated.
	 * @param maxAmount The maximum amount that will be transferred.
	 * @param transaction The transaction this transfer is part of, or {@code null} if a transaction should be opened just for this transfer.
	 * @param <T> The type of resources to move.
	 * @return The total amount of resources that was successfully transferred.
	 * @throws IllegalStateException If no transaction is passed and a transaction is already active on the current thread.
	 */
	public static <T> long move(@Nullable Storage<T> from, @Nullable Storage<T> to, Predicate<T> filter, long maxAmount, @Nullable TransactionContext transaction) {
		if (from == null || to == null) return 0;

		long totalMoved = 0;

		try (Transaction iterationTransaction = Transaction.openNested(transaction)) {
			for (StorageView<T> view : from.iterable(iterationTransaction)) {
				if (view.isResourceBlank()) continue;
				T resource = view.getResource();
				if (!filter.test(resource)) continue;
				long maxExtracted;

				// check how much can be extracted
				try (Transaction extractionTestTransaction = iterationTransaction.openNested()) {
					maxExtracted = view.extract(resource, maxAmount - totalMoved, extractionTestTransaction);
					extractionTestTransaction.abort();
				}

				try (Transaction transferTransaction = iterationTransaction.openNested()) {
					// check how much can be inserted
					long accepted = to.insert(resource, maxExtracted, transferTransaction);

					// extract it, or rollback if the amounts don't match
					if (view.extract(resource, accepted, transferTransaction) == accepted) {
						totalMoved += accepted;
						transferTransaction.commit();
					}
				}

				if (maxAmount == totalMoved) {
					// early return if nothing can be moved anymore
					iterationTransaction.commit();
					return totalMoved;
				}
			}

			iterationTransaction.commit();
		}

		return totalMoved;
	}

	/**
	 * Try to insert up to some amount of a resource into a list of storage slots, trying to "stack" first,
	 * i.e. prioritizing slots that already contain the resource.
	 *
	 * @return How much was inserted.
	 * @see Storage#insert
	 */
	public static <T> long insertStacking(List<SingleSlotStorage<T>> slots, T resource, long maxAmount, TransactionContext transaction) {
		StoragePreconditions.notNegative(maxAmount);
		long amount = 0;

		for (SingleSlotStorage<T> slot : slots) {
			if (!slot.isResourceBlank()) {
				amount += slot.insert(resource, maxAmount - amount, transaction);
				if (amount == maxAmount) return amount;
			}
		}

		for (SingleSlotStorage<T> slot : slots) {
			amount += slot.insert(resource, maxAmount - amount, transaction);
			if (amount == maxAmount) return amount;
		}

		return amount;
	}

	/**
	 * Attempt to find a resource stored in the passed storage.
	 *
	 * @see #findStoredResource(Storage, Predicate, TransactionContext)
	 * @return A non-blank resource stored in the storage, or {@code null} if none could be found.
	 */
	@Nullable
	public static <T> T findStoredResource(@Nullable Storage<T> storage, @Nullable TransactionContext transaction) {
		return findStoredResource(storage, r -> true, transaction);
	}

	/**
	 * Attempt to find a resource stored in the passed storage that matches the passed filter.
	 *
	 * @param storage The storage to inspect, may be null.
	 * @param filter The filter. Only a resource for which this filter returns {@code true} will be returned.
	 * @param transaction The current transaction, or {@code null} if a transaction should be opened for this query.
	 * @param <T> The type of the stored resources.
	 * @return A non-blank resource stored in the storage that matches the filter, or {@code null} if none could be found.
	 */
	@Nullable
	public static <T> T findStoredResource(@Nullable Storage<T> storage, Predicate<T> filter, @Nullable TransactionContext transaction) {
		if (storage == null) return null;

		if (transaction == null) {
			try (Transaction outer = Transaction.openOuter()) {
				return findStoredResourceInner(storage, filter, outer);
			}
		} else {
			return findStoredResourceInner(storage, filter, transaction);
		}
	}

	@Nullable
	private static <T> T findStoredResourceInner(Storage<T> storage, Predicate<T> filter, TransactionContext transaction) {
		for (StorageView<T> view : storage.iterable(transaction)) {
			if (!view.isResourceBlank() && filter.test(view.getResource())) {
				return view.getResource();
			}
		}

		return null;
	}

	/**
	 * Attempt to find a resource stored in the passed storage that can be extracted.
	 *
	 * @see #findExtractableResource(Storage, Predicate, TransactionContext)
	 * @return A non-blank resource stored in the storage that can be extracted, or {@code null} if none could be found.
	 */
	@Nullable
	public static <T> T findExtractableResource(@Nullable Storage<T> storage, @Nullable TransactionContext transaction) {
		return findExtractableResource(storage, r -> true, transaction);
	}

	/**
	 * Attempt to find a resource stored in the passed storage that matches the passed filter and can be extracted.
	 *
	 * @param storage The storage to inspect, may be null.
	 * @param filter The filter. Only a resource for which this filter returns {@code true} will be returned.
	 * @param transaction The current transaction, or {@code null} if a transaction should be opened for this query.
	 * @param <T> The type of the stored resources.
	 * @return A non-blank resource stored in the storage that matches the filter and can be extracted, or {@code null} if none could be found.
	 */
	@Nullable
	public static <T> T findExtractableResource(@Nullable Storage<T> storage, Predicate<T> filter, @Nullable TransactionContext transaction) {
		if (storage == null) return null;

		try (Transaction nested = Transaction.openNested(transaction)) {
			for (StorageView<T> view : storage.iterable(nested)) {
				// Extract below could change the resource, so we have to query it before extracting.
				T resource = view.getResource();

				if (!view.isResourceBlank() && filter.test(resource) && view.extract(resource, Long.MAX_VALUE, nested) > 0) {
					// Will abort the extraction.
					return resource;
				}
			}
		}

		return null;
	}

	/**
	 * Attempt to find a resource stored in the passed storage that can be extracted, and how much of it can be extracted.
	 *
	 * @see #findExtractableContent(Storage, Predicate, TransactionContext)
	 * @return A non-blank resource stored in the storage that can be extracted, and the strictly positive amount of it that can be extracted,
	 * or {@code null} if none could be found.
	 */
	@Nullable
	public static <T> ResourceAmount<T> findExtractableContent(@Nullable Storage<T> storage, @Nullable TransactionContext transaction) {
		return findExtractableContent(storage, r -> true, transaction);
	}

	/**
	 * Attempt to find a resource stored in the passed storage that can be extracted and matches the filter, and how much of it can be extracted.
	 *
	 * @param storage The storage to inspect, may be null.
	 * @param filter The filter. Only a resource for which this filter returns {@code true} will be returned.
	 * @param transaction The current transaction, or {@code null} if a transaction should be opened for this query.
	 * @param <T> The type of the stored resources.
	 * @return A non-blank resource stored in the storage that can be extracted and matches the filter, and the strictly positive amount of it that can be extracted,
	 * or {@code null} if none could be found.
	 */
	@Nullable
	public static <T> ResourceAmount<T> findExtractableContent(@Nullable Storage<T> storage, Predicate<T> filter, @Nullable TransactionContext transaction) {
		T extractableResource = findExtractableResource(storage, filter, transaction);

		if (extractableResource != null) {
			long extractableAmount = storage.simulateExtract(extractableResource, Long.MAX_VALUE, transaction);

			if (extractableAmount > 0) {
				return new ResourceAmount<>(extractableResource, extractableAmount);
			}
		}

		return null;
	}

	/**
	 * Compute the comparator output for a storage, similar to {@link ScreenHandler#calculateComparatorOutput(Inventory)}.
	 *
	 * @param storage The storage for which the comparator level should be computed.
	 * @param transaction The current transaction, or {@code null} if a transaction should be opened for this computation.
	 * @param <T> The type of the stored resources.
	 * @return An integer between 0 and 15 (inclusive): the comparator output for the passed storage.
	 */
	public static <T> int calculateComparatorOutput(@Nullable Storage<T> storage, @Nullable TransactionContext transaction) {
		if (storage == null) return 0;

		if (transaction == null) {
			try (Transaction outer = Transaction.openOuter()) {
				return calculateComparatorOutputInner(storage, outer);
			}
		} else {
			return calculateComparatorOutputInner(storage, transaction);
		}
	}

	private static <T> int calculateComparatorOutputInner(Storage<T> storage, TransactionContext transaction) {
		double fillPercentage = 0;
		int viewCount = 0;
		boolean hasNonEmptyView = false;

		for (StorageView<T> view : storage.iterable(transaction)) {
			viewCount++;

			if (view.getAmount() > 0) {
				fillPercentage += (double) view.getAmount() / view.getCapacity();
				hasNonEmptyView = true;
			}
		}

		return MathHelper.floor(fillPercentage / viewCount * 14) + (hasNonEmptyView ? 1 : 0);
	}
}
