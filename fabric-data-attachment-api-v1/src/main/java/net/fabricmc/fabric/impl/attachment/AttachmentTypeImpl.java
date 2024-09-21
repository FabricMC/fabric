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

import java.util.Objects;
import java.util.function.Supplier;

import com.mojang.serialization.Codec;
import org.jetbrains.annotations.Nullable;

import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.attachment.v1.AttachmentType;

public sealed class AttachmentTypeImpl<A> implements AttachmentType<A> permits AttachmentTypeImpl.Lazy {
	private final @Nullable Supplier<A> initializer;
	private final @Nullable Codec<A> persistenceCodec;
	private final boolean copyOnDeath;

	protected Identifier identifier;

	public AttachmentTypeImpl(
			Identifier identifier,
			@Nullable Supplier<A> initializer,
			@Nullable Codec<A> persistenceCodec,
			boolean copyOnDeath
	) {
		this.identifier = identifier;
		this.initializer = initializer;
		this.persistenceCodec = persistenceCodec;
		this.copyOnDeath = copyOnDeath;
	}

	@Override
	public Identifier identifier() {
		if (this.identifier == null) {
			throw new RuntimeException("Attempted to get attachment type identifier before it has been initialized.");
		}

		return identifier;
	}

	@Override
	public @Nullable Supplier<A> initializer() {
		return initializer;
	}

	@Override
	public @Nullable Codec<A> persistenceCodec() {
		return persistenceCodec;
	}

	@Override
	public boolean copyOnDeath() {
		return copyOnDeath;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj == null || obj.getClass() != this.getClass()) return false;
		var that = (AttachmentTypeImpl) obj;
		return Objects.equals(this.identifier, that.identifier) &&
				Objects.equals(this.initializer, that.initializer) &&
				Objects.equals(this.persistenceCodec, that.persistenceCodec) &&
				this.copyOnDeath == that.copyOnDeath;
	}

	@Override
	public int hashCode() {
		return Objects.hash(identifier, initializer, persistenceCodec, copyOnDeath);
	}

	@Override
	public String toString() {
		return "AttachmentTypeImpl[" +
				"identifier=" + identifier + ", " +
				"initializer=" + initializer + ", " +
				"persistenceCodec=" + persistenceCodec + ", " +
				"copyOnDeath=" + copyOnDeath + ']';
	}

	public static final class Lazy<A> extends AttachmentTypeImpl<A> {
		private final String path;

		public Lazy(String path, @Nullable Supplier<A> initializer, @Nullable Codec<A> persistenceCodec, boolean copyOnDeath) {
			super(null, initializer, persistenceCodec, copyOnDeath);

			this.path = path;
		}

		public void initialize(String namespace) {
			this.identifier = Identifier.of(namespace, this.path);
		}
	}
}
