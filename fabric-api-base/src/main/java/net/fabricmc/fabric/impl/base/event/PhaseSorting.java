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
import java.util.Collections;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import com.google.common.annotations.VisibleForTesting;
import org.jetbrains.annotations.ApiStatus;

/**
 * Contains phase-sorting logic for {@link ArrayBackedEvent}.
 */
@ApiStatus.Internal
public class PhaseSorting {
	@VisibleForTesting
	public static boolean ENABLE_CYCLE_WARNING = true;

	/**
	 * Deterministically sort a list of phases.
	 * 1) Compute phase SCCs (i.e. cycles).
	 * 2) Sort phases by id within SCCs.
	 * 3) Sort SCCs with respect to each other by respecting constraints, and by id in case of a tie.
	 */
	static <T> void sortPhases(List<EventPhaseData<T>> sortedPhases) {
		// FIRST KOSARAJU SCC VISIT
		List<EventPhaseData<T>> toposort = new ArrayList<>(sortedPhases.size());

		for (EventPhaseData<T> phase : sortedPhases) {
			forwardVisit(phase, null, toposort);
		}

		clearStatus(toposort);
		Collections.reverse(toposort);

		// SECOND KOSARAJU SCC VISIT
		Map<EventPhaseData<T>, PhaseScc<T>> phaseToScc = new IdentityHashMap<>();

		for (EventPhaseData<T> phase : toposort) {
			if (phase.visitStatus == 0) {
				List<EventPhaseData<T>> sccPhases = new ArrayList<>();
				// Collect phases in SCC.
				backwardVisit(phase, sccPhases);
				// Sort phases by id.
				sccPhases.sort(Comparator.comparing(p -> p.id));
				// Mark phases as belonging to this SCC.
				PhaseScc<T> scc = new PhaseScc<>(sccPhases);

				for (EventPhaseData<T> phaseInScc : sccPhases) {
					phaseToScc.put(phaseInScc, scc);
				}
			}
		}

		clearStatus(toposort);

		// Build SCC graph
		for (PhaseScc<T> scc : phaseToScc.values()) {
			for (EventPhaseData<T> phase : scc.phases) {
				for (EventPhaseData<T> subsequentPhase : phase.subsequentPhases) {
					PhaseScc<T> subsequentScc = phaseToScc.get(subsequentPhase);

					if (subsequentScc != scc) {
						scc.subsequentSccs.add(subsequentScc);
						subsequentScc.inDegree++;
					}
				}
			}
		}

		// Order SCCs according to priorities. When there is a choice, use the SCC with the lowest id.
		// The priority queue contains all SCCs that currently have 0 in-degree.
		PriorityQueue<PhaseScc<T>> pq = new PriorityQueue<>(Comparator.comparing(scc -> scc.phases.get(0).id));
		sortedPhases.clear();

		for (PhaseScc<T> scc : phaseToScc.values()) {
			if (scc.inDegree == 0) {
				pq.add(scc);
				// Prevent adding the same SCC multiple times, as phaseToScc may contain the same value multiple times.
				scc.inDegree = -1;
			}
		}

		while (!pq.isEmpty()) {
			PhaseScc<T> scc = pq.poll();
			sortedPhases.addAll(scc.phases);

			for (PhaseScc<T> subsequentScc : scc.subsequentSccs) {
				subsequentScc.inDegree--;

				if (subsequentScc.inDegree == 0) {
					pq.add(subsequentScc);
				}
			}
		}
	}

	private static <T> void forwardVisit(EventPhaseData<T> phase, EventPhaseData<T> parent, List<EventPhaseData<T>> toposort) {
		if (phase.visitStatus == 0) {
			// Not yet visited.
			phase.visitStatus = 1;

			for (EventPhaseData<T> data : phase.subsequentPhases) {
				forwardVisit(data, phase, toposort);
			}

			toposort.add(phase);
			phase.visitStatus = 2;
		} else if (phase.visitStatus == 1 && ENABLE_CYCLE_WARNING) {
			// Already visiting, so we have found a cycle.
			ArrayBackedEvent.LOGGER.warn(String.format(
					"Event phase ordering conflict detected.%nEvent phase %s is ordered both before and after event phase %s.",
					phase.id,
					parent.id
			));
		}
	}

	private static <T> void clearStatus(List<EventPhaseData<T>> phases) {
		for (EventPhaseData<T> phase : phases) {
			phase.visitStatus = 0;
		}
	}

	private static <T> void backwardVisit(EventPhaseData<T> phase, List<EventPhaseData<T>> sccPhases) {
		if (phase.visitStatus == 0) {
			phase.visitStatus = 1;
			sccPhases.add(phase);

			for (EventPhaseData<T> data : phase.previousPhases) {
				backwardVisit(data, sccPhases);
			}
		}
	}

	private static class PhaseScc<T> {
		final List<EventPhaseData<T>> phases;
		final List<PhaseScc<T>> subsequentSccs = new ArrayList<>();
		int inDegree = 0;

		private PhaseScc(List<EventPhaseData<T>> phases) {
			this.phases = phases;
		}
	}
}
