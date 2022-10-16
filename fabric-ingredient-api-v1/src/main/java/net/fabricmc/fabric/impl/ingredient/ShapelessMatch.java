package net.fabricmc.fabric.impl.ingredient;

import java.util.Arrays;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import org.jetbrains.annotations.ApiStatus;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.collection.DefaultedList;

/**
 * Helper class to perform a shapeless recipe match when ingredients that require testing are involved.
 *
 * <p>The problem to solve is a maximum cardinality bipartite matching,
 * for which this implementation uses the augmenting path algorithm.
 */
@ApiStatus.Internal
public class ShapelessMatch {
	private final boolean[] visited;
	private final int[] match;
	private final IntList[] adj;

	private ShapelessMatch(int size) {
		visited = new boolean[size];
		match = new int[size];
		adj = new IntList[size];
	}

	private boolean augment(int l) {
		if (visited[l]) return false;
		visited[l] = true;

		for (int r : adj[l]) {
			if (match[r] == -1 || augment(match[r])) {
				match[r] = l;
				return true;
			}
		}

		return false;
	}

	public static boolean isMatch(CraftingInventory inventory, DefaultedList<Ingredient> ingredients) {
		ShapelessMatch m = new ShapelessMatch(ingredients.size());

		// Build stack -> ingredient bipartite graph
		int stackIndex = 0;

		for (int i = 0; i < inventory.size(); ++i) {
			ItemStack stack = inventory.getStack(i);
			if (stack.isEmpty()) continue;

			m.adj[stackIndex] = new IntArrayList();

			for (int j = 0; j < ingredients.size(); ++j) {
				if (ingredients.get(j).test(stack)) {
					m.adj[stackIndex].add(j);
				}
			}

			++stackIndex;
		}

		// Init matches to -1 (no match)
		Arrays.fill(m.match, -1);

		// Try to find an augmenting path for each stack
		for (int i = 0; i < ingredients.size(); ++i) {
			if (!m.augment(i)) {
				return false;
			}

			Arrays.fill(m.visited, false);
		}

		return true;
	}
}
