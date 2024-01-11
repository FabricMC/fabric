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

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;

import net.fabricmc.fabric.api.modelevents.ModelPartCallbacks;
import net.fabricmc.fabric.api.modelevents.PartTreePath;

@VisibleForTesting
@ApiStatus.Internal
public class PathTree<T> {
    private final Map<Key, T> entries = new HashMap<>();
    private final Node<T> treeNode = new Node<>();

    public T getOrCreate(ModelPartCallbacks.MatchingStrategy matchingStrategy, PartTreePath path, Supplier<T> constructor) {
        if (matchingStrategy == ModelPartCallbacks.MatchingStrategy.EXACT) {
            return treeNode.getOrCreate(path, constructor);
        }

        return entries.computeIfAbsent(new Key(matchingStrategy, path), k -> constructor.get());
    }

    public void findMatchingLeafNodes(PartTreePath path, Consumer<T> consumer) {
        T exactMatch = treeNode.get(path);
        if (exactMatch != null) {
            consumer.accept(exactMatch);
        }
        entries.forEach((key, value) -> {
            if (key.test(path)) {
                consumer.accept(value);
            }
        });
    }

    private static class Node<T> {
        private final Map<String, Node<T>> nodes = new HashMap<>();
        private T value;

        public T getOrCreate(PartTreePath path, Supplier<T> constructor) {
            Node<T> node = this;
            for (String fragment : path) {
                node = nodes.computeIfAbsent(fragment, f -> new Node<>());
            }
            return node.value == null ? (node.value = constructor.get()) : node.value;
        }

        @Nullable
        public T get(PartTreePath path) {
            Node<T> node = this;
            for (String fragment : path) {
                node = nodes.get(fragment);
                if (node == null) {
                    return null;
                }
            }
            return node.value;
        }
    }

    record Key(
        ModelPartCallbacks.MatchingStrategy matchingStrategy,
        PartTreePath path
    ) implements Predicate<PartTreePath> {
        @Override
        public boolean test(PartTreePath path) {
            return switch (matchingStrategy()) {
                case EXACT -> path.equals(path());
                case STARTS_WITH -> path.beginsWith(path());
                case ENDS_WITH -> path.endsWith(path());
                case CONTAINS -> path.includes(path());
            };
        }
    }
}
