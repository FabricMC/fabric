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

import java.util.function.Supplier;

import com.mojang.serialization.Codec;
import org.jetbrains.annotations.Nullable;

import net.minecraft.util.Identifier;

/**
 * An attachment allows "attaching" arbitrary data to various game objects (entities, block entities, worlds and chunks at the moment).
 * Use the methods provided in {@link AttachmentRegistry} to create and register attachments.
 *
 * <p>Attachments can optionally be made to persist between restarts using a provided {@link Codec}s, and
 * can optionally be automatically synced between server and client.</p>
 *
 * @param <A> type of the attached data. It is strongly encouraged for this to be an immutable type.
 */
public interface AttachmentType<A> {
	/**
	 * @return the identifier that uniquely identifies this attachment
	 */
	Identifier identifier();

	/**
	 * If an object has no value associated to an attachment,
	 * this initializer is used to create a (non-{@code null}) starting value.
	 *
	 * <p>The result of the initializer <i>must</i> not have shared state across {@link AttachmentType} instances.
	 * It is strongly encouraged to have {@link A} be an immutable type.</p>
	 *
	 * @return the initializer for this attachment
	 */
	Supplier<A> initializer();

	/**
	 * If present, the codec determines how the attached data, if present, is written to and read from NBT.
	 * If absent, the attached data will not persist after server restarts.
	 *
	 * @return the codec, may be null
	 */
	@Nullable
	Codec<A> codec();

	/**
	 * @return whether the attached data persists across server restarts
	 */
	boolean persistent();

	/**
	 * @return whether the attached data is synced between server and client
	 */
	boolean synced();
}
