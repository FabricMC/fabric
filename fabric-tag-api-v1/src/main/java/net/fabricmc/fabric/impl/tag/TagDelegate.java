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

package net.fabricmc.fabric.impl.tag;

import java.util.List;
import java.util.function.Supplier;

import net.minecraft.tag.TagGroup;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

/**
 * Represents a thread-safe delegate which refers to a tag in a tag group.
 *
 * @param <T> the type of object the tag stores
 */
public final class TagDelegate<T> implements Tag.Identified<T>, FabricTagExtensions {
	private final Identifier id;
	private final Supplier<TagGroup<T>> groupSupplier;
	private volatile Target<T> target;
	private int clearCount;

	public static <T> Tag.Identified<T> create(Identifier id, Supplier<TagGroup<T>> groupSupplier) {
		return new TagDelegate<>(id, groupSupplier);
	}

	private TagDelegate(Identifier id, Supplier<TagGroup<T>> groupSupplier) {
		this.id = id;
		this.groupSupplier = groupSupplier;
	}

	@Override
	public boolean contains(T entry) {
		return this.getTag().contains(entry);
	}

	@Override
	public List<T> values() {
		return this.getTag().values();
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
		TagGroup<T> group = this.groupSupplier.get();
		Tag<T> ret;

		if (target == null || target.group != group) {
			ret = group.getTagOrEmpty(getId());
			this.target = new Target<>(group, ret);
		} else {
			ret = target.tag;
		}

		return ret;
	}

	@Override
	public Identifier getId() {
		return this.id;
	}

	@Override
	public boolean fabric_hasBeenReplaced() {
		return this.clearCount > 0;
	}

	@Override
	public void fabric_setClearCount(int clearCount) {
		this.clearCount = clearCount;
	}

	private static final class Target<T> {
		Target(TagGroup<T> group, Tag<T> tag) {
			this.group = group;
			this.tag = tag;
		}

		final TagGroup<T> group;
		final Tag<T> tag;
	}
}
