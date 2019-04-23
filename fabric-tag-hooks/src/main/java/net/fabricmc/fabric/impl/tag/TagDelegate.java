/*
 * Copyright (c) 2016, 2017, 2018 FabricMC
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

package net.fabricmc.fabric.impl.tag;

import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

import java.util.Collection;

public class TagDelegate<T> extends Tag<T> {
	protected Tag<T> delegate;

	public TagDelegate(Identifier id, Tag<T> delegate) {
		super(id);
		this.delegate = delegate;
	}

	protected void onAccess() {

	}

	@Override
	public boolean contains(T var1) {
		onAccess();
		return delegate.contains(var1);
	}

	@Override
	public Collection<T> values() {
		onAccess();
		return delegate.values();
	}

	@Override
	public Collection<Tag.Entry<T>> entries() {
		onAccess();
		return delegate.entries();
	}
}
