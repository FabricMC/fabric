package net.fabricmc.fabric.api.client.model.loading.v1;

import org.jetbrains.annotations.ApiStatus;

import net.fabricmc.fabric.impl.client.model.loading.BlockStateResolverRegistry;

import net.minecraft.block.Block;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.util.Identifier;

public interface BlockStateResolver {
	static void register(Block block, BlockStateResolver resolver) {
		BlockStateResolverRegistry.register(block, resolver);
	}

	void resolve(Context context);

	@ApiStatus.NonExtendable
	interface Context {
		void putModel(ModelIdentifier id, UnbakedModel model);

		/**
		 * Load a model using an {@link Identifier}, {@link ModelIdentifier}, ... or get it if it was already loaded.
		 *
		 * <p>Please note that the game engine keeps track of circular model loading calls on its own.
		 *
		 * @param id The model identifier.
		 * @return The UnbakedModel. Can return a missing model if it's not present!
		 */
		UnbakedModel getOrLoadModel(Identifier id);

		/**
		 * The current model loader instance (changes when resource packs reload).
		 *
		 * <p>Do <b>not</b> call {@link ModelLoader#getOrLoadModel} as it doesn't supported nested model resolution,
		 * use {@link #getOrLoadModel} from the context instead.
		 */
		ModelLoader loader();
	}
}
