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

package net.fabricmc.fabric.api.attachment.v1;

import java.util.function.UnaryOperator;

import org.jetbrains.annotations.Nullable;

import net.minecraft.nbt.NbtCompound;

public interface AttachmentTarget {
	default <A> A getAttached(Attachment<A> attachment) {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	@Nullable
	default <A> A setAttached(Attachment<A> attachment, @Nullable A value) {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	default boolean hasAttached(Attachment<?> attachment) {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	@Nullable
	default <A> A removeAttached(Attachment<A> attachment) {
		return setAttached(attachment, null);
	}

	@Nullable
	default <A> A modifyAttached(Attachment<A> attachment, UnaryOperator<A> modifier) {
		return setAttached(attachment, modifier.apply(getAttached(attachment)));
	}

	default void writeAttachmentsToNbt(NbtCompound nbt) {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	default void readAttachmentsFromNbt(NbtCompound nbt) {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	default boolean hasSerializableAttachments() {
		throw new UnsupportedOperationException("Implemented via mixin");
	}
}
