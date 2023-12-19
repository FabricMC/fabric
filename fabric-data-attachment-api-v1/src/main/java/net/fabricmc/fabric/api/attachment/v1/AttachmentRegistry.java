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

import java.util.Objects;
import java.util.function.Supplier;

import org.jetbrains.annotations.Nullable;

import net.minecraft.util.Identifier;

import net.fabricmc.fabric.impl.attachment.AttachmentRegistryImpl;

public final class AttachmentRegistry {
	private AttachmentRegistry() {
	}

	public static <A> Attachment<A> createDefaulted(Identifier id, A defaultValue) {
		Objects.requireNonNull(id, "identifier cannot be null");
		Objects.requireNonNull(defaultValue, "default value cannot be null");

		return AttachmentRegistry.<A>builder().defaultValue(defaultValue).buildAndRegister(id);
	}

	public static <A> Attachment<A> createDefaulted(Identifier id, A defaultValue, AttachmentSerializer<A> serializer) {
		Objects.requireNonNull(id, "identifier cannot be null");
		Objects.requireNonNull(defaultValue, "default value cannot be null");
		Objects.requireNonNull(serializer, "serializer cannot be null");

		return AttachmentRegistry.<A>builder().defaultValue(defaultValue).buildAndRegister(id);
	}

	@Nullable
	public static Attachment<?> get(Identifier id) {
		return AttachmentRegistryImpl.get(id);
	}

	public static <A> Builder<A> builder() {
		return AttachmentRegistryImpl.builder();
	}

	public interface Builder<A> {
		Builder<A> synced(boolean synced);

		Builder<A> defaultValue(A defaultVal);

		Builder<A> initializer(Supplier<A> supplier);

		Builder<A> serializer(AttachmentSerializer<A> serializer);

		Attachment<A> buildAndRegister(Identifier id);
	}
}
