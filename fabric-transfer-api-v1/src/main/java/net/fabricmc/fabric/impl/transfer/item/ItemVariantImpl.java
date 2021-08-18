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

package net.fabricmc.fabric.impl.transfer.item;

import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;

public class ItemVariantImpl implements ItemVariant {
	public static ItemVariant of(Item item, @Nullable CompoundTag tag) {
		Objects.requireNonNull(item, "Item may not be null.");

		// Only tag-less or empty item variants are cached for now.
		if (tag == null || item == Items.AIR) {
			return ((ItemVariantCache) item).fabric_getCachedItemVariant();
		} else {
			return new ItemVariantImpl(item, tag);
		}
	}

	private static final Logger LOGGER = LogManager.getLogger("fabric-transfer-api-v1/item");

	private final Item item;
	private final @Nullable CompoundTag nbt;
	private final int hashCode;

	public ItemVariantImpl(Item item, CompoundTag nbt) {
		this.item = item;
		this.nbt = nbt == null ? null : nbt.copy(); // defensive copy
		hashCode = Objects.hash(item, nbt);
	}

	@Override
	public Item getObject() {
		return item;
	}

	@Nullable
	@Override
	public CompoundTag getNbt() {
		return nbt;
	}

	@Override
	public boolean isBlank() {
		return item == Items.AIR;
	}

	@Override
	public CompoundTag toNbt() {
		CompoundTag result = new CompoundTag();
		result.putString("item", Registry.ITEM.getId(item).toString());

		if (nbt != null) {
			result.put("tag", nbt.copy());
		}

		return result;
	}

	public static ItemVariant fromNbt(CompoundTag tag) {
		try {
			Item item = Registry.ITEM.get(new Identifier(tag.getString("item")));
			CompoundTag aTag = tag.contains("tag") ? tag.getCompound("tag") : null;
			return of(item, aTag);
		} catch (RuntimeException runtimeException) {
			LOGGER.debug("Tried to load an invalid ItemVariant from NBT: {}", tag, runtimeException);
			return ItemVariant.blank();
		}
	}

	@Override
	public void toPacket(PacketByteBuf buf) {
		if (isBlank()) {
			buf.writeBoolean(false);
		} else {
			buf.writeBoolean(true);
			buf.writeVarInt(Item.getRawId(item));
			buf.writeCompoundTag(nbt);
		}
	}

	public static ItemVariant fromPacket(PacketByteBuf buf) {
		if (!buf.readBoolean()) {
			return ItemVariant.blank();
		} else {
			Item item = Item.byRawId(buf.readVarInt());
			CompoundTag nbt = buf.readCompoundTag();
			return of(item, nbt);
		}
	}

	@Override
	public String toString() {
		return "ItemVariantImpl{item=" + item + ", tag=" + nbt + '}';
	}

	@Override
	public boolean equals(Object o) {
		// succeed fast with == check
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ItemVariantImpl ItemVariant = (ItemVariantImpl) o;
		// fail fast with hash code
		return hashCode == ItemVariant.hashCode && item == ItemVariant.item && nbtMatches(ItemVariant.nbt);
	}

	@Override
	public int hashCode() {
		return hashCode;
	}
}
