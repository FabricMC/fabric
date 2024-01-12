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

package net.fabricmc.fabric.impl.client.modelevents;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.jetbrains.annotations.ApiStatus;

import com.google.common.base.Suppliers;
import com.google.common.collect.AbstractIterator;

import net.fabricmc.fabric.api.client.modelevents.v1.data.DataCollection;

@ApiStatus.Internal
final class ListDataCollection<K, T> implements DataCollection<T> {
    private final Supplier<Optional<T>>[] values;

    public static <K, T> DataCollection<T> of(List<K> values, Function<K, T> lookupFunction) {
        if (values == null || values.isEmpty()) {
            return DataCollection.of();
        }
        return new ListDataCollection<>(values, lookupFunction);
    }

    @SuppressWarnings("unchecked")
    private ListDataCollection(List<K> values, Function<K, T> lookupFunction) {
        this.values = values.stream()
                // Raaaaah don't create a new optional all the time when people access
                .map(value -> Suppliers.memoize(() -> Optional.ofNullable(lookupFunction.apply(value))))
                .toArray(Supplier[]::new);
    }

    @Override
    public int size() {
        return values.length;
    }

    @Override
    public Optional<T> getAt(int index) {
        if (index < 0 || index >= size()) {
            return Optional.empty();
        }
        return values[index].get();
    }

    @Override
    public void forEach(Consumer<? super T> consumer) {
        Objects.requireNonNull(consumer);
        for (var value : values) {
            value.get().ifPresent(consumer);
        }
    }

    @Override
    public Iterator<T> iterator() {
        return new AbstractIterator<>() {
            private int index;

            @Override
            protected T computeNext() {
                Optional<T> value = Optional.empty();
                while (index < size() && (value = values[index++].get()).isEmpty());
                return value.orElseGet(this::endOfData);
            }
        };
    }
}
