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

package net.fabricmc.fabric.api.client.model.loading.v1;

import java.util.function.Function;

import org.jetbrains.annotations.ApiStatus;

import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.Baker;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.event.Event;

/**
 * Contains interfaces for the events mods can use to modify models.
 *
 * <p>Example use cases:
 * <ul>
 *     <li>Overriding a model for a particular blockstate - check if the given identifier is a {@link ModelIdentifier},
 *     and then check if it has the appropriate variant for that blockstate. If so, return your desired model,
 *     otherwise return the given model.</li>
 *     <li>Wrapping a model to override certain behaviors - simply return a new model instance and delegate calls
 *     to the original model as needed.</li>
 * </ul>
 *
 * <p>Phases are used to ensure that modifications occur in a reasonable order, e.g. wrapping occurs after overrides,
 * and separate phases are provided for mods that wrap their own models and mods that need to wrap all models.
 *
 * <p>These hooks fire for <b>every single model that's loaded</b>, so the code written here should be as simple/performant
 * as possible.
 */
public final class ModelModifier {
	/**
	 * Recommended phase to use when overriding models, e.g. replacing a model with another model.
	 */
	public static final Identifier OVERRIDE_PHASE = new Identifier("fabric", "override");
	/**
	 * Recommended phase to use for transformations that need to happen before wrapping, but after model overrides.
	 */
	public static final Identifier DEFAULT_PHASE = Event.DEFAULT_PHASE;
	/**
	 * Recommended phase to use when wrapping models.
	 */
	public static final Identifier WRAP_PHASE = new Identifier("fabric", "wrap");
	/**
	 * Recommended phase to use when wrapping models with transformations that want to happen last,
	 * e.g. for connected textures or other similar visual effects that should be the final processing step.
	 */
	public static final Identifier WRAP_LAST_PHASE = new Identifier("fabric", "wrap_last");

	@FunctionalInterface
	public interface OnLoad {
		/**
		 * This handler is invoked to allow modifying the unbaked model instance that is stored when it is first loaded.
		 *
		 * @param model the current unbaked model instance
		 * @param context context with additional information about the model/loader
		 * @return the model that should be used in this scenario. If no changes are needed, just return {@code model} as-is.
		 * @see ModelLoadingPlugin.Context#modifyModelOnLoad
		 */
		UnbakedModel modifyModelOnLoad(UnbakedModel model, Context context);

		/**
		 * Context for an on load model modification event.
		 */
		@ApiStatus.NonExtendable
		interface Context {
			/**
			 * The identifier of this model (may be a {@link ModelIdentifier}).
			 */
			Identifier id();

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
			 *
			 * @apiNote Calling {@link ModelLoader#getOrLoadModel} is fine for {@link BeforeBake} and {@link AfterBake},
			 * it should only be avoided in {@link OnLoad} listeners.
			 */
			ModelLoader loader();
		}
	}

	@FunctionalInterface
	public interface BeforeBake {
		/**
		 * This handler is invoked to allow modifying the unbaked model instance that is baked.
		 *
		 * @param model the current unbaked model instance
		 * @param context context with additional information about the model/loader
		 * @return the model that should be used in this scenario. If no changes are needed, just return {@code model} as-is.
		 * @see ModelLoadingPlugin.Context#modifyModelBeforeBake
		 */
		UnbakedModel modifyModelBeforeBake(UnbakedModel model, Context context);

		/**
		 * Context for a before bake model modification event.
		 */
		@ApiStatus.NonExtendable
		interface Context {
			/**
			 * The identifier of this model (may be a {@link ModelIdentifier}).
			 */
			Identifier id();

			/**
			 * Function that can be used to retrieve sprites.
			 */
			Function<SpriteIdentifier, Sprite> textureGetter();

			/**
			 * The settings this model is being baked with.
			 */
			ModelBakeSettings settings();

			/**
			 * The baker being used to bake this model.
			 * It can be used to {@linkplain Baker#bake load baked models}.
			 */
			Baker baker();

			/**
			 * The current model loader instance (changes when resource packs reload).
			 * It can be used to {@linkplain ModelLoader#getOrLoadModel load unbaked models}.
			 */
			ModelLoader loader();
		}
	}

	@FunctionalInterface
	public interface AfterBake {
		/**
		 * This handler is invoked to allow modifying the baked model instance that is used and stored, after baking.
		 *
		 * <p>For further information, see the docs of {@link ModelLoadingPlugin.Context#modifyModelAfterBake()}.
		 *
		 * @param model the current baked model instance
		 * @param context context with additional information about the model/loader
		 * @return the model that should be used in this scenario. If no changes are needed, just return {@code model} as-is.
		 * @see ModelLoadingPlugin.Context#modifyModelAfterBake
		 */
		BakedModel modifyModelAfterBake(BakedModel model, Context context);

		/**
		 * Context for an after bake model modification event.
		 */
		@ApiStatus.NonExtendable
		interface Context {
			/**
			 * The identifier of this model (may be a {@link ModelIdentifier}).
			 */
			Identifier id();

			/**
			 * The unbaked model that is being baked.
			 */
			UnbakedModel sourceModel();

			/**
			 * Function that can be used to retrieve sprites.
			 */
			Function<SpriteIdentifier, Sprite> textureGetter();

			/**
			 * The settings this model is being baked with.
			 */
			ModelBakeSettings settings();

			/**
			 * The baker being used to bake this model.
			 * It can be used to {@linkplain Baker#bake load baked models}.
			 */
			Baker baker();

			/**
			 * The current model loader instance (changes when resource packs reload).
			 * It can be used to {@linkplain ModelLoader#getOrLoadModel load unbaked models}.
			 */
			ModelLoader loader();
		}
	}

	private ModelModifier() { }
}
