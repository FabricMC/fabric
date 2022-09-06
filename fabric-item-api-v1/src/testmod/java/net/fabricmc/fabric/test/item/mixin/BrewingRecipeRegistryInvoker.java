package net.fabricmc.fabric.test.item.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import net.minecraft.recipe.BrewingRecipeRegistry;

@Mixin(BrewingRecipeRegistry.class)
public interface BrewingRecipeRegistryInvoker {
	@Invoker("registerPotionRecipe")
	static void invokeRegisterPotionType(Potion input, Item item, Potion output) {
		throw new AssertionError();
	}
}
