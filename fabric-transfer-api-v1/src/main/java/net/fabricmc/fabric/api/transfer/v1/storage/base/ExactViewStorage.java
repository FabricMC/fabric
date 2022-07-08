package net.fabricmc.fabric.api.transfer.v1.storage.base;


import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.BlankVariantView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.OverridingMethodsMustInvokeSuper;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
/**
 * A storage that provides a variant-to-slot map.<br>
 * Its {@link Storage#exactView} is quick.<br>
 * Capacity is almost infinite.<br>
 *
 * @param <T> The type param {@code T} of {@link Storage}
 * @param <S> The type param {@code T} of {@link SnapshotParticipant}
 * @implNote If extending {@link Slot}, {@link #newSlot()} should be overrided.
 * @see ItemImpl
 */
public abstract class ExactViewStorage<T extends TransferVariant<?>, S> extends SnapshotParticipant<S> implements Storage<T> {
    /**
     * Should never throw {@link ConcurrentModificationException}, such as {@link ConcurrentHashMap}.
     *
     * @see #iterator()
     */
    protected Map<T, SingleSlotStorage<T>> map;
    /**
     * If a variant is contained here, its corresponding slot might be modified.
     *
     * @see #iterator()
     * @see #onFinalCommit()
     */
    protected Set<T> pendings;
    protected Collection<T> order;

    protected ExactViewStorage() {
        this(new ConcurrentHashMap<>(), new HashSet<>());
    }

    protected ExactViewStorage(Map<T, SingleSlotStorage<T>> map, Set<T> pendings) {
        this.map = map;
        this.pendings = pendings;
        this.order = new LinkedHashSet<>();
    }

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
    }

    @Override
    public long insert(T resource, long maxAmount, TransactionContext transaction) {
        var slot = map.get(resource);
        if (slot == null) {
            slot = newSlot();
            map.put(resource, slot);
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
        return view != null ? view : new BlankVariantView<>(blankVariant(), 0);
    }

    public abstract T blankVariant();

    public Map<T, SingleSlotStorage<T>> getMap() {
        return map;
    }

    public Set<T> getPendings() {
        return pendings;
    }

    /**
     * Iterate according to the iteration order of {@link #order}.<br>
     * This is <b>read-only</b>. Invoking {@link Iterator#remove} is not allowed.
     */
    public Iterable<ResourceAmount<T>> orderedIterable() {
        return () -> new Iterator<>() {
            final Iterator<T> ite = order.iterator();
            @Nullable ResourceAmount<T> next;

            @Override
            public boolean hasNext() {
                next0();
                return next != null;
            }

            @Override
            public ResourceAmount<T> next() {
                next0();
                if (next == null)
                    throw new NoSuchElementException(order.toString());
                var n = next;
                next = null;
                return n;
            }

            private void next0() {
                while (next == null) {
                    if (!ite.hasNext())
                        return;
                    T key = ite.next();
                    var slot = map.get(key);
                    if (slot.isResourceBlank() || slot.getAmount() == 0) {
                        ite.remove();
                        continue;
                    }
                    next = new ResourceAmount<>(slot.getResource(), slot.getAmount());
                }
            }
        };
    }

    /**
     * Just extending {@link Slot} affects nothing, this method should also be overrided to return extended {@link Slot}.
     *
     * @return Empty slot.
     */
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

    public static ExactViewStorage<ItemVariant, ?> itemImpl() {
        return new ItemImpl();
    }

    /**
     * A simple implement to {@link ExactViewStorage<ItemVariant>}.
     */
    public static class ItemImpl extends ExactViewStorage<ItemVariant, Object> {
        @Override
        public ItemVariant blankVariant() {
            return ItemVariant.blank();
        }
    }
}
