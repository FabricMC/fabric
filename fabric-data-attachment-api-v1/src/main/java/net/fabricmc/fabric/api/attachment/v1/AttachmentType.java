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
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import net.minecraft.util.Identifier;

/**
 * An attachment allows "attaching" arbitrary data to various game objects (entities, block entities, worlds and chunks at the moment).
 * Use the methods provided in {@link AttachmentRegistry} to create and register attachments.
 *
 * <p>Attachments can optionally be made to persist between restarts using a provided {@link Codec}.</p>
 *
 * @param <A> type of the attached data. It is encouraged for this to be an immutable type.
 */
@ApiStatus.NonExtendable
@ApiStatus.Experimental
public interface AttachmentType<A> {
	/**
	 * @return the identifier that uniquely identifies this attachment
	 */
	Identifier identifier();

	/**
	 * An optional {@link Codec} used for reading and writing attachments. Currently, only used for persistence.
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
	 * If an object has no value associated to an attachment,
	 * this initializer is used to create a non-{@code null} starting value.
	 *
	 * <p>It is <i>encouraged</i> for {@link A} to be an immutable data type, such as a primitive type
	 * or an immutable record.</p>
	 *
	 * <p>Otherwise, one must be very careful, as attachments <i>must not share any mutable state</i>.
	 * As an example, for a (mutable) list/array attachment type,
	 * the initializer should create a new independent instance each time it is called.</p>
	 *
	 * @return the initializer for this attachment
	 */
	@Nullable
	Supplier<A> initializer();
}
