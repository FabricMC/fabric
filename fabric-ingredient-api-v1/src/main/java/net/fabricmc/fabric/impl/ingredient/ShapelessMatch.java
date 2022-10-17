package net.fabricmc.fabric.impl.ingredient;

import java.util.Arrays;
import java.util.BitSet;
import java.util.List;

import org.jetbrains.annotations.ApiStatus;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;

/**
 * Helper class to perform a shapeless recipe match when ingredients that require testing are involved.
 *
 * <p>The problem to solve is a maximum cardinality bipartite matching, for which this implementation uses the augmenting path algorithm.
 * This has good performance in simple cases, and sufficient O(N^3) asymptotic complexity in the worst case.
 */
@ApiStatus.Internal
public class ShapelessMatch {
	private final int[] match;
	/**
	 * The first {@code size} bits are for the visited array (on the left partition).
	 * The remaining {@code size * size} bits are for the adjacency matrix.
	 */
	private final BitSet bitSet;

	private ShapelessMatch(int size) {
		match = new int[size];
		bitSet = new BitSet(size * (size+1));
	}

	private boolean augment(int l) {
		if (bitSet.get(l)) return false;
		bitSet.set(l);

		for (int r = 0; r < match.length; ++r) {
			if (bitSet.get(match.length + l * match.length + r)) {
				if (match[r] == -1 || augment(match[r])) {
					match[r] = l;
					return true;
				}
			}
		}

		return false;
	}

	public static boolean isMatch(List<ItemStack> stacks, List<Ingredient> ingredients) {
		if (stacks.size() != ingredients.size()) {
			return false;
		}

		ShapelessMatch m = new ShapelessMatch(ingredients.size());

		// Build stack -> ingredient bipartite graph
		for (int i = 0; i < stacks.size(); ++i) {
			ItemStack stack = stacks.get(i);

			for (int j = 0; j < ingredients.size(); ++j) {
				if (ingredients.get(j).test(stack)) {
					m.bitSet.set((i + 1) * m.match.length + j);
				}
			}
		}

		// Init matches to -1 (no match)
		Arrays.fill(m.match, -1);

		// Try to find an augmenting path for each stack
		for (int i = 0; i < ingredients.size(); ++i) {
			if (!m.augment(i)) {
				return false;
			}

			m.bitSet.set(0, m.match.length, false);
		}

		return true;
	}
}
