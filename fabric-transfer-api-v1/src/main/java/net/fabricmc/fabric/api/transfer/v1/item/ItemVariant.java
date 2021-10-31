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

package net.fabricmc.fabric.api.transfer.v1.item;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;

import net.fabricmc.fabric.impl.transfer.item.ItemVariantImpl;
import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant;

/**
 * An immutable count-less ItemStack, i.e. an immutable association of an item and an optional NBT compound tag.
 *
 * <p>Do not implement, use the static {@code of(...)} functions instead.
 *
 * <p><b>Experimental feature</b>, we reserve the right to remove or change it without further notice.
 * The transfer API is a complex addition, and we want to be able to correct possible design mistakes.
 */
@ApiStatus.Experimental
@ApiStatus.NonExtendable
public interface ItemVariant extends TransferVariant<Item> {
	/**
	 * Retrieve a blank ItemVariant.
	 */
	static ItemVariant blank() {
		return of(Items.AIR);
	}

	/**
	 * Retrieve an ItemVariant with the item and tag of a stack.
	 */
	static ItemVariant of(ItemStack stack) {
		return of(stack.getItem(), stack.getTag());
	}

	/**
	 * Retrieve an ItemVariant with an item and without a tag.
	 */
	static ItemVariant of(ItemConvertible item) {
		return of(item, null);
	}

	/**
	 * Retrieve an ItemVariant with an item and an optional tag.
	 */
	static ItemVariant of(ItemConvertible item, @Nullable CompoundTag tag) {
		return ItemVariantImpl.of(item.asItem(), tag);
	}

	/**
	 * Return true if the item and tag of this variant match those of the passed stack, and false otherwise.
	 */
	default boolean matches(ItemStack stack) {
		return isOf(stack.getItem()) && nbtMatches(stack.getTag());
	}

	/**
	 * Return the item of this variant.
	 */
	default Item getItem() {
		return getObject();
	}

	/**
	 * Create a new item stack with count 1 from this variant.
	 */
	default ItemStack toStack() {
		return toStack(1);
	}

	/**
	 * Create a new item stack from this variant.
	 *
	 * @param count The count of the returned stack. It may lead to counts higher than maximum stack size.
	 */
	default ItemStack toStack(int count) {
		if (isBlank()) return ItemStack.EMPTY;
		ItemStack stack = new ItemStack(getItem(), count);
		stack.setTag(copyNbt());
		return stack;
	}

	/**
	 * Deserialize a variant from an NBT compound tag, assuming it was serialized using
	 * {@link #toNbt}. If an error occurs during deserialization, it will be logged
	 * with the DEBUG level, and a blank variant will be returned.
	 */
	static ItemVariant fromNbt(CompoundTag nbt) {
		return ItemVariantImpl.fromNbt(nbt);
	}

	/**
	 * Write a variant from a packet byte buffer, assuming it was serialized using
	 * {@link #toPacket}.
	 */
	static ItemVariant fromPacket(PacketByteBuf buf) {
		return ItemVariantImpl.fromPacket(buf);
	}
}
