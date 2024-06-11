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
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

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
 * Contains interfaces for the events that can be used to modify models at different points in the loading and baking
 * process.
 *
 * <p>Example use cases:
 * <ul>
 *     <li>Overriding a model for a particular block state - check if the given top-level identifier is not null,
 *     and then check if it has the appropriate variant for that block state. If so, return your desired model,
 *     otherwise return the given model.</li>
 *     <li>Wrapping a model to override certain behaviors - simply return a new model instance and delegate calls
 *     to the original model as needed.</li>
 * </ul>
 *
 * <p>Phases are used to ensure that modifications occur in a reasonable order, e.g. wrapping occurs after overrides,
 * and separate phases are provided for mods that wrap their own models and mods that need to wrap models of other mods
 * or wrap models arbitrarily.
 *
 * <p>These callbacks are invoked for <b>every single model that is loaded or baked</b>, so implementations should be
 * as efficient as possible.
 */
public final class ModelModifier {
	/**
	 * Recommended phase to use when overriding models, e.g. replacing a model with another model.
	 */
	public static final Identifier OVERRIDE_PHASE = Identifier.of("fabric", "override");
	/**
	 * Recommended phase to use for transformations that need to happen before wrapping, but after model overrides.
	 */
	public static final Identifier DEFAULT_PHASE = Event.DEFAULT_PHASE;
	/**
	 * Recommended phase to use when wrapping models.
	 */
	public static final Identifier WRAP_PHASE = Identifier.of("fabric", "wrap");
	/**
	 * Recommended phase to use when wrapping models with transformations that want to happen last,
	 * e.g. for connected textures or other similar visual effects that should be the final processing step.
	 */
	public static final Identifier WRAP_LAST_PHASE = Identifier.of("fabric", "wrap_last");

	@FunctionalInterface
	public interface OnLoad {
		/**
		 * This handler is invoked to allow modification of an unbaked model right after it is first loaded and before
		 * it is cached.
		 *
		 * @param model the current unbaked model instance
		 * @param context context with additional information about the model/loader
		 * @return the model that should be used in this scenario. If no changes are needed, just return {@code model} as-is.
		 * @see ModelLoadingPlugin.Context#modifyModelOnLoad
		 */
		UnbakedModel modifyModelOnLoad(UnbakedModel model, Context context);

		/**
		 * The context for an on load model modification event.
		 */
		@ApiStatus.NonExtendable
		interface Context {
			/**
			 * Models with a resource ID are loaded directly from JSON or a {@link ModelModifier}.
			 *
			 * @return the identifier of the given model as an {@link Identifier}, or null if {@link #topLevelId()} is
			 * not null
			 */
			@UnknownNullability("#topLevelId() != null")
			Identifier resourceId();

			/**
			 * Models with a top-level ID are loaded from blockstate files, {@link BlockStateResolver}s, or by copying
			 * a previously loaded model.
			 *
			 * @return the identifier of the given model as a {@link ModelIdentifier}, or null if {@link #resourceId()}
			 * is not null
			 */
			@UnknownNullability("#resourceId() != null")
			ModelIdentifier topLevelId();

			/**
			 * Loads a model using an {@link Identifier}, or gets it if it was already loaded.
			 *
			 * @param id the model identifier
			 * @return the unbaked model, or a missing model if it is not present
			 */
			UnbakedModel getOrLoadModel(Identifier id);

			/**
			 * The current model loader instance, which changes between resource reloads.
			 */
			ModelLoader loader();
		}
	}

	@FunctionalInterface
	public interface BeforeBake {
		/**
		 * This handler is invoked to allow modification of the unbaked model instance right before it is baked.
		 *
		 * @param model the current unbaked model instance
		 * @param context context with additional information about the model/loader
		 * @return the model that should be used in this scenario. If no changes are needed, just return {@code model} as-is.
		 * @see ModelLoadingPlugin.Context#modifyModelBeforeBake
		 */
		UnbakedModel modifyModelBeforeBake(UnbakedModel model, Context context);

