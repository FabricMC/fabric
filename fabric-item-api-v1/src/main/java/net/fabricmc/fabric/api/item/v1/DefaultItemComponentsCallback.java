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

package net.fabricmc.fabric.api.item.v1;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentType;
import net.minecraft.item.Item;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/**
 * Events to modify the default {@link ComponentMap} of items.
 */
public interface DefaultItemComponentsCallback {
	/**
	 * Event used to add new data components to known items.
	 */
	Event<ModifyCallback> MODIFY = EventFactory.createArrayBacked(ModifyCallback.class, listeners -> context -> {
		for (ModifyCallback listener : listeners) {
			listener.modify(context);
		}
	});

	/**
	 * Event used to modify the default data components of items after they have been modified by other mods during {@link #MODIFY}.
	 */
	Event<AfterModifyCallback> AFTER_MODIFY = EventFactory.createArrayBacked(AfterModifyCallback.class, listeners -> context -> {
		for (AfterModifyCallback listener : listeners) {
			listener.afterModify(context);
		}
	});

	interface ModifyContext {
		/**
		 * Modify the default data components of the specified item.
		 *
		 * @param item The item to modify
		 * @param builderConsumer A consumer that provides a {@link ComponentMap.Builder} to modify the item's components.
		 */
		void modify(Item item, Consumer<ComponentMap.Builder> builderConsumer);
	}

	interface AfterModifyContext {
		/**
		 * Modify the default data components of any item with the specified {@link DataComponentType}.
		 *
		 * @param type The type of the data component to find on items
		 * @param builderConsumer A consumer that provides a {@link ComponentMap.Builder} to modify the item's components.
		 * @param <T> The value type of the data component
		 */
		<T> void modify(DataComponentType<T> type, BiConsumer<T, ComponentMap.Builder> builderConsumer);
	}

	@FunctionalInterface
	interface ModifyCallback {
		/**
		 * Modify the default data components of items using the provided {@link ModifyContext} instance.
		 *
		 * @param context The context to modify items
		 */
		void modify(ModifyContext context);
	}

	@FunctionalInterface
	interface AfterModifyCallback {
		/**
		 * Modify the default data components of items using the provided {@link AfterModifyContext} instance.
		 *
		 * @param context The context to modify items
		 */
		void afterModify(AfterModifyContext context);
	}
}
