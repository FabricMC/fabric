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

package net.fabricmc.fabric.api.components.v1.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import com.mojang.serialization.Codec;
import org.jetbrains.annotations.Nullable;

import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.components.v1.api.Component;
import net.fabricmc.fabric.api.components.v1.api.ComponentType;
import net.fabricmc.fabric.api.components.v1.api.TargetedEvent;

public final class ComponentTypeImpl<A extends AttachmentTarget, C extends Component<A>> implements ComponentType<A, C>  {
	private final AttachmentType<C> attachmentType;
	private final Map<TargetedEvent<? super A, ?>, List<Component.EventHandler<?, ? super A, ?>>> eventHandlers = new IdentityHashMap<>();

	public ComponentTypeImpl(AttachmentType<C> attachmentType) {
		this.attachmentType = attachmentType;
	}

	@Override
	public <EA extends AttachmentTarget, E> List<Component.EventHandler<?, EA, E>> getEventHandlers(TargetedEvent<EA, E> event) {
		//noinspection unchecked
		return (List<Component.EventHandler<?, EA, E>>) (Object) this.eventHandlers.getOrDefault(event, Collections.emptyList());
	}

	@Override
	public Identifier identifier() {
		return this.attachmentType.identifier();
	}

	@Override
	public @Nullable Codec<C> persistenceCodec() {
		return this.attachmentType.persistenceCodec();
	}

	@Override
	public boolean isPersistent() {
		return this.attachmentType.isPersistent();
	}

	@Override
	public @Nullable Supplier<C> initializer() {
		return this.attachmentType.initializer();
	}

	@Override
	public boolean copyOnDeath() {
		return this.attachmentType.copyOnDeath();
	}

	public static final class BuilderImpl<A extends AttachmentTarget, C extends Component<A>> implements Builder<A, C>, AttachmentRegistry.Builder<C> {
		private final AttachmentRegistry.Builder<C> attachmentBuilder = AttachmentRegistry.builder();
		private final Map<TargetedEvent<? super A, ?>, List<Component.EventHandler<?, ? super A, ?>>> eventHandlers = new IdentityHashMap<>();

		@Override
		public <E> Builder<A, C> listen(TargetedEvent<? super A, E> event, Class<A> targetClass, Component.EventHandler<C, A, E> handler) {
			List<Component.EventHandler<?, ? super A, ?>> handlers = this.eventHandlers.computeIfAbsent(event, e -> new ArrayList<>());

			handlers.add(new Component.EventHandler<C, AttachmentTarget, E>() {
				@Override
				public void handle(C component, AttachmentTarget target, E eventPayload) {
					if (targetClass.isAssignableFrom(target.getClass())) {
						//noinspection unchecked
						handler.handle(component, (A) target, eventPayload);
					}
				}
			});

			return this;
		}

		@Override
		public <E> Builder<A, C> listen(TargetedEvent<? super A, E> event, Class<A> targetClass, Component.EventListener<C, A> handler) {
			return this.listen(event, targetClass, (component, attachmentTarget, eventPayload) ->
					handler.handle(component, attachmentTarget)
			);
		}

		@Override
		public <E> Builder<A, C> listen(TargetedEvent<A, E> event, Component.EventHandler<C, A, E> handler) {
			this.eventHandlers.computeIfAbsent(event, e -> new ArrayList<>()).add(handler);

			return this;
		}

		@Override
		public <E> Builder<A, C> listen(TargetedEvent<A, E> event, Component.EventListener<C, A> handler) {
			return this.listen(event, (component, attachmentTarget, eventPayload) ->
					handler.handle(component, attachmentTarget)
			);
		}

		public ComponentTypeImpl<A, C> buildAndRegister(Identifier id) {
			var attachmentType = this.attachmentBuilder.buildAndRegister(id);
			var type = new ComponentTypeImpl<>(attachmentType);

			this.eventHandlers.forEach((event, list) -> {
				type.eventHandlers.put(event, List.copyOf(list));
				((TargetedEventImpl<? super A, ?>) event).addListener(type);
			});

			return type;
		}

		@Override
		public AttachmentRegistry.Builder<C> persistent(Codec<C> codec) {
			return attachmentBuilder.persistent(codec);
		}

		@Override
		public AttachmentRegistry.Builder<C> copyOnDeath() {
			return attachmentBuilder.copyOnDeath();
		}

		@Override
		public AttachmentRegistry.Builder<C> initializer(Supplier<C> initializer) {
			return attachmentBuilder.initializer(initializer);
		}
	}
}
