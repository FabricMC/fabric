package net.fabricmc.fabric.api.lookup.v1.item;

import java.util.Objects;

import org.jetbrains.annotations.Nullable;

import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;

import net.fabricmc.fabric.impl.lookup.item.ItemKeyImpl;

/**
 * The immutable combination of an item and additional NBT data. Compare using {@link ItemKey#equals}.
 */
public interface ItemKey {
	ItemKey EMPTY = ItemKeyImpl.of(Items.AIR, null);

	default boolean isEmpty() {
		return this.equals(EMPTY);
	}

	static ItemKey of(@Nullable ItemStack stack) {
		return stack == null ? EMPTY : of(stack.getItem(), stack.getTag());
	}

	static ItemKey of(ItemConvertible item) {
		return of(item, null);
	}

	static ItemKey of(ItemConvertible item, @Nullable CompoundTag tag) {
		Objects.requireNonNull(item, "item cannot be null");
		if (item.asItem() == Items.AIR) return EMPTY;

		return ItemKeyImpl.of(item.asItem(), tag);
	}

	default ItemStack toStack(int count) {
		if (isEmpty()) {
			return ItemStack.EMPTY;
		}

		ItemStack stack = new ItemStack(getItem(), count);

		if (hasTag()) {
			stack.setTag(copyTag());
		}

		return stack;
	}

	default ItemStack toStack() {
		return toStack(1);
	}

	default boolean matches(ItemStack stack) {
		if (stack == null || stack.isEmpty()) {
			return isEmpty();
		} else {
			return stack.getItem() == getItem() && (stack.hasTag() ? tagMatches(stack.getTag()) : !hasTag());
		}
	}

	Item getItem();

	boolean hasTag();

	boolean tagMatches(@Nullable CompoundTag other);

	@Nullable CompoundTag copyTag();

	@Override
	String toString();

	CompoundTag toTag();

	void toPacket(PacketByteBuf buf);

	static ItemKey fromTag(CompoundTag tag) {
		return ItemKeyImpl.fromTag(tag);
	}

	static ItemKey fromPacket(PacketByteBuf buf) {
		return ItemKeyImpl.fromPacket(buf);
	}
}
