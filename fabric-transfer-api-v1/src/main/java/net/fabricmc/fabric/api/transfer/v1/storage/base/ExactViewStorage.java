package net.fabricmc.fabric.api.transfer.v1.storage.base;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.OverridingMethodsMustInvokeSuper;

import java.util.*;
/**
 * ExactViewStorage provides a variant-to-slot-map implement for {@link Storage}.
 * @param <T> The type param {@code T} of {@link Storage}
 * @param <S> The type param {@code T} of {@link SnapshotParticipant}
 * @see ItemImpl
 */
public abstract class ExactViewStorage<T extends TransferVariant<?>, S> extends SnapshotParticipant<S> implements Storage<T> {
	/**
	 * <b>Don't</b> iterate this map if not necessary. Iterate this storage directly instead.
	 *
	 * @see #iterator()
	 */
	protected final Map<T, SingleSlotStorage<T>> map = new LinkedHashMap<>();
	/**
	 * If a variant is contained here, its corresponding slot might be modified.
	 */
	protected final Set<T> pendings = new HashSet<>();
	/**
	 * To avoid {@link ConcurrentModificationException} while iterating.
	 */
	protected final Map<T, SingleSlotStorage<T>> mod = new LinkedHashMap<>();

	/**
	 * Overriding method <b>mustn't</b> invoke this super.
	 */
	@Override
	protected S createSnapshot() {
		//noinspection unchecked
		return (S) new Object();
	}

	@Override
	protected void readSnapshot(S snapshot) {

	}

	@OverridingMethodsMustInvokeSuper
	@Override
	protected void onFinalCommit() {
		pendings.clear();
		map.putAll(mod);
		mod.clear();
	}

	@Override
	public long insert(T resource, long maxAmount, TransactionContext transaction) {
		var slot = map.get(resource);
		if (slot == null) {
			slot = newSlot();
			mod.put(resource, slot);
		}
		return slot.insert(resource, maxAmount, transaction);
	}

	@Override
	public long extract(T resource, long maxAmount, TransactionContext transaction) {
		return exactView(transaction, resource).extract(resource, maxAmount, transaction);
	}

	/**
	 * While iterating, certain empty slot will be removed.
	 */
	@Override
	public Iterator<StorageView<T>> iterator() {
		return new Iterator<>() {
			final Iterator<? extends StorageView<T>> ite = map.values().iterator();

			@Override
			public boolean hasNext() {
				return ite.hasNext();
			}

			@Override
			public StorageView<T> next() {
				var n = ite.next();
				if ((n.getAmount() == 0 || n.isResourceBlank()) && !pendings.contains(n.getResource())) {
					ite.remove();
				}
				return n;
			}
		};
	}

	@Override
	public @NotNull StorageView<T> exactView(@Nullable TransactionContext transaction, T resource) {
		StorageView<T> view = map.get(resource);
		if (view == null)
			view = map.get(resource);
		return view != null ? view : new BlankVariantView<>(blankVariant(), 0);
	}

	public abstract T blankVariant();

	protected SingleSlotStorage<T> newSlot() {
		return new Slot();
	}

	protected class Slot extends SingleVariantStorage<T> {
		@Override
		protected T getBlankVariant() {
			return blankVariant();
		}

		@Override
		protected long getCapacity(T variant) {
			return Long.MAX_VALUE;
		}

		@Override
		public long insert(T insertedVariant, long maxAmount, TransactionContext transaction) {
			pendings.add(insertedVariant);
			ExactViewStorage.this.updateSnapshots(transaction);
			return super.insert(insertedVariant, maxAmount, transaction);
		}

		@Override
		public long extract(T extractedVariant, long maxAmount, TransactionContext transaction) {
			pendings.add(extractedVariant);
			ExactViewStorage.this.updateSnapshots(transaction);
			return super.extract(extractedVariant, maxAmount, transaction);
		}
	}

	/**
	 * ItemImpl is a simple implement for {@link ExactViewStorage<ItemVariant>}.
	 */
	public static class ItemImpl extends ExactViewStorage<ItemVariant, Object> {
		@Override
		public ItemVariant blankVariant() {
			return ItemVariant.blank();
		}
	}
}
