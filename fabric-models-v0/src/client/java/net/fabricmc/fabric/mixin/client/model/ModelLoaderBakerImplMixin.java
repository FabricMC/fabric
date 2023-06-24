package net.fabricmc.fabric.mixin.client.model;

import net.fabricmc.fabric.impl.client.model.ModelLoaderHooks;

import net.fabricmc.fabric.impl.client.model.ModelLoadingRegistryImpl;

import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.Baker;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.UnbakedModel;

import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Function;

@Mixin(targets = { "net/minecraft/client/render/model/ModelLoader$BakerImpl"})
public class ModelLoaderBakerImplMixin {
	@Shadow
	@Final
	private ModelLoader field_40571;

	@Redirect(method = "bake", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/model/ModelLoader$BakerImpl;getOrLoadModel(Lnet/minecraft/util/Identifier;)Lnet/minecraft/client/render/model/UnbakedModel;"))
	private UnbakedModel firePreBakeEvent(@Coerce Baker baker, Identifier id) {
		UnbakedModel m = baker.getOrLoadModel(id);
		ModelLoadingRegistryImpl.LoaderInstance loader = ((ModelLoaderHooks)this.field_40571).fabric_getLoader();
		return loader.onUnbakedModelPreBake(id, m);
	}

	@Redirect(method = "bake", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/model/UnbakedModel;bake(Lnet/minecraft/client/render/model/Baker;Ljava/util/function/Function;Lnet/minecraft/client/render/model/ModelBakeSettings;Lnet/minecraft/util/Identifier;)Lnet/minecraft/client/render/model/BakedModel;"))
	private BakedModel fireRegularBakeEvent(UnbakedModel instance, Baker baker, Function<SpriteIdentifier, Sprite> textureGetter, ModelBakeSettings settings, Identifier identifier) {
		BakedModel theModel = instance.bake(baker, textureGetter, settings, identifier);
		ModelLoadingRegistryImpl.LoaderInstance loader = ((ModelLoaderHooks)this.field_40571).fabric_getLoader();
		return loader.onBakedModelLoad(identifier, instance, theModel, textureGetter, settings, baker);
	}

	@Redirect(method = "bake", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/model/json/JsonUnbakedModel;bake(Lnet/minecraft/client/render/model/Baker;Lnet/minecraft/client/render/model/json/JsonUnbakedModel;Ljava/util/function/Function;Lnet/minecraft/client/render/model/ModelBakeSettings;Lnet/minecraft/util/Identifier;Z)Lnet/minecraft/client/render/model/BakedModel;"))
	private BakedModel fireRegularBakeEvent(JsonUnbakedModel instance, Baker baker, JsonUnbakedModel parent, Function<SpriteIdentifier, Sprite> textureGetter, ModelBakeSettings settings, Identifier identifier, boolean hasDepth) {
		BakedModel theModel = instance.bake(baker, parent, textureGetter, settings, identifier, hasDepth);
		ModelLoadingRegistryImpl.LoaderInstance loader = ((ModelLoaderHooks)this.field_40571).fabric_getLoader();
		return loader.onBakedModelLoad(identifier, instance, theModel, textureGetter, settings, baker);
	}
}
