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

package net.fabricmc.fabric.api.transfer.v1.transaction.base;

import java.util.ArrayList;
import java.util.List;

import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;

public abstract class SnapshotParticipant<T> implements Transaction.CloseCallback {
	/**
	 * Create a new snapshot.
	 */
	protected abstract T createSnapshot();

	/**
	 * Read the snapshot state.
	 */
	protected abstract void readSnapshot(T snapshot);

	/**
	 * Signals that the snapshot will not be used anymore, and is safe to cache for the next call to {@link #createSnapshot}.
	 */
	protected void releaseSnapshot(T snapshot) {
	}

	/**
	 * Use this to call markDirty() if you need to.
	 */
	protected void onFinalCommit() {
	}

	/**
	 * Update the stored snapshots so that the changes happening as part of the passed transaction can be correctly
	 * committed or rolled back.
	 * Subclasses should call this function every time they are about to change their internal state.
	 */
	protected final void updateSnapshots(Transaction transaction) {
		// Make sure we have enough storage for snapshots
		while (snapshots.size() <= transaction.nestingDepth()) {
			snapshots.add(null);
		}

		// If the snapshot is null, we need to create it, and we need to register a callback.
		if (snapshots.get(transaction.nestingDepth()) == null) {
			snapshots.set(transaction.nestingDepth(), createSnapshot());
			transaction.addCloseCallback(this);
		}
	}

	private final List<T> snapshots = new ArrayList<>();

	@Override
	public final void onClose(Transaction transaction, Transaction.Result result) {
		T snapshot = snapshots.get(transaction.nestingDepth());
		snapshots.set(transaction.nestingDepth(), null);

		if (result.wasAborted()) {
			readSnapshot(snapshot);
			releaseSnapshot(snapshot);
		} else if (transaction.nestingDepth() > 0) {
			T oldSnapshot = snapshots.set(transaction.nestingDepth()-1, snapshot);

			if (oldSnapshot != null) {
				releaseSnapshot(oldSnapshot);
			} else {
				transaction.getOpenTransaction(transaction.nestingDepth()-1).addCloseCallback(this);
			}
		} else {
			releaseSnapshot(snapshot);
			// TODO: should onFinalCommit be deferred until after all `onClose` actions are run?
			onFinalCommit();
		}
	}
}
