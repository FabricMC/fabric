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

package net.fabricmc.fabric.impl.lookup.item;

import java.util.Objects;

import org.jetbrains.annotations.Nullable;

import net.minecraft.item.Item;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import net.fabricmc.fabric.api.lookup.v1.item.ItemKey;

public class ItemKeyImpl implements ItemKey {
	private final Item item;
	private final @Nullable CompoundTag tag;
	private final int hashCode;

	ItemKeyImpl(Item item, CompoundTag tag) {
		this.item = item;
		this.tag = tag == null ? null : tag.copy(); // defensive copy
		hashCode = Objects.hash(item, tag);
	}

	@Override
	public Item getItem() {
		return item;
	}

	@Override
	public boolean hasTag() {
		return tag != null;
	}

	@Override
	public boolean tagMatches(@Nullable CompoundTag other) {
		return Objects.equals(tag, other);
	}

	@Override
	public @Nullable CompoundTag copyTag() {
		return tag == null ? null : tag.copy();
	}

	@Override
	public CompoundTag toTag() {
		CompoundTag result = new CompoundTag();
		result.putString("item", Registry.ITEM.getId(item).toString());

		if (tag != null) {
			result.put("tag", tag.copy());
		}

		return result;
	}

	@Override
	public void toPacket(PacketByteBuf buf) {
		buf.writeVarInt(Registry.ITEM.getRawId(item));
		buf.writeCompoundTag(tag);
	}

	@Override
	public String toString() {
		return "ItemKeyImpl{item=" + item + ", tag=" + tag + '}';
	}

	@Override
	public boolean equals(Object o) {
		// succeed fast with == check
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ItemKeyImpl itemKey = (ItemKeyImpl) o;
		// fail fast with hash code
		return hashCode == itemKey.hashCode && item == itemKey.item && tagMatches(itemKey.tag);
	}

	@Override
	public int hashCode() {
		return hashCode;
	}

	public static ItemKey of(Item item, @Nullable CompoundTag tag) {
		return ItemKeyCache.get(item, tag);
	}

	public static ItemKey fromTag(CompoundTag tag) {
		if (tag == null) {
			return ItemKey.EMPTY;
		}

		Item item = Registry.ITEM.get(new Identifier(tag.getString("item")));
		CompoundTag aTag = tag.contains("tag") ? tag.getCompound("tag") : null;
		return of(item, aTag);
	}

	public static ItemKey fromPacket(PacketByteBuf buf) {
		Item item = Registry.ITEM.get(buf.readVarInt());
		CompoundTag tag = buf.readCompoundTag();
		return of(item, tag);
	}
}
