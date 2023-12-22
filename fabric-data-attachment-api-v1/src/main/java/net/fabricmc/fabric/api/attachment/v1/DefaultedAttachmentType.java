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

import org.jetbrains.annotations.ApiStatus;

/**
 * An {@link AttachmentType} that will automatically initialize attachments with a default value when queried for the first time.
 *
 * <p>Make sure to store such attachment types in fields of type {@link DefaultedAttachmentType} to avoid mistakenly
 * calling {@link AttachmentTarget#getAttached(AttachmentType)} instead.</p>
 *
 * @param <A>
 * @see AttachmentTarget#getAttached(DefaultedAttachmentType)
 */
@ApiStatus.NonExtendable
public interface DefaultedAttachmentType<A> extends AttachmentType<A> {
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
	Supplier<A> initializer();
}
