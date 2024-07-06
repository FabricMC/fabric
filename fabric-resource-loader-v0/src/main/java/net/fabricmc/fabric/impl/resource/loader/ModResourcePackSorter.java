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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.resource.ModResourcePack;
import net.fabricmc.fabric.impl.base.toposort.NodeSorting;
import net.fabricmc.fabric.impl.base.toposort.SortableNode;

public class ModResourcePackSorter {
	public static final Identifier DEFAULT_PHASE = Identifier.of("fabric", "default");
	public static final List<Identifier> LOAD_PHASES = List.of(
			Identifier.of("fabric", "bottom"),
			Identifier.of("fabric", "lower"),
			DEFAULT_PHASE,
			Identifier.of("fabric", "upper"),
			Identifier.of("fabric", "top")
	);
	private final Object lock = new Object();
	private ModResourcePack[] packs;
	/**
	 * Registered load phases.
	 */
	private final Map<Identifier, LoadPhaseData> phases = new LinkedHashMap<>();
	/**
	 * Phases sorted in the correct dependency order.
	 */
	private final List<LoadPhaseData> sortedPhases = new ArrayList<>();

	ModResourcePackSorter() {
		this.packs = new ModResourcePack[0];
	}

	public void appendPacks(List<ModResourcePack> packs) {
		packs.addAll(Arrays.asList(this.packs));
	}

	public void addPack(ModResourcePack pack) {
		addPack(DEFAULT_PHASE, pack);
	}

	public void addPack(Identifier phaseId, ModResourcePack pack) {
		Objects.requireNonNull(phaseId, "Can't register a pack for a null phase id");
		Objects.requireNonNull(pack, "Can't register a null pack");

		if (!LOAD_PHASES.contains(phaseId)) {
			throw new IllegalArgumentException("Unknown phase id: " + phaseId);
		}

		synchronized (lock) {
			getOrCreatePhase(phaseId, true).addPack(pack);
			rebuildPackList(packs.length + 1);
		}
	}

	private LoadPhaseData getOrCreatePhase(Identifier id, boolean sortIfCreate) {
		LoadPhaseData phase = phases.get(id);

		if (phase == null) {
			phase = new LoadPhaseData(id);
			phases.put(id, phase);
			sortedPhases.add(phase);

			if (sortIfCreate) {
				NodeSorting.sort(sortedPhases, "event phases", Comparator.comparing(data -> data.id));
			}
		}

		return phase;
	}

	private void rebuildPackList(int newLength) {
		// Rebuild pack list.
		if (sortedPhases.size() == 1) {
			// Special case with a single phase: use the array of the phase directly.
			packs = sortedPhases.getFirst().packs;
		} else {
			ModResourcePack[] newHandlers = new ModResourcePack[newLength];
			int newHandlersIndex = 0;

			for (LoadPhaseData existingPhase : sortedPhases) {
				int length = existingPhase.packs.length;
				System.arraycopy(existingPhase.packs, 0, newHandlers, newHandlersIndex, length);
				newHandlersIndex += length;
			}

			packs = newHandlers;
		}
	}

	public void addPhaseOrdering(Identifier firstPhase, Identifier secondPhase) {
		Objects.requireNonNull(firstPhase, "Tried to add an ordering for a null phase.");
		Objects.requireNonNull(secondPhase, "Tried to add an ordering for a null phase.");
		if (firstPhase.equals(secondPhase)) throw new IllegalArgumentException("Tried to add a phase that depends on itself.");

		synchronized (lock) {
			LoadPhaseData first = getOrCreatePhase(firstPhase, false);
			LoadPhaseData second = getOrCreatePhase(secondPhase, false);
			LoadPhaseData.link(first, second);
			NodeSorting.sort(this.sortedPhases, "event phases", Comparator.comparing(data -> data.id));
			rebuildPackList(packs.length);
		}
	}

	public static class LoadPhaseData extends SortableNode<LoadPhaseData> {
		final Identifier id;
		ModResourcePack[] packs;

		LoadPhaseData(Identifier id) {
			this.id = id;
			this.packs = new ModResourcePack[0];
		}

		void addPack(ModResourcePack pack) {
			int oldLength = packs.length;
			packs = Arrays.copyOf(packs, oldLength + 1);
			packs[oldLength] = pack;
		}

		@Override
		protected String getDescription() {
			return id.toString();
		}
	}
}
