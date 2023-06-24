package net.fabricmc.fabric.api.client.model;

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

public interface ModelLoadObserver {
	/**
	 * Use this event to modify/observe the unbaked model that will be added to the registry for a given identifier.
	 * @param location the {@link Identifier} of the model (this may be a {@link ModelIdentifier})
	 * @param originalModel the original model
	 * @param loader the model loader
	 * @return the model which should actually be loaded for this resource location
	 */
	default UnbakedModel onUnbakedModelLoad(Identifier location, UnbakedModel originalModel, ModelLoader loader) {
		return originalModel;
	}

	/**
	 * Use this event to change the unbaked model that will be used for baking a given identifier.
	 * @param location the {@link Identifier} of the model (this may be a {@link ModelIdentifier})
	 * @param originalModel the original model
	 * @param loader the model loader
	 * @return the model which will be used for baking this identifier
	 */
	default UnbakedModel onUnbakedModelPreBake(Identifier location, UnbakedModel originalModel, ModelLoader loader) {
		return originalModel;
	}

	/**
	 * Use this event to modify the baked model that will be added to the model registry for this identifier.
	 * @param location the {@link Identifier} of the model (this may be a {@link ModelIdentifier})
	 * @param originalModel the original model
	 * @param textureGetter a function to retrieve textures
	 * @param settings the settings the model was baked with
	 * @param baker a Baker that can be used to bake other models
	 * @param loader the model loader
	 * @return the model which should actually be loaded for this identifier
	 */
	default BakedModel onBakedModelLoad(Identifier location, UnbakedModel unbakedModel, BakedModel originalModel, Function<SpriteIdentifier, Sprite> textureGetter, ModelBakeSettings settings, Baker baker, ModelLoader loader) {
		return originalModel;
	}
}
