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

package net.fabricmc.fabric.impl.base.event;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.event.Event;

class EventPhaseData<T> {
	final List<T> listeners = new ArrayList<>();
	final List<Identifier> subsequentPhases = new ArrayList<>();
	final Event.PhaseDependency[] dependencies;

	EventPhaseData(Event.PhaseDependency[] dependencies) {
		this.dependencies = dependencies;
	}
}
