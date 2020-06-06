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

package net.fabricmc.fabric.api.transaction.v1;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import net.minecraft.world.World;

public final class Transaction {
	private final Transaction parent;
	private Transaction child = null;

	private final Map<TransactionDataKey<Object>, Object> data = new HashMap<>();

	private boolean valid = true;

	Transaction(Transaction parent) {
		this.parent = parent;
	}

	/**
	 * Gets a value from the internal data map.
	 *
	 * <p>Calling this will invalidate any child transactions.
	 *
	 * @see Map#get(Object)
	 */
	public <D> D get(TransactionDataKey<D> container) {
		this.checkValid();
		this.invalidateChildren();
		return this.getUnchecked(container);
	}

	@SuppressWarnings("unchecked")
	private <D> D getUnchecked(TransactionDataKey<D> container) {
		return (D) this.data.get(container);
	}

	/**
	 * Puts a value into the internal data map.
	 *
	 * <p>Calling this will invalidate any child transactions.
	 *
	 * @see Map#put(Object, Object)
	 */
	public <D> D put(TransactionDataKey<D> container, D value) {
		this.checkValid();
		this.invalidateChildren();
		return putUnchecked(container, value);
	}

	@SuppressWarnings("unchecked")
	private <D> D putUnchecked(TransactionDataKey<D> container, D value) {
		return (D) this.data.put((TransactionDataKey<Object>) container, value);
	}

	/**
	 * <br>Calling this will invalidate any child transactions.
	 *
	 * @see Map#computeIfAbsent(Object, Function)
	 */
	@SuppressWarnings({"unchecked", "rawtypes"})
	public <D> D computeIfAbsent(TransactionDataKey<D> container, Function<TransactionDataKey<D>, D> op) {
		this.checkValid();
		this.invalidateChildren();
		return (D) this.data.computeIfAbsent((TransactionDataKey<Object>) container, (Function) op);
	}

	/**
	 * Initializes the data map value for the {@code container} by going through
	 * the transaction tree, picking the closest value and applying {@code op}
	 * to it.
	 *
	 * <p><b>WARNING</b>: The data passed to {@code op} must be copied if it
	 * contains mutable state to prevent modifying the parent transaction's
	 * state!
	 *
	 * <p>Calling this will invalidate any child transactions.
	 *
	 * @param container the container to init the value for
	 * @param op        the map to apply before putting the value into the data
	 *                  map, this should copy the object to prevent mutating the
	 *                  parent transaction's state
	 * @param <D>       the data type
	 */
	public <D> void initWithParent(TransactionDataKey<D> container, Function<D, D> op) {
		this.checkValid();
		this.invalidateChildren();

		if (!this.data.containsKey(container)) {
			Transaction current = this;

			while (current != null) {
				D data = current.getUnchecked(container);

				if (data != null) {
					this.putUnchecked(container, op.apply(data));
					return;
				}

				current = current.parent;
			}
		}
	}

	/**
	 * Collect a list of all the data. The first element in the list will be
	 * closest to the root of the transaction tree.
	 *
	 * <p><b>WARNING</b>: The data returned here must not be modified since it
	 * partially belongs to parent transactions!
	 *
	 * <p>Calling this will invalidate any child transactions.
	 *
	 * @param container the container to get the data list for
	 * @param <D>       the data type
	 * @return the data list
	 */
	public <D> List<D> collectData(TransactionDataKey<D> container) {
		this.checkValid();
		this.invalidateChildren();

		LinkedList<D> dataList = new LinkedList<>();
		Transaction current = this;

		while (current != null) {
			D data = current.getUnchecked(container);

			if (data != null) {
				dataList.addFirst(data);
			}

			current = current.parent;
		}

		return dataList;
	}

	/**
	 * Commit this transaction.
	 * If this transaction has a parent, applies this transaction's changes to
	 * the parent. Otherwise, applies this transaction's changes to the actual
	 * target objects.
	 *
	 * <p>Calling this will invalidate any child transactions.
	 */
	public void commit() {
		this.checkValid();

		if (this.parent != null) {
			this.data.forEach((k, v) -> k.applyChangesToTransaction(this.parent, v));
		} else {
			this.data.forEach(TransactionDataKey::applyChanges);
		}

		this.invalidate();
	}

	/**
	 * Invalidate this transaction. After this is called, any operations on this
	 * transaction will throw an {@link IllegalStateException}.
	 *
	 * <p>In most cases, this does not have to be called manually.
	 */
	public void invalidate() {
		this.invalidateChildren();

		if (this.parent != null) {
			this.parent.child = null;
		}

		this.valid = false;
	}

	private void invalidateChildren() {
		if (this.child != null) {
			this.child.invalidate();
			this.child = null;
		}
	}

	public boolean isValid() {
		return this.valid;
	}

	private void checkValid() {
		if (!this.valid) throw new IllegalStateException("Transaction is not valid!");
	}

	/**
	 * Create a new sub-transaction.
	 *
	 * <p>Calling this will invalidate any existing subtransactions of this
	 * transaction.
	 *
	 * @return the new transaction
	 */
	public Transaction createTransaction() {
		this.checkValid();
		this.invalidateChildren();

		Transaction transaction = new Transaction(this);
		this.child = transaction;
		return transaction;
	}

	/**
	 * Create a new transaction using the default {@link TransactionTracker}s.
	 *
	 * <p>Calling this will invalidate any existing transactions.
	 *
	 * @return the new transaction
	 */
	public static Transaction create(World world) {
		return TransactionTracker.getInstance(world).create();
	}
}
