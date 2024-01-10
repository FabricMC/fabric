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

package net.fabricmc.fabric.impl.modelevents;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.jetbrains.annotations.ApiStatus;

import net.fabricmc.fabric.api.modelevents.data.DataCollection;

@ApiStatus.Internal
public class ListDataCollection<K, T> implements DataCollection<T> {
    private final List<K> values;
    private final Function<K, T> lookupFunction;

    public ListDataCollection(List<K> values, Function<K, T> lookupFunction) {
        this.values = values;
        this.lookupFunction = lookupFunction;
    }

    @Override
    public int size() {
        return values.size();
    }

    @Override
    public Optional<T> getAt(int index) {
        if (index < 0 || index >= size()) {
            return Optional.empty();
        }
        return Optional.of(lookupFunction.apply(values.get(index)));
    }

    @Override
    public Optional<T> getFirst() {
        return getAt(0);
    }

    @Override
    public Optional<T> getLast() {
        return getAt(size() - 1);
    }
}
