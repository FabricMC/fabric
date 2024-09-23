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

package net.fabricmc.fabric.api.components.v1.api;

import java.util.List;
import java.util.function.Consumer;

import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.components.v1.impl.ComponentTypeImpl;

public interface ComponentType<A extends AttachmentTarget, C extends Component<A>> extends AttachmentType<C> {
	static <A extends AttachmentTarget, C extends Component<A>> ComponentType<A, C> create(Identifier identifier) {
		return create(identifier, builder -> { });
	}

	static <A extends AttachmentTarget, C extends Component<A>> ComponentType<A, C> create(Identifier identifier, Consumer<Builder<A, C>> consumer) {
		var builder = new ComponentTypeImpl.BuilderImpl<A, C>();

		consumer.accept(builder);

		return builder.buildAndRegister(identifier);
	}

	<EA extends AttachmentTarget, E> List<Component.EventHandler<?, EA, E>> getEventHandlers(TargetedEvent<EA, E> event);

	interface Builder<A extends AttachmentTarget, C extends Component<A>> extends AttachmentRegistry.Builder<C> {
		<E> Builder<A, C> listen(TargetedEvent<? super A, E> event, Class<A> targetClass, Component.EventHandler<C, A, E> handler);
		<E> Builder<A, C> listen(TargetedEvent<? super A, E> event, Class<A> targetClass, Component.EventListener<C, A> handler);
		<E> Builder<A, C> listen(TargetedEvent<A, E> event, Component.EventHandler<C, A, E> handler);
		<E> Builder<A, C> listen(TargetedEvent<A, E> event, Component.EventListener<C, A> handler);
	}
}
