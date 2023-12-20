package net.fabricmc.fabric.impl.attachment;

import net.minecraft.nbt.NbtCompound;

import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;

public interface AttachmentTargetImpl extends AttachmentTarget {
	/**
	 * Writes all the attached data to NBT.
	 *
	 * @param nbt the NBT to write to
	 */
	default void writeAttachmentsToNbt(NbtCompound nbt) {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	/**
	 * Reads all the attached data from NBT.
	 *
	 * @param nbt the NBT to read from
	 */
	default void readAttachmentsFromNbt(NbtCompound nbt) {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	/**
	 * @return whether the {@link AttachmentTarget} has any attachment data to serialize
	 */
	default boolean hasPersistentAttachments() {
		throw new UnsupportedOperationException("Implemented via mixin");
	}
}
