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

package net.fabricmc.fabric.api.client.modelevents.v1.data;

import java.util.Optional;

import org.jetbrains.annotations.ApiStatus;

import net.fabricmc.fabric.impl.client.modelevents.EmptyDataCollection;

/**
 * Provides a limited, performance-prioritized, immutable view of a collection of data.
 * <p>
 * Individual elements can be accessed using {@link DataCollection#getAt(int)} with
 * special members provided for the common use case of {@link DataCollection#getFirst()}
 * and {@link DataCollection#getLast()} for getting the first and last element in the collection.
 * <p>
 * As all getter methods return {@link Optional}s little additional logic is required to
 * account for cases where the data collection may be empty.
 *
 * Example usage:
 * <pre>
 *
 * collection.getFirst().ifPresent(t -> {
 *   doMyThingWithT(t);
 * });
 * collection.getLast().ifPresent(t -> {
 *   doMyThingWithT(t);
 * });
 *
 * </pre>
 *
 * @param <T> The data elements this collection contains.
 */
@ApiStatus.NonExtendable
public interface DataCollection<T> extends Iterable<T> {
    /**
     * Returns an empty data collection for elements of type {@code T}.
     */
    @SuppressWarnings("unchecked")
    static <T> DataCollection<T> of() {
        return (DataCollection<T>)EmptyDataCollection.INSTANCE;
    }

    /**
     * Gets the number of elements accessible through this collection.
     */
    int size();

    /**
     * Gets a single element at a given position within this collection.
     * If the collection is empty of the requested index does not correspond to
     * an element contained in this collection, will instead return an empty optional.
     *
     * @param index Element index
     * @return Optional element at the requested index
     */
    Optional<T> getAt(int index);

    /**
     * Gets the first element within this collection.
     * Calling this is the equivalent of calling {@code collection.getAt(0)}
     *
     * @return Optional first element
     */
    default Optional<T> getFirst() {
        return getAt(0);
    }

    /**
     * Gets the last element within this collection.
     * Calling this is the equivalent of calling {@code collection.getAt(collection.size() - 1)}
     *
     * @return Optional first element
     */
    default Optional<T> getLast() {
        return getAt(size() - 1);
    }
}
