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

package net.fabricmc.fabric.impl.tag.extension;

import java.util.Collection;
import java.util.function.Supplier;

import net.minecraft.tag.Tag;
import net.minecraft.tag.TagContainer;
import net.minecraft.util.Identifier;

public final class TagDelegate<T> extends Tag<T> {
	private final Supplier<TagContainer<T>> containerSupplier;
	private volatile Target<T> target;

	public TagDelegate(Identifier id, Supplier<TagContainer<T>> containerSupplier) {
		super(id);

		this.containerSupplier = containerSupplier;
	}

	@Override
	public boolean contains(T var1) {
		return getTag().contains(var1);
	}

	@Override
	public Collection<T> values() {
		return getTag().values();
	}

	@Override
	public Collection<Tag.Entry<T>> entries() {
		return getTag().entries();
	}

	/**
	 * Retrieve the tag this delegate is pointing to, computing it if missing or outdated.
	 *
	 * <p>Thread safety is being ensured by using an immutable holder object for consistently retrieving both result
	 * and condition, volatile for safe publishing and assuming TagContainer.getOrCreate is safe to call concurrently.
	 *
	 * <p>It should be possible to exploit a benign data race on this.target by removing volatile, but this option
	 * hasn't been chosen yet since a performance problem in the area is yet to be proven.
	 */
	private Tag<T> getTag() {
		Target<T> target = this.target;
		TagContainer<T> reqContainer = containerSupplier.get();
		Tag<T> ret;

		if (target == null || target.container != reqContainer) {
			ret = reqContainer.getOrCreate(getId());
			this.target = new Target<>(reqContainer, ret);
		} else {
			ret = target.tag;
		}

		return ret;
	}

	private static final class Target<T> {
		Target(TagContainer<T> container, Tag<T> tag) {
			this.container = container;
			this.tag = tag;
		}

		final TagContainer<T> container;
		final Tag<T> tag;
	}
}
