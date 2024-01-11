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

import java.util.Iterator;
import java.util.List;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.fabricmc.fabric.api.modelevents.PartTreePath;

@ApiStatus.Internal
public class PartTreePathImpl implements PartTreePath {
    public static final PartTreePathImpl EMPTY = new PartTreePathImpl(List.of());

    private final List<String> path;
    private final int depth;
    @Nullable
    private String pathString;

    public PartTreePathImpl(List<String> path) {
        this.path = new ObjectArrayList<>(path);
        this.depth = path.size();
    }

    @Override
    public int depth() {
        return depth;
    }

    @Override
    public int indexOf(PartTreePath path) {
        int start = -1;
        int index = -1;
        for (String fragment : path) {
            if (++index == 0) {
                start = this.path.indexOf(fragment);
                if (start == -1) return -1;
            }
            if (start + index >= depth) return -1;
            if (!this.path.get(start + index).contentEquals(fragment)) return -1;
        }

        return start;
    }

    @Override
    public boolean endsWith(PartTreePath path) {
        return toString().endsWith(path.toString());
    }

    public PartTreePathImpl append(String name) {
        PartTreePathImpl copy = new PartTreePathImpl(path);
        copy.path.add(name);
        return copy;
    }

    @Override
    public String toString() {
        if (pathString == null) {
            pathString = String.join("/", path.toArray(String[]::new));
        }
        return pathString;
    }

    @Override
    public int hashCode() {
        return path.hashCode();
    }

    @Override
    public Iterator<String> iterator() {
        return path.iterator();
    }
}
