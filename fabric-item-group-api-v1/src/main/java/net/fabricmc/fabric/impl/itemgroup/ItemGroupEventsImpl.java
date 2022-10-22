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

package net.fabricmc.fabric.impl.itemgroup;

import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;

@ApiStatus.Internal
public class ItemGroupEventsImpl {
	private static final Map<Identifier, Event<ItemGroupEvents.ModifyContent>> IDENTIFIER_EVENT_MAP = new HashMap<>();

	public static Event<ItemGroupEvents.ModifyContent> getOrCreateModifyEntriesEvent(Identifier identifier) {
		return IDENTIFIER_EVENT_MAP.computeIfAbsent(identifier, (g -> createModifyEvent()));
	}

	@Nullable
	public static Event<ItemGroupEvents.ModifyContent> getModifyEntriesEvent(Identifier identifier) {
		return IDENTIFIER_EVENT_MAP.get(identifier);
	}

	private static Event<ItemGroupEvents.ModifyContent> createModifyEvent() {
		return EventFactory.createArrayBacked(ItemGroupEvents.ModifyContent.class, callbacks -> (featureSet, entries) -> {
			for (ItemGroupEvents.ModifyContent callback : callbacks) {
				callback.modifyContent(featureSet, entries);
			}
		});
	}
}
