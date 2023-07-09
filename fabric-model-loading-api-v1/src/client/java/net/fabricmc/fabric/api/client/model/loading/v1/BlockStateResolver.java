package net.fabricmc.fabric.api.client.model.loading.v1;

import org.jetbrains.annotations.ApiStatus;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.block.BlockModels;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.util.Identifier;

/**
 * Interface for block state resolvers.
 *
 * <p>Block state resolvers replace the {@code blockstates/} JSON files.
 * They allow defining custom block-state formats for example.
 *
 * <p>If only custom models are needed, {@link ModelResolver} should be used to resolve specific model files.
 */
// TODO: add test
@FunctionalInterface
public interface BlockStateResolver {
	/**
	 * TODO: javadoc
	 */
	void resolveBlockStates(Context context);

	/**
	 * The context for block state resolution.
	 */
	@ApiStatus.NonExtendable
	interface Context {
		/**
		 * Add a model for a specific block state.
		 *
		 * <p>The {@link ModelIdentifier} for a specific block state can be obtained using {@link BlockModels#getModelId(BlockState)}.
		 *
		 * @param id the model identifier, corresponding to a block state
		 * @param model the unbaked model for this identifie
		 */
		// TODO: Change to blockstate first param
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
