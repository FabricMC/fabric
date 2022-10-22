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

package net.fabricmc.fabric.api.itemgroup.v1;

import net.minecraft.item.ItemGroup;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.impl.itemgroup.ItemGroupEventsImpl;

public class ItemGroupEvents {
	/**
	 * This event allows the content of any item group to be modified.
	 * <p/>
	 * If you know beforehand which item group you'd like to modify, use {@link #modifyContent(ItemGroup)}
	 * or {@link #modifyContent(Identifier)} instead.
	 * <p/>
	 * This event is invoked after those two more specific events.
	 */
	public static final Event<ModifyContentAll> MODIFY_CONTENT_ALL = EventFactory.createArrayBacked(ModifyContentAll.class, callbacks -> (group, featureSet, content) -> {
		for (ModifyContentAll callback : callbacks) {
			callback.modifyContent(group, featureSet, content);
		}
	});

	public static Event<ModifyContent> modifyContent(ItemGroup itemGroup) {
		return modifyContent(itemGroup.getId());
	}

	public static Event<ModifyContent> modifyContent(Identifier identifier) {
		return ItemGroupEventsImpl.getOrCreateModifyContentEvent(identifier);
	}

	@FunctionalInterface
	public interface ModifyContent {
		void modifyContent(FeatureSet featureSet, ItemGroupContent content);
	}

	@FunctionalInterface
	public interface ModifyContentAll {
		void modifyContent(ItemGroup group, FeatureSet featureSet, ItemGroupContent content);
	}
}
