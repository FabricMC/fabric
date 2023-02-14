package net.fabricmc.fabric.api.transfer.v1.storage;

import java.util.List;

import org.jetbrains.annotations.ApiStatus;

import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;

/**
 * A {@link Storage} implementation made of indexed slots.
 * Please note that some storages may not implement this interface:
 * checking whether a storage is slotted can be done using {@code instanceof}.
 *
 * @param <T> The type of the stored resources.
 *
 * <b>Experimental feature</b>, we reserve the right to remove or change it without further notice.
 * The transfer API is a complex addition, and we want to be able to correct possible design mistakes.
 */
@ApiStatus.Experimental
public interface SlottedStorage<T> extends Storage<T> {
	/**
	 * Retrieve the number of slots in this storage.
	 */
	int getSlotCount();

	/**
	 * Retrieve a specific slot of this storage.
	 *
	 * @throws IndexOutOfBoundsException If the slot index is out of bounds.
	 */
	SingleSlotStorage<T> getSlot(int slot);

	/**
	 * Retrieve all the slots of this storage. <b>The list must not be modified.</b>
	 */
	default List<SingleSlotStorage<T>> getSlots() {
		int slotCount = getSlotCount();
		SingleSlotStorage<T>[] slots = new SingleSlotStorage[slotCount];

		for (int i = 0; i < slotCount; i++) {
			slots[i] = getSlot(i);
		}

		return List.of(slots);
	}
}
