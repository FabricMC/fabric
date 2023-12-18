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

package net.fabricmc.fabric.impl.resource.loader;

import java.util.Collections;
import java.util.Set;
import java.util.function.Predicate;


/**
 * Interface injected to ResourcePackProfile.
 */
public interface FabricResourcePackProfile {
	default boolean isHidden() {
		return false;
	}

	/**
	 * @return whether every parent is enabled.
	 *
	 * <p>If this is not empty, the pack's status is synced
	 * to that of the parent pack(s), where the pack gets enabled if and only if each
	 * of the parent is enabled.
	 */
	default boolean parentsEnabled(Set<String> enabled) {
		return true;
	}

	default void setParentsPredicate(Predicate<Set<String>> predicate) {
	}
}
