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

import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import com.google.common.base.Strings;
import com.google.common.collect.Interner;
import com.google.common.collect.Interners;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.fabricmc.fabric.api.modelevents.PartTreePath;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;

@ApiStatus.Internal
public final class PartTreePathImpl implements PartTreePath {
    private static final Interner<PartTreePathImpl> INTERNER = Interners.newWeakInterner();
    public static final PartTreePathImpl EMPTY = new PartTreePathImpl(new String[0]);

    private final String[] path;
    @Nullable
    private String pathString;

    @ApiStatus.Internal
    public static PartTreePath of(String path) {
        // resolve null and remove whitespace/control characters.
        path = Strings.nullToEmpty(path).trim().strip();
        if (path.isEmpty()) {
            return EMPTY;
        }
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        if (!Identifier.isPathValid(path)) {
            throw new InvalidIdentifierException("Non [a-z0-9/._-] character in path of location: " + path);
        }

        return path.isEmpty()
                ? EMPTY
                : INTERNER.intern(new PartTreePathImpl(path.split("/")));
    }

    private PartTreePathImpl(String[] path) {
        this.path = path;
    }

    @Override
    public int depth() {
        return path.length;
    }

    @Override
    public int indexOf(PartTreePath path) {
        Objects.requireNonNull(path);
        if (path.depth() == depth()) {
            // if the path is the same length as us, is the same as an equality check.
            return equals(path) ? 0 : -1;
        }
        if (path.depth() > depth()) {
            // trivial case: we obviously cannot contain a path that is longer than us
            return -1;
        }

        // non-trivial case: the path is shorter than us, time to get SMRT!
        String[] parts = ((PartTreePathImpl)path).path;

        IntArrayList possibleStarts = new IntArrayList(depth());

        // locate potential starts
        for (int i = 0; i < this.path.length; i++) {
            if (Objects.equals(this.path[0], parts[0])) {
                if (path.depth() == 1) {
                    // trivial case: if the path we're searching for is length 1, we don't have to go any further
                    return i;
                }
                possibleStarts.add(i);
            }
        }

        // check which of the starts also match the rest of the path
        for (int start : possibleStarts) {
            if (subSectionEquals(start, parts)) {
                return start;
            }
        }

        // nothing matched
        return -1;
    }

    @Override
    public boolean beginsWith(PartTreePath path) {
        Objects.requireNonNull(path);
        // we override the default with a more optimized strategy. We only care if the start of our array matches so only check that case
        return depth() >= path.depth() && subSectionEquals(0, ((PartTreePathImpl)path).path);
    }

    @Override
    public boolean endsWith(PartTreePath path) {
        Objects.requireNonNull(path);
        // ditto to beginsWith(path)
        return depth() >= path.depth() && subSectionEquals(depth() - path.depth(), ((PartTreePathImpl)path).path);
    }

    private boolean subSectionEquals(int start, String[] values) {
        return Arrays.equals(path, start, start + values.length, values, 0, values.length - 1);
    }

    public PartTreePathImpl append(String name) {
        Objects.requireNonNull(name);
        String[] newPath = Arrays.copyOf(path, path.length + 1);
        newPath[newPath.length - 1] = name;
        return new PartTreePathImpl(newPath);
    }

    @Override
    public String toString() {
        if (pathString == null) {
            pathString = String.join("/", path);
        }
        return pathString;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(path);
    }

    @Override
    public boolean equals(Object o) {
        return o == this || (o instanceof PartTreePathImpl p && Arrays.equals(path, p.path));
    }

    @Override
    public Iterator<String> iterator() {
        return Arrays.asList(path).iterator();
    }
}
