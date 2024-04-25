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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

import net.minecraft.component.ComponentMap;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;

import net.fabricmc.fabric.api.item.v1.DefaultItemComponentEvents;
import net.fabricmc.fabric.mixin.item.ItemAccessor;

public class DefaultItemComponentImpl {
	public static void modifyItemComponents() {
		var modifyContext = new ModifyContextImpl();
		var afterModifyContext = new AfterModifyContextImpl();

		DefaultItemComponentEvents.MODIFY.invoker().modify(modifyContext);
		DefaultItemComponentEvents.AFTER_MODIFY.invoker().afterModify(afterModifyContext);

		for (Item item : Registries.ITEM) {
			apply(item, modifyContext.modifications);
			apply(item, afterModifyContext.modifications);
		}
	}

	private static void apply(Item item, List<Modification> modifications) {
		List<Modification> modificationsToApply = new ArrayList<>();

		for (Modification modification : modifications) {
			if (modification.predicate().test(item)) {
				modificationsToApply.add(modification);
			}
		}

		if (modificationsToApply.isEmpty()) {
			return;
		}

		ComponentMap.Builder builder = ComponentMap.builder().addAll(item.getComponents());

		for (Modification modification : modificationsToApply) {
			modification.builderConsumer().accept(builder);
		}

		((ItemAccessor) item).setComponents(builder.build());
	}

	private record Modification(Predicate<Item> predicate, Consumer<ComponentMap.Builder> builderConsumer) {
	}

	static class ModifyContextImpl implements DefaultItemComponentEvents.ModifyContext {
		private final List<Modification> modifications = new ArrayList<>();

		@Override
		public void modify(Predicate<Item> itemPredicate, Consumer<ComponentMap.Builder> builderConsumer) {
			modifications.add(new Modification(itemPredicate, builderConsumer));
		}
	}

	static class AfterModifyContextImpl implements DefaultItemComponentEvents.AfterModifyContext {
		private final List<Modification> modifications = new ArrayList<>();

		@Override
		public <T> void modify(Predicate<Item> itemPredicate, Consumer<ComponentMap.Builder> builderConsumer) {
			modifications.add(new Modification(itemPredicate, builderConsumer));
		}
	}
}
