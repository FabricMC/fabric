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
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import net.fabricmc.fabric.api.components.v1.api.Component;
import net.fabricmc.fabric.api.components.v1.api.ComponentType;
import net.fabricmc.fabric.api.components.v1.api.TargetedEvent;
import net.fabricmc.fabric.api.event.Event;

public class TargetedEventImpl<A extends AttachmentTarget, E> implements TargetedEvent<A, E> {
	private static final Map<Event<?>, TargetedEvent<?, ?>> INSTANCES = new IdentityHashMap<>();

	private final List<ComponentType<? extends A, ?>> componentTypes = new ArrayList<>();

	private TargetedEventImpl() {

	}

	public void addListener(ComponentType<? extends A, ?> componentType) {
		this.componentTypes.add(componentType);
	}

	public static <T extends AttachmentTarget, C> TargetedEventImpl<T, C> create(Event<?> event) {
		//noinspection unchecked
		return (TargetedEventImpl<T, C>) INSTANCES.computeIfAbsent(event, e -> new TargetedEventImpl<>());
	}


	private <CA extends A, C extends Component<CA>> void invokeListenersForComponentType(ComponentType<CA, C> componentType, AttachmentTarget attachmentTarget, E eventPayload) {
		var component = attachmentTarget.getAttached(componentType);

		if (component == null && componentType.initializer() != null) {
			component = attachmentTarget.getAttachedOrCreate(componentType);
		}

		if (component != null) {
			for (var handler : componentType.getEventHandlers(this)) {
				//noinspection unchecked,rawtypes
				((Component.EventHandler) handler).handle(component, attachmentTarget, eventPayload);
			}
		}
	}

	@Override
	public void invoke(A attachmentTarget, E eventContext) {
		for (var componentType : this.componentTypes) {
			this.invokeListenersForComponentType(componentType, attachmentTarget, eventContext);
		}
	}
}
