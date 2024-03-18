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

import java.util.List;

import org.jetbrains.annotations.ApiStatus;

import net.minecraft.predicate.item.ItemPredicate;

@ApiStatus.NonExtendable
public interface FabricItemPredicate {
	default List<CustomItemPredicate> custom() {
		throw new AssertionError("Should be interface injected");
	}

	@ApiStatus.NonExtendable
	interface FabricBuilder {
		default ItemPredicate.Builder custom(CustomItemPredicate... predicate) {
			throw new AssertionError("Should be interface injected");
		}
	}
}
