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

package net.fabricmc.fabric.impl.attachment;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.attachment.v1.AttachmentSerializer;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;

@ApiStatus.Internal
public class AttachmentTypeImpl<A, T> implements AttachmentType<A, T> {
	private static volatile Map<TypeIdentifier, AttachmentType<?, ?>> attachmentsMap = new HashMap<>();

	public static <A, T> AttachmentType<A, T> create(Identifier identifier, Class<A> attachmentClass, Class<T> targetClass, @Nullable AttachmentSerializer<A, ? super T> serializer) {
		Objects.requireNonNull(identifier, "Identifier may not be null.");
		Objects.requireNonNull(attachmentClass, "Attachment class may not be null.");

		synchronized (AttachmentTypeImpl.class) {
			var typeIdentifier = new TypeIdentifier(identifier, targetClass);

			if (attachmentsMap.containsKey(typeIdentifier)) {
				throw new IllegalArgumentException("Duplicate AttachmentType creation for identifier " + typeIdentifier);
			}

			// Use copy-on-write here to allow for lock-free queries
			Map<TypeIdentifier, AttachmentType<?, ?>> newMap = new HashMap<>(attachmentsMap.size() + 1);
			newMap.putAll(attachmentsMap);
			AttachmentTypeImpl<A, T> attachment = new AttachmentTypeImpl<>(identifier, attachmentClass, targetClass, serializer);
			newMap.put(typeIdentifier, attachment);
			attachmentsMap = newMap;
			return attachment;
		}
	}

	@Nullable
	public static <T> AttachmentTypeImpl<?, T> get(Identifier identifier, Class<T> targetClass) {
		return (AttachmentTypeImpl<?, T>) attachmentsMap.get(new TypeIdentifier(identifier, targetClass));
	}

	private final Identifier identifier;
	private final Class<A> attachmentClass;
	private final Class<T> targetClass;
	@Nullable
	private final AttachmentSerializer<A, ? super T> serializer;

	public AttachmentTypeImpl(Identifier identifier, Class<A> attachmentClass, Class<T> targetClass, @Nullable AttachmentSerializer<A, ? super T> serializer) {
		this.identifier = identifier;
		this.attachmentClass = attachmentClass;
		this.targetClass = targetClass;
		this.serializer = serializer;
	}

	@Override
	public Identifier getIdentifier() {
		return identifier;
	}

	@Override
	public Class<A> getAttachmentClass() {
		return attachmentClass;
	}

	@Override
	public Class<T> getTargetClass() {
		return targetClass;
	}

	@Nullable
	public AttachmentSerializer<A, ? super T> getSerializer() {
		return serializer;
	}

	private record TypeIdentifier(Identifier identifier, Class<?> targetClass) {
	}
}
