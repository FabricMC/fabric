package net.fabricmc.fabric.api.events;

import net.fabricmc.fabric.util.HandlerArray;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.util.Identifier;

import java.util.Map;

@FunctionalInterface
public interface BakedModelReloadEvent {

	/**
	 * Fired from {@link ModelLoader} construction.
	 * Used to inject custom {@link BakedModel}s into block model manager.
	 */
	public static final HandlerArray<BakedModelReloadEvent> BLOCK_MODEL_RELOAD = new HandlerArray<>(BakedModelReloadEvent.class);

	/**
	 * Note: Map keys should be {@link ModelIdentifier}s.
	 *
	 * @param models The current map of model identifiers to baked models.
	 */
	void reloadBlockModels(Map<Identifier, BakedModel> models);

}
