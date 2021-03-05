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

package net.fabricmc.fabric.api.lookup.v1.item;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;

import net.fabricmc.fabric.impl.lookup.item.ItemKeyImpl;

/**
 * An immutable count-less ItemStack, i.e. an immutable association of an item and an optional NBT compound tag.
 *
 * <p>Do not implement, use the static {@code of(...)} functions instead.
 */
@ApiStatus.NonExtendable
public interface ItemKey {
	/**
	 * Retrieve an empty ItemKey.
	 */
	static ItemKey empty() {
		return of(Items.AIR);
	}

	/**
	 * Retrieve an ItemKey with the item and tag of a stack.
	 */
	static ItemKey of(ItemStack stack) {
		return of(stack.getItem(), stack.getTag());
	}

	/**
	 * Retrieve an ItemKey with an item and without a tag.
	 */
	static ItemKey of(ItemConvertible item) {
		return of(item, null);
	}

	/**
	 * Retrieve an ItemKey with an item and an optional tag.
	 */
	static ItemKey of(ItemConvertible item, @Nullable CompoundTag tag) {
		return ItemKeyImpl.of(item.asItem(), tag);
	}

	/**
	 * Return true if this key is empty, i.e. its item is Items.AIR, and false otherwise.
	 */
	boolean isEmpty();

	/**
	 * Return true if the item and tag of this key match those of the passed stack, and false otherwise.
	 */
	boolean matches(ItemStack stack);

	/**
	 * Return true if the tag of this key matches the passed tag, and false otherwise.
	 *
	 * <p>Note: True is returned if both tags are {@code null}.
	 */
	boolean tagMatches(@Nullable CompoundTag other);

	/**
	 * Return true if this key has a tag, false otherwise.
	 */
	boolean hasTag();

	/**
	 * Return the item of this key.
	 */
	Item getItem();

	/**
	 * Return a copy of the tag of this key, or {@code null} if this key doesn't have a tag.
	 *
	 * <p>Note: use {@link #tagMatches} if you only need to check for tag equality.
	 */
	@Nullable CompoundTag copyTag();

	/**
	 * Create a new item stack with count 1 from this key.
	 */
	ItemStack toStack();

	/**
	 * Create a new item stack from this key.
	 *
	 * @param count The count of the returned stack. It may lead to counts higher than maximum stack size.
	 */
	ItemStack toStack(int count);

	/**
	 * Save this key into an NBT compound tag. {@link #fromNbt} can be used to retrieve the key later.
	 *
	 * <p>Note: This key is safe to use for persisting data as items are saved using their full Identifier.
	 */
	CompoundTag toNbt();

	/**
	 * Deserialize a key from an NBT compound tag, assuming it was serialized using {@link #toNbt}.
	 * If an error occurs during deserialization, it will be logged with the DEBUG level, and an empty key will be returned.
	 */
	static ItemKey fromNbt(CompoundTag nbt) {
		return ItemKeyImpl.fromNbt(nbt);
	}

	/**
	 * Save this key into a packet byte buffer. {@link #fromPacket} can be used to retrieve the key later.
	 *
	 * <p>Note: Items are saved using their raw registry integer id.
	 */
	void toPacket(PacketByteBuf buf);

	/**
	 * Write a key from a packet byte buffer, assuming it was serialized using {@link #toPacket}.
	 */
	static ItemKey fromPacket(PacketByteBuf buf) {
		return ItemKeyImpl.fromPacket(buf);
	}
}
