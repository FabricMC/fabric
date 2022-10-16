package net.fabricmc.fabric.api.ingredient.v1;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;

import net.fabricmc.fabric.impl.ingredient.CustomIngredientImpl;

/**
 * Interface that modders can implement to create new behaviors for {@link Ingredient}s.
 *
 * <p>This is not directly implemented on vanilla {@link Ingredient}s, but conversions are possible:
 * <ul>
 *     <li>{@link #toVanilla()} converts a custom ingredient to a vanilla {@link Ingredient}.</li>
 *     <li>{@link FabricIngredient} can be used to check if a vanilla {@link Ingredient} is custom,
 *     and retrieve the custom ingredient in that case.</li>
 * </ul>
 */
public interface CustomIngredient {
	/**
	 * Check if a stack matches this ingredient.
	 *
	 * @param stack the stack to test
	 * @return true if the stack matches this ingredient, false otherwise
	 */
	boolean test(ItemStack stack);

	/**
	 * {@return the list of stacks that match this ingredient.}
	 *
	 * <p>The following guidelines should be followed for good compatibility:
	 * <ul>
	 *     <li>These stacks are generally used for display purposes, and need not be exhaustive or perfectly accurate.</li>
	 *     <li>An exception is ingredients that {@linkplain #requiresTesting() don't require testing},
	 *     for which it is important that the returned stacks correspond exactly to all the accepted {@link Item}s.</li>
	 *     <li>At least one stack must be returned for the ingredient not to be considered {@linkplain Ingredient#isEmpty() empty}.</li>
	 *     <li>The ingredient should try to return at least one stack with each accepted {@link Item}.
	 *     This allows mods that inspect the ingredient to figure out which stacks it might accept.</li>
	 * </ul>
	 *
	 * <p>Note: no caching needs to be done by the implementation, this is already handled by the ingredient itself.
	 */
	ItemStack[] getMatchingStacks();

	/**
	 * Return whether this ingredient always requires {@linkplain #test direct stack testing}.
	 *
	 * <p>If false, {@linkplain #test testing this ingredient} with an item stack must be equivalent to checking whether
	 * the item stack's item is contained in the ingredient's {@linkplain #getMatchingStacks() list of matching stacks}.
	 * In that case, optimized matching logic can be used, for example using {@link Ingredient#getMatchingItemIds()}.
	 *
	 * <p>If true, the ingredient must always be tested using {@link #test(ItemStack)}.
	 * Note: Fabric patches some vanilla systems such as shapeless recipes to account for this.
	 *
	 * @return true if this ingredient ignores NBT data when matching stacks, false otherwise
	 */
	boolean requiresTesting();

	/**
	 * {@return The serializer for this ingredient.}
	 *
	 * <p>The serializer must have be registered using {@link CustomIngredientSerializer#register}.
	 */
	CustomIngredientSerializer<?> getSerializer();

	/**
	 * {@return A new {@link Ingredient} behaving as defined by this custom ingredient.}
	 */
	default Ingredient toVanilla() {
		return new CustomIngredientImpl(this);
	}

}
