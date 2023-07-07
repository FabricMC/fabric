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

package net.fabricmc.fabric.impl.base.toposort;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import com.google.common.annotations.VisibleForTesting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Contains a topological sort implementation, with tie breaking using a {@link Comparator}.
 *
 * <p>The final order is always deterministic (i.e. doesn't change with the order of the input elements or the edges),
 * assuming that they are all different according to the comparator. This also holds in the presence of cycles.
 *
 * <p>The steps are as follows:
 * <ol>
 *     <li>Compute node SCCs (Strongly Connected Components, i.e. cycles).</li>
 *     <li>Sort nodes within SCCs using the comparator.</li>
 *     <li>Sort SCCs with respect to each other by respecting constraints, and using the comparator in case of a tie.</li>
 * </ol>
 */
public class NodeSorting {
	private static final Logger LOGGER = LoggerFactory.getLogger("fabric-api-base");

	@VisibleForTesting
	public static boolean ENABLE_CYCLE_WARNING = true;

	/**
	 * Sort a list of nodes.
	 *
	 * @param sortedPhases The list of nodes to sort. Will be modified in-place.
	 * @param elementDescription A description of the elements, used for logging in the presence of cycles.
	 * @param comparator The comparator to break ties and to order elements within a cycle.
	 */
	public static <N extends SortableNode<N>> void sort(List<N> sortedPhases, String elementDescription, Comparator<N> comparator) {
		// FIRST KOSARAJU SCC VISIT
		List<N> toposort = new ArrayList<>(sortedPhases.size());

		for (N phase : sortedPhases) {
			forwardVisit(phase, null, toposort);
		}

		clearStatus(toposort);
		Collections.reverse(toposort);

		// SECOND KOSARAJU SCC VISIT
		Map<N, PhaseScc<N>> phaseToScc = new IdentityHashMap<>();

		for (N phase : toposort) {
			if (!phase.visited) {
				List<N> sccPhases = new ArrayList<>();
				// Collect phases in SCC.
				backwardVisit(phase, sccPhases);
				// Sort phases by id.
				sccPhases.sort(comparator);
				// Mark phases as belonging to this SCC.
				PhaseScc<N> scc = new PhaseScc<>(sccPhases);

				for (N phaseInScc : sccPhases) {
					phaseToScc.put(phaseInScc, scc);
				}
			}
		}

		clearStatus(toposort);

		// Build SCC graph
		for (PhaseScc<N> scc : phaseToScc.values()) {
			for (N phase : scc.phases) {
				for (N subsequentPhase : phase.subsequentNodes) {
					PhaseScc<N> subsequentScc = phaseToScc.get(subsequentPhase);

					if (subsequentScc != scc) {
						scc.subsequentSccs.add(subsequentScc);
						subsequentScc.inDegree++;
					}
				}
			}
		}

		// Order SCCs according to priorities. When there is a choice, use the SCC with the lowest id.
		// The priority queue contains all SCCs that currently have 0 in-degree.
		PriorityQueue<PhaseScc<N>> pq = new PriorityQueue<>(Comparator.comparing(scc -> scc.phases.get(0), comparator));
		sortedPhases.clear();

		for (PhaseScc<N> scc : phaseToScc.values()) {
			if (scc.inDegree == 0) {
				pq.add(scc);
				// Prevent adding the same SCC multiple times, as phaseToScc may contain the same value multiple times.
				scc.inDegree = -1;
			}
		}

		while (!pq.isEmpty()) {
			PhaseScc<N> scc = pq.poll();
			sortedPhases.addAll(scc.phases);

			// Print cycle warning
			if (ENABLE_CYCLE_WARNING && scc.phases.size() > 1) {
				StringBuilder builder = new StringBuilder();
				builder.append("Found cycle while sorting ").append(elementDescription).append(":\n");

				for (N phase : scc.phases) {
					builder.append("\t").append(phase.getDescription()).append("\n");
				}

				LOGGER.warn(builder.toString());
			}

			for (PhaseScc<N> subsequentScc : scc.subsequentSccs) {
				subsequentScc.inDegree--;

				if (subsequentScc.inDegree == 0) {
					pq.add(subsequentScc);
				}
			}
		}
	}

	private static <N extends SortableNode<N>> void forwardVisit(N phase, N parent, List<N> toposort) {
		if (!phase.visited) {
			// Not yet visited.
			phase.visited = true;

			for (N data : phase.subsequentNodes) {
				forwardVisit(data, phase, toposort);
			}

			toposort.add(phase);
		}
	}

	private static <N extends SortableNode<N>> void clearStatus(List<N> phases) {
		for (N phase : phases) {
			phase.visited = false;
		}
	}

	private static <N extends SortableNode<N>> void backwardVisit(N phase, List<N> sccPhases) {
		if (!phase.visited) {
			phase.visited = true;
			sccPhases.add(phase);

			for (N data : phase.previousNodes) {
				backwardVisit(data, sccPhases);
			}
		}
	}

	private static class PhaseScc<N extends SortableNode<N>> {
		final List<N> phases;
		final List<PhaseScc<N>> subsequentSccs = new ArrayList<>();
		int inDegree = 0;

		private PhaseScc(List<N> phases) {
			this.phases = phases;
		}
	}
}
