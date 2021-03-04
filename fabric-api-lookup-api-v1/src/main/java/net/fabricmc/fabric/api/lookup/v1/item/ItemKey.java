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
 * An immutable count-less ItemStack, i.e. the immutable combination of an item and an optional NBT tag.
 *
 * <p>Do not implement, use the static {@code of(...)} functions instead.
 */
@ApiStatus.NonExtendable
public interface ItemKey {
	ItemKey EMPTY = of(Items.AIR);

	static ItemKey of(ItemStack stack) {
		return of(stack.getItem(), stack.getTag());
	}

	static ItemKey of(ItemConvertible item) {
		return of(item, null);
	}

	static ItemKey of(ItemConvertible item, @Nullable CompoundTag tag) {
		return ItemKeyImpl.of(item.asItem(), tag);
	}

	boolean isEmpty();

	boolean matches(ItemStack stack);

	boolean tagMatches(@Nullable CompoundTag other);

	boolean hasTag();

	Item getItem();

	@Nullable CompoundTag copyTag();

	ItemStack toStack();

	ItemStack toStack(int count);

	CompoundTag toNbt();

	static ItemKey fromNbt(CompoundTag nbt) {
		return ItemKeyImpl.fromNbt(nbt);
	}

	void toPacket(PacketByteBuf buf);

	static ItemKey fromPacket(PacketByteBuf buf) {
		return ItemKeyImpl.fromPacket(buf);
	}
}
