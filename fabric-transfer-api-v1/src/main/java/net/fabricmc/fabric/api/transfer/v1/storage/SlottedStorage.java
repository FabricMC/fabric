package net.fabricmc.fabric.api.transfer.v1.storage;

import java.util.List;

import org.jetbrains.annotations.ApiStatus;

/**
 * A {@link Storage} implementation made of indexed slots.
 * Please note that some storages may not implement this interface: checking whether a storage is slotted can be done using {@code instanceof}.
 *
 * @param <T> The type of the stored resources.
 *
 * <b>Experimental feature</b>, we reserve the right to remove or change it without further notice.
 * The transfer API is a complex addition, and we want to be able to correct possible design mistakes.
 */
@ApiStatus.Experimental
public interface SlottedStorage<T> extends Storage<T> {
	/**
	 * Retrieve the list of slots in the inventory. This list may not be mutated.
	 */
	List<? extends SingleSlotStorage<T>> getSlots();

	/**
	 * Retrieve a wrapper around a specific slot of this storage.
	 */
	default SingleSlotStorage<T> getSlot(int slot) {
		return getSlots().get(slot);
	}
}
