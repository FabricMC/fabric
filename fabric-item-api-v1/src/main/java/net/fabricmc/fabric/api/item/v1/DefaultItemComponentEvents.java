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

import java.util.function.Consumer;
import java.util.function.Predicate;

import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentType;
import net.minecraft.item.Item;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/**
 * Events to modify the default {@link ComponentMap} of items.
 */
public final class DefaultItemComponentEvents {
	/**
	 * Event used to add or remove data components to known items.
	 */
	public static final Event<ModifyCallback> MODIFY = EventFactory.createArrayBacked(ModifyCallback.class, listeners -> context -> {
		for (ModifyCallback listener : listeners) {
			listener.modify(context);
		}
	});

	/**
	 * Event used to modify the default data components of items after they have been modified by other mods during {@link #MODIFY}.
	 */
	public static final Event<AfterModifyCallback> AFTER_MODIFY = EventFactory.createArrayBacked(AfterModifyCallback.class, listeners -> context -> {
		for (AfterModifyCallback listener : listeners) {
			listener.afterModify(context);
		}
	});

	private DefaultItemComponentEvents() {
	}

	public interface ModifyContext {
		/**
		 * Modify the default data components of the specified item.
		 *
		 * @param itemPredicate A predicate to match items to modify
		 * @param builderConsumer A consumer that provides a {@link ComponentMap.Builder} to modify the item's components.
		 */
		void modify(Predicate<Item> itemPredicate, Consumer<ComponentMap.Builder> builderConsumer);

		/**
		 * Modify the default data components of the specified item.
		 *
		 * @param item The item to modify
		 * @param builderConsumer A consumer that provides a {@link ComponentMap.Builder} to modify the item's components.
		 */
		default void modify(Item item, Consumer<ComponentMap.Builder> builderConsumer) {
			modify(Predicate.isEqual(item), builderConsumer);
		}
	}

	public interface AfterModifyContext {
		/**
		 * Modify the default data components of any item with the specified {@link DataComponentType}.
		 *
		 * @param itemPredicate A predicate to match items to modify
		 * @param builderConsumer A consumer that provides a {@link ComponentMap.Builder} to modify the item's components.
		 * @param <T> The value type of the data component
		 */
		<T> void modify(Predicate<Item> itemPredicate, Consumer<ComponentMap.Builder> builderConsumer);

		/**
		 * Modify the default data components of any item with the specified {@link DataComponentType}.
		 *
		 * @param type The type of the data component to find on items
		 * @param builderConsumer A consumer that provides a {@link ComponentMap.Builder} to modify the item's components.
		 * @param <T> The value type of the data component
		 */
		default <T> void modify(DataComponentType<T> type, Consumer<ComponentMap.Builder> builderConsumer) {
			modify((item) -> item.getComponents().contains(type), builderConsumer);
		}
	}

	@FunctionalInterface
	public interface ModifyCallback {
		/**
		 * Modify the default data components of items using the provided {@link ModifyContext} instance.
		 *
		 * @param context The context to modify items
		 */
		void modify(ModifyContext context);
	}

	@FunctionalInterface
	public interface AfterModifyCallback {
		/**
		 * Modify the default data components of items using the provided {@link AfterModifyContext} instance.
		 *
		 * @param context The context to modify items
		 */
		void afterModify(AfterModifyContext context);
	}
}
