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

import org.jetbrains.annotations.ApiStatus;

import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;

/**
 * A view of a single stored resource in a {@link Storage}, for use with {@link Storage#iterator} or {@link Storage#exactView}.
 *
 * @param <T> The type of the stored resource.
 *
 * <b>Experimental feature</b>, we reserve the right to remove or change it without further notice.
 * The transfer API is a complex addition, and we want to be able to correct possible design mistakes.
 */
@ApiStatus.Experimental
public interface StorageView<T> {
	/**
	 * Try to extract a resource from this view.
	 *
	 * @return The amount that was extracted.
	 */
	long extract(T resource, long maxAmount, TransactionContext transaction);

	/**
	 * Return {@code true} if the {@link #getResource} contained in this storage view is blank, or {@code false} otherwise.
	 *
	 * <p>This function is mostly useful when dealing with storages of arbitrary types.
	 * For transfer variant storages, this should always be equivalent to {@code getResource().isBlank()}.
	 */
	boolean isResourceBlank();

	/**
	 * @return The resource stored in this view. May not be blank if {@link #isResourceBlank} is {@code false}.
	 */
	T getResource();

	/**
	 * @return The amount of {@link #getResource} stored in this view.
	 */
	long getAmount();

	/**
	 * @return The total amount of {@link #getResource} that could be stored in this view,
	 * or an estimate of the number of resources that could be stored if this view has a blank resource.
	 */
	long getCapacity();

	/**
	 * If this is view is a delegate around another storage view, return the underlying view.
	 * This can be used to check if two views refer to the same inventory "slot".
	 * <b>Do not try to extract from the underlying view, or you risk bypassing some checks.</b>
	 *
	 * <p>It is expected that two storage views with the same underlying view ({@code a.getUnderlyingView() == b.getUnderlyingView()})
	 * share the same content, and mutating one should mutate the other. However, one of them may allow extraction, and the other may not.
	 */
	default StorageView<T> getUnderlyingView() {
		return this;
	}
}
