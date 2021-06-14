package net.fabricmc.fabric.api.transfer.v1.storage;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * An immutable association of an immutable resource instance (for example {@code Item} or {@code Fluid}) and an optional NBT tag.
 *
 * <p>This is exposed for convenience for code that needs to be generic across multiple resource keys,
 * but note that a {@link Storage} is not necessarily bound to {@code ResourceKey}. It can contain any immutable object.
 *
 * @deprecated Experimental feature, we reserve the right to remove or change it without further notice.
 * The transfer API is a complex addition, and we want to be able to correct possible design mistakes.
 */
@ApiStatus.Experimental
@Deprecated
public interface ResourceKey<T> {
	/**
	 * Return true if this key is empty, and false otherwise.
	 */
	boolean isEmpty();

	/**
	 * Return the resource of this key.
	 */
	T getResource();

	/**
	 * Return the underlying tag.
	 *
	 * <p><b>NEVER MUTATE THIS TAG</b>, if you need to mutate it you can use {@link #copyTag()} to retrieve a copy instead.
	 */
	@Nullable
	NbtCompound getTag();

	/**
	 * Return true if this key has a tag, false otherwise.
	 */
	default boolean hasTag() {
		return getTag() != null;
	}

	/**
	 * Return true if the tag of this key matches the passed tag, and false otherwise.
	 *
	 * <p>Note: True is returned if both tags are {@code null}.
	 */
	default boolean tagMatches(@Nullable NbtCompound other) {
		return Objects.equals(getTag(), other);
	}

	/**
	 * Return {@code true} if the resource of this key matches the passed fluid.
	 */
	default boolean isOf(T resource) {
		return getResource() == resource;
	}

	/**
	 * Return a copy of the tag of this key, or {@code null} if this key doesn't have a tag.
	 *
	 * <p>Note: Use {@link #tagMatches} if you only need to check for tag equality, or {@link #getTag()} if you don't need to mutate the tag.
	 */
	@Nullable
	default NbtCompound copyTag() {
		NbtCompound tag = getTag();
		return tag == null ? null : tag.copy();
	}

	/**
	 * Save this key into an NBT compound tag. Subinterfaces should have a matching static {@code fromNbt}.
	 *
	 * <p>Note: This key is safe to use for persisting data as resources are saved using their full Identifier.
	 */
	NbtCompound toNbt();


	/**
	 * Write this key into a packet byte buffer. Subinterfaces should have a matching static {@code fromPacket}.
	 *
	 * <p>Implementation note: Resources are saved using their raw registry integer id.
	 */
	void toPacket(PacketByteBuf buf);
}
