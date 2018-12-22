package net.fabricmc.fabric.mixin.client.render.model;

import net.fabricmc.fabric.api.events.BakedModelReloadEvent;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

/**
 * @author shadowfacts
 */
@Mixin(ModelLoader.class)
public class MixinModelLoader {

	@Shadow
	private Map<Identifier, BakedModel> bakedModels;

	@Inject(method = "<init>", at = @At("RETURN"))
	public void loadCustomModels(ResourceManager resourceManager, SpriteAtlasTexture texture, CallbackInfo info) {
		for (BakedModelReloadEvent handler : BakedModelReloadEvent.BLOCK_MODEL_RELOAD.getBackingArray()) {
			handler.reloadBlockModels(bakedModels);
		}
	}

}
