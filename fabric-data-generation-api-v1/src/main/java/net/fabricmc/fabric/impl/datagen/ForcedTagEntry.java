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

package net.fabricmc.fabric.impl.datagen;

import java.util.function.Consumer;
import java.util.function.Predicate;

import net.minecraft.registry.tag.TagEntry;
import net.minecraft.util.Identifier;

public class ForcedTagEntry extends TagEntry {
	private final TagEntry delegate;

	public ForcedTagEntry(TagEntry delegate) {
		super(delegate.id, true, delegate.required);
		this.delegate = delegate;
	}

	@Override
	public <T> boolean resolve(TagEntry.ValueGetter<T> arg, Consumer<T> consumer) {
		return delegate.resolve(arg, consumer);
	}

	@Override
	public boolean canAdd(Predicate<Identifier> objectExistsTest, Predicate<Identifier> tagExistsTest) {
		return true;
	}
}
