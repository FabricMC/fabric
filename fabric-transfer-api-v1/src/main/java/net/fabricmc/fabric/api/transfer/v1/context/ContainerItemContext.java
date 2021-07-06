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

package net.fabricmc.fabric.api.transfer.v1.context;

import java.util.List;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import net.fabricmc.fabric.api.lookup.v1.item.ItemApiLookup;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.impl.transfer.context.PlayerContainerItemContext;

/**
 * A context that determines how an {@link ItemVariant} interacts with an inventory, or at least the part of it that is visible to the context.
 * For example, it allows a water bucket to replace itself by an empty bucket when emptied.
 *
 * <p>When an {@linkplain ItemApiLookup item API} requires a {@code ContainerItemContext} as context,
 * it will generally be suitable to obtain a context instance with {@link #ofPlayerHand} or {@link #ofPlayerCursor},
 * and then use {@link #find} to query an API instance.
 *
 * <p>A {@code ContainerItemContext} is made of the following parts:
 * <ul>
 *     <li>{@linkplain #getMainSlot A main slot}, containing the item variant the API was queried for initially.</li>
 *     <li>{@linkplain #insertOverflow An overflow insertion function}, that can be used to insert into the context when insertion into a slot fails.</li>
 *     <li>{@linkplain #getWorld The current world}, that can be used to retrieve client-wide or server-side data.</li>
 *     <li>The context may also contain additional slots, accessible through {@link #getAdditionalSlots}.</li>
 * </ul>
 *
 * <p>Implementors of item APIs can freely use these methods, but most will generally want to use the following convenience methods instead:
 * <ul>
 *     <li>Query which variant is currently in the main slot through {@link #getItemVariant}.</li>
 *     <li>Query how much of the (non-blank) variant is in the inventory through {@link #getAmount}.</li>
 *     <li>Transform some of the current variant into another variant through {@link #transform}.</li>
 * </ul>
 *
 * @deprecated Experimental feature, we reserve the right to remove or change it without further notice.
 * The transfer API is a complex addition, and we want to be able to correct possible design mistakes.
 */
@ApiStatus.Experimental
@Deprecated
public interface ContainerItemContext {
	/**
	 * Return a context for the passed player's hand. This is recommended for item use interactions.
	 */
	static ContainerItemContext ofPlayerHand(PlayerEntity player, Hand hand) {
		return new PlayerContainerItemContext(player, hand);
	}

	/**
	 * Return a context for the passed player's cursor slot. This is recommended for screen handler click interactions.
	 */
	static ContainerItemContext ofPlayerCursor(PlayerEntity player, ScreenHandler screenHandler) {
		return ofPlayerSlot(player, InventoryStorage.ofCursor(screenHandler));
	}

	/**
	 * Return a context for a slot, with the passed player as fallback.
	 */
	static ContainerItemContext ofPlayerSlot(PlayerEntity player, SingleSlotStorage<ItemVariant> slot) {
		return new PlayerContainerItemContext(player, slot);
	}

	/**
	 * Try to find an API instance for the passed lookup and return it, or {@code null} if there is none.
	 * The API is queried for the current variant, if it's not blank.
	 *
	 * @see ItemApiLookup#find
	 */
	@Nullable
	default <A> A find(ItemApiLookup<A, ContainerItemContext> lookup) {
		return getItemVariant().isBlank() ? null : lookup.find(getItemVariant().toStack(), this);
	}

	/**
	 * Return the current item variant of this context, that is the variant in the slot of the context.
	 * If the result is non blank, {@link #getAmount} should be
	 */
	default ItemVariant getItemVariant() {
		return getMainSlot().getResource();
	}

	/**
	 * Return the current amount of {@link #getItemVariant()} in the slot of the context.
	 *
	 * @throws IllegalStateException If {@linkplain #getItemVariant() the current variant} is blank.
	 */
	default long getAmount() {
		if (getItemVariant().isBlank()) {
			throw new IllegalStateException("Amount may not be queried when the current resource is blank.");
		}

		return getMainSlot().getAmount();
	}

	/**
	 * Try to insert some items into this context, prioritizing the main slot over the rest of the inventory.
	 *
	 * @see Storage#insert
	 */
	default long insert(ItemVariant itemVariant, long maxAmount, TransactionContext transaction) {
		// Main slot first
		long mainInserted = getMainSlot().insert(itemVariant, maxAmount, transaction);
		// Overflow second
		long overflowInserted = insertOverflow(itemVariant, maxAmount - mainInserted, transaction);

		return mainInserted + overflowInserted;
	}

	/**
	 * Try to extract some items from this context's slot.
	 *
	 * @see Storage#extract
	 */
	default long extract(ItemVariant itemVariant, long maxAmount, TransactionContext transaction) {
		return getMainSlot().extract(itemVariant, maxAmount, transaction);
	}

	/**
	 * Try to transform as many as possibly of {@linkplain #getItemVariant() the current variant} into another variant.
	 * That is, extract the old variant, and insert the same amount of the new variant instead.
	 *
	 * @param into The variant of the items after the conversion. May not be blank.
	 * @param maxAmount The maximum amount of items to convert. May not be negative.
	 * @param transaction The transaction this operation is part of.
	 * @return A nonnegative integer not greater than maxAmount: the amount that was transformed.
	 */
	default long transform(ItemVariant into, long maxAmount, TransactionContext transaction) {
		StoragePreconditions.notBlankNotNegative(into, maxAmount);

		try (Transaction nested = transaction.openNested()) {
			long extracted = extract(getItemVariant(), maxAmount, nested);

			if (insert(into, maxAmount, nested) == extracted) {
				nested.commit();
				return extracted;
			}
		}

		return 0;
	}

	/**
	 * Return the main slot of this context.
	 */
	SingleSlotStorage<ItemVariant> getMainSlot();

	/**
	 * Try to insert overflow items into this context.
	 *
	 * @see Storage#insert
	 */
	long insertOverflow(ItemVariant itemVariant, long maxAmount, TransactionContext transactionContext);

	/**
	 * Get additional slots that may be available in this context.
	 * These may or may not include the main slot of this context.
	 *
	 * @return An unmodifiable list containing additional slots of this context. If no additional slot is available, the list is empty.
	 */
	List<SingleSlotStorage<ItemVariant>> getAdditionalSlots();

	/**
	 * Return the world of this context.
	 */
	World getWorld();
}
