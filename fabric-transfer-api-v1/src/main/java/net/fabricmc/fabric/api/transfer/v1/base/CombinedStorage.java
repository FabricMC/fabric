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

package net.fabricmc.fabric.api.transfer.v1.base;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageFunction;

public class CombinedStorage<T, S extends Storage<T>> implements Storage<T> {
	protected final List<S> parts;
	protected final StorageFunction<T> insertionFunction;
	protected final StorageFunction<T> extractionFunction;

	public CombinedStorage(List<S> parts) {
		this.parts = new ArrayList<>(parts);
		this.insertionFunction = new CombinedStorageFunction<>(parts.stream().map(Storage::insertionFunction).collect(Collectors.toList()));
		this.extractionFunction = new CombinedStorageFunction<>(parts.stream().map(Storage::extractionFunction).collect(Collectors.toList()));
	}

	@Override
	public StorageFunction<T> insertionFunction() {
		return insertionFunction;
	}

	@Override
	public StorageFunction<T> extractionFunction() {
		return extractionFunction;
	}

	@Override
	public boolean forEach(Visitor<T> visitor) {
		for (S part : parts) {
			if (part.forEach(visitor)) {
				return true;
			}
		}

		return false;
	}
}
