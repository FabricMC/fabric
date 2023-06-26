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

import net.fabricmc.fabric.api.event.Event;

import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.Baker;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;

import java.util.function.Function;

/**
 * Contains interfaces for the events mods can use to modify models. These events have multiple phases: OVERRIDE,
 * DEFAULT, and WRAP, that fire in that order. These can be used to help maximize compatibility among multiple mods.
 *
 * Example use cases:
 * <ol>
 *     <li>Overriding a model for a particular blockstate - check if the given identifier is a {@link ModelIdentifier},
 *     and then check if it has the appropriate variant for that blockstate. If so, return your desired model,
 *     otherwise return the given model.</li>
 *     <li>Wrapping a model to override certain behaviors - simply return a new model instance and delegate calls
 *     to the original model as needed.</li>
 * </ol>
 *
 * Phases are used to ensure that modifications occur in a reasonable order, e.g. wrapping occurs after overrides,
 * and separate phases are provided for mods that wrap their own models and mods that need to wrap all models.
 * <p></p>
 * These hooks fire for every single model that's loaded so the code written here should be as simple/performant
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
	 * Recommended phase to use when wrapping your own mod's existing models.
	 */
	public static final Identifier WRAP_SELF_PHASE = new Identifier("fabric", "wrap_self");
	/**
	 * Recommended phase to use when wrapping other models, e.g. for connected textures or other similar visual
	 * effects.
	 */
	public static final Identifier WRAP_FINAL_PHASE = new Identifier("fabric", "wrap_final");

	private ModelModifier() {}

	@FunctionalInterface
	public interface Unbaked {
		/**
		 * This handler is invoked to allow modifying the unbaked model instance that is used/stored in a given,
		 * event-dependent scenario.
		 * <p></p>
		 * For further information, see the docs of the particular event you are registering for.
		 * @param model the current unbaked model instance
		 * @param context context with additional information about the model/loader
		 * @return the model that should be used in this scenario. If no changes are needed, just return {@code model} as-is.
		 */
		UnbakedModel modifyUnbakedModel(UnbakedModel model, Context context);

		/**
		 * Context for an unbaked model load/pre-bake event.
		 * @param location the location of this model (may be a {@link ModelIdentifier})
		 * @param loader the current model loader instance (changes when resource packs reload)
		 */
		record Context(Identifier location, ModelLoader loader) {}
	}

	@FunctionalInterface
	public interface Baked {
		/**
		 * This handler is invoked to allow modifying the baked model instance that is used/stored in a given,
		 * event-dependent scenario.
		 * <p></p>
		 * For further information, see the docs of the particular event you are registering for.
		 * @param model the current baked model instance
		 * @param context context with additional information about the model/loader
		 * @return the model that should be used in this scenario. If no changes are needed, just return {@code model} as-is.
		 */
		BakedModel modifyBakedModel(BakedModel model, Context context);

		/**
		 * Context for a baked model load event.
		 * @param location the location of this model (may be a {@link ModelIdentifier})
		 * @param sourceModel the unbaked model that is being baked
		 * @param textureGetter function that can be used to retrieve sprites
		 * @param settings the settings this model is being baked with
		 * @param baker the baker being used to bake this model
		 * @param loader the current model loader instance (changes when resource packs reload)
		 */
		record Context(Identifier location, UnbakedModel sourceModel, Function<SpriteIdentifier, Sprite> textureGetter, ModelBakeSettings settings, Baker baker, ModelLoader loader) {}
	}
}
