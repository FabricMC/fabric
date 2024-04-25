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

package net.fabricmc.fabric.impl.item;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentType;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;

import net.fabricmc.fabric.api.item.v1.DefaultItemComponentEvents;
import net.fabricmc.fabric.mixin.item.ItemAccessor;

public class DefaultItemComponentImpl {
	public static void modifyItemComponents() {
		DefaultItemComponentEvents.MODIFY.invoker().modify(ModifyContextImpl.INSTANCE);
		DefaultItemComponentEvents.AFTER_MODIFY.invoker().afterModify(AfterModifyContextImpl.INSTANCE);
	}

	private static void modifyItem(Item item, Consumer<ComponentMap.Builder> builderConsumer) {
		ComponentMap.Builder builder = ComponentMap.builder().addAll(item.getComponents());
		builderConsumer.accept(builder);
		((ItemAccessor) item).setComponents(builder.build());
	}

	static class ModifyContextImpl implements DefaultItemComponentEvents.ModifyContext {
		private static final ModifyContextImpl INSTANCE = new ModifyContextImpl();

		private ModifyContextImpl() {
		}

		@Override
		public void modify(Item item, Consumer<ComponentMap.Builder> builderConsumer) {
			modifyItem(item, builderConsumer);
		}
	}

	static class AfterModifyContextImpl implements DefaultItemComponentEvents.AfterModifyContext {
		private static final AfterModifyContextImpl INSTANCE = new AfterModifyContextImpl();

		private AfterModifyContextImpl() {
		}

		@Override
		public <T> void modify(DataComponentType<T> type, BiConsumer<T, ComponentMap.Builder> builderConsumer) {
			for (Item item : Registries.ITEM) {
				if (item.getComponents().contains(type)) {
					modifyItem(item, builder -> builderConsumer.accept(item.getComponents().get(type), builder));
				}
			}
		}
	}
}
