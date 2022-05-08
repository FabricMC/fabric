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

package net.fabricmc.fabric.api.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.google.common.collect.ImmutableCollection;

public class ImmutableCollectionRedirect<T, C extends Collection<T>> {
	protected final Supplier<C> collectionSupplier;
	protected final Consumer<C> collectionSetter;

	public ImmutableCollectionRedirect(Supplier<C> collectionSupplier, Consumer<C> collectionSetter) {
		this.collectionSupplier = collectionSupplier;
		this.collectionSetter = collectionSetter;
	}

	public void add(T value) {
		this.ensureMutable();

		this.collectionSupplier.get().add(value);
	}

	public boolean remove(T value) {
		this.ensureMutable();

		return this.collectionSupplier.get().remove(value);
	}

	public boolean contains(T value) {
		return this.collectionSupplier.get().contains(value);
	}

	@SuppressWarnings("unchecked")
	protected void ensureMutable() {
		Collection<T> collection = this.collectionSupplier.get();

		if (collection instanceof ImmutableCollection immutableCollection) {
			if (immutableCollection instanceof Set) {
				this.collectionSetter.accept((C) new HashSet<>(collection));
			} else if (immutableCollection instanceof List) {
				this.collectionSetter.accept((C) new ArrayList<>(collection));
			} else {
				throw new IllegalArgumentException("redirect does not know how to make " + collection.getClass().toString() + " a mutable collection");
			}
		}
	}
}
