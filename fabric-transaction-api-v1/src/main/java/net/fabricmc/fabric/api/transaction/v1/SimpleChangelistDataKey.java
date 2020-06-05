package net.fabricmc.fabric.api.transaction.v1;

import java.util.List;

/**
 * An implementation of {@link ChangelistDataKey} that provides prebuilt
 * methods for applying changelists to the target object.
 *
 * @param <T> the change list item type
 * @param <R> the target object type
 */
public interface SimpleChangelistDataKey<T, R> extends ChangelistDataKey<T> {

    /**
     * Gets the current state of the target object.
     *
     * @param ta the transaction to retrieve the object state from
     * @return the current state of the object
     */
    default R getCurrentState(Transaction ta) {
        R inv = this.copyState();
        for (List<T> l : ta.collectData(this)) {
            for (T change : l) {
                this.apply(inv, change);
            }
        }
        return inv;
    }

    /**
     * Create a copy of the persistent state.
     *
     * @return
     */
    R copyState();

    /**
     * Gets the persistent state (the state outside of the transaction) of the
     * target object.
     *
     * @return the persistent object state
     */
    R getPersistentState();

    /**
     * Apply a single change onto the receiver.
     *
     * @param receiver the receiver
     * @param change   the change to apply
     */
    void apply(R receiver, T change);

    @Override
    default void applyChanges(List<T> changes) {
        for (T change : changes) {
            this.apply(this.getPersistentState(), change);
        }
    }

}
