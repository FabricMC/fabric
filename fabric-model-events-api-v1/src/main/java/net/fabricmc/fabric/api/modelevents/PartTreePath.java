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

package net.fabricmc.fabric.api.modelevents;

import net.fabricmc.fabric.impl.modelevents.PartTreePathImpl;

/**
 * Represents the absolute or relative path of a part within a model's tree.
 */
public interface PartTreePath extends Iterable<String> {
    /**
     * Gets the root path.
     */
    static PartTreePath of() {
        return PartTreePathImpl.EMPTY;
    }

    /**
     * Creates a new model path from a string value.
     */
    static PartTreePath of(String path) {
        return PartTreePathImpl.of(path);
    }

    /**
     * Finds the position at which a specific part appears on this path.
     * If the part cannot be found will return -1.
     * @return The index position of the part, otherwise -1.
     */
    int indexOf(PartTreePath path);

    /**
     * The depth of this path.
     */
    int depth();

    /**
     * Checks whether any of the part names on this path matches the requested part.
     */
    default boolean includes(PartTreePath path) {
        return indexOf(path) != -1;
    }

    /**
     * Checks whether the specified part name corresponds to the first element in this path.
     */
    default boolean beginsWith(PartTreePath path) {
        return indexOf(path) == 0;
    }

    /**
     * Checks whether the specified part name corresponds to the last element in this path.
     */
    default boolean endsWith(PartTreePath path) {
        return indexOf(path) == (depth() - path.depth());
    }

    /**
     * Checks whether this path is empty (is the root path).
     */
    default boolean isRoot() {
        return depth() == 0;
    }
}
