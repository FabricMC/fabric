package net.fabricmc.fabric.api.client.model.loading.v1;

import org.jetbrains.annotations.ApiStatus;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.util.Identifier;

/**
 * Interface for block state resolvers.
 *
 * <p>Block state resolvers are responsible for mapping each {@link BlockState} of a block to an {@link UnbakedModel}.
 * Block state resolvers replace the {@code blockstates/} JSON files.
 * They allow defining custom block state formats for example.
 *
 * <p>If only custom models are needed, {@link ModelResolver} should be used to resolve specific model files.
 */
// TODO: add test
// TODO: better javadoc
@FunctionalInterface
public interface BlockStateResolver {
	/**
	 * Resolve the models for all block states of the block.
	 *
	 * <p>For each block state, call {@link Context#setModel} to set its unbaked model.
	 * It must be called exactly once for each block state.
	 *
	 * <p>Note that the unbaked model for each block state will be baked.
	 * If many block states share the same model, this can be quite wasteful (as each model will get baked multiple times).
	 * In that case, it's better to use the delegating unbaked model TODO add delegating unbaked model and expand docs
	 * TODO: javadoc
	 */
	void resolveBlockStates(Context context);

	/**
	 * The context for block state resolution.
	 */
	@ApiStatus.NonExtendable
	interface Context {
		/**
		 * The block for which the block state models are being resolved.
		 */
		Block block();

		/**
		 * Set the model for a block state.
		 *
		 * @param state the block state for which this model should be used
		 * @param model the unbaked model for this block state
		 */
		void setModel(BlockState state, UnbakedModel model);

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