		/**
		 * The context for a before bake model modification event.
		 */
		@ApiStatus.NonExtendable
		interface Context {
			/**
			 * Models with a resource ID are loaded directly from JSON or a {@link ModelModifier}.
			 *
			 * @return the identifier of the given model as an {@link Identifier}, or null if {@link #topLevelId()} is
			 * not null
			 */
			@UnknownNullability("#topLevelId() != null")
			Identifier resourceId();

			/**
			 * Models with a top-level ID are loaded from blockstate files, {@link BlockStateResolver}s, or by copying
			 * a previously loaded model.
			 *
			 * @return the identifier of the given model as a {@link ModelIdentifier}, or null if {@link #resourceId()}
			 * is not null
			 */
			@UnknownNullability("#resourceId() != null")
			ModelIdentifier topLevelId();

			/**
			 * The function that can be used to retrieve sprites.
			 */
			Function<SpriteIdentifier, Sprite> textureGetter();

			/**
			 * The settings this model is being baked with.
			 */
			ModelBakeSettings settings();

			/**
			 * The baker being used to bake this model.
			 * It can be used to {@linkplain Baker#getOrLoadModel load unbaked models} and
			 * {@linkplain Baker#bake load baked models}.
			 */
			Baker baker();

			/**
			 * The current model loader instance, which changes between resource reloads.
			 */
			ModelLoader loader();
		}
	}

	@FunctionalInterface
	public interface AfterBake {
		/**
		 * This handler is invoked to allow modification of the baked model instance right after it is baked and before
		 * it is cached.
		 *
		 * <p>Note that the passed baked model may be null and that this handler may return a null baked model, since
		 * {@link UnbakedModel#bake} and {@link Baker#bake} may also return null baked models. Null baked models are
		 * automatically mapped to the missing model during model retrieval.
		 *
		 * <p>For further information, see the docs of {@link ModelLoadingPlugin.Context#modifyModelAfterBake()}.
		 *
		 * @param model the current baked model instance
		 * @param context context with additional information about the model/loader
		 * @return the model that should be used in this scenario. If no changes are needed, just return {@code model} as-is.
		 * @see ModelLoadingPlugin.Context#modifyModelAfterBake
		 */
		@Nullable
		BakedModel modifyModelAfterBake(@Nullable BakedModel model, Context context);

		/**
		 * The context for an after bake model modification event.
		 */
		@ApiStatus.NonExtendable
		interface Context {
			/**
			 * Models with a resource ID are loaded directly from JSON or a {@link ModelModifier}.
			 *
			 * @return the identifier of the given model as an {@link Identifier}, or null if {@link #topLevelId()} is
			 * not null
			 */
			@UnknownNullability("#topLevelId() != null")
			Identifier resourceId();

			/**
			 * Models with a top-level ID are loaded from blockstate files, {@link BlockStateResolver}s, or by copying
			 * a previously loaded model.
			 *
			 * @return the identifier of the given model as a {@link ModelIdentifier}, or null if {@link #resourceId()}
			 * is not null
			 */
			@UnknownNullability("#resourceId() != null")
			ModelIdentifier topLevelId();

			/**
			 * The unbaked model that is being baked.
			 */
			UnbakedModel sourceModel();

			/**
			 * The function that can be used to retrieve sprites.
			 */
			Function<SpriteIdentifier, Sprite> textureGetter();

			/**
			 * The settings this model is being baked with.
			 */
			ModelBakeSettings settings();

			/**
			 * The baker being used to bake this model.
			 * It can be used to {@linkplain Baker#getOrLoadModel load unbaked models} and
			 * {@linkplain Baker#bake load baked models}.
			 */
			Baker baker();

			/**
			 * The current model loader instance, which changes between resource reloads.
			 */
			ModelLoader loader();
		}
	}

	private ModelModifier() { }
}
