package net.fabricmc.fabric.client.events;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.events.BakedModelReloadEvent;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.json.ModelItemPropertyOverrideList;
import net.minecraft.client.render.model.json.ModelTransformations;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.util.math.Direction;

import java.util.List;
import java.util.Random;

public class CustomBakedModelMod implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		BakedModelReloadEvent.BLOCK_MODEL_RELOAD.register(models -> {
			models.put(new ModelIdentifier("minecraft:diamond_block#"), new EmptyBakedModel());
		});
	}

	public static class EmptyBakedModel implements BakedModel {
		@Override
		public List<BakedQuad> getQuads(BlockState var1, Direction var2, Random var3) {
			return ImmutableList.of();
		}

		@Override
		public boolean useAmbientOcclusion() {
			return false;
		}

		@Override
		public boolean hasDepthInGui() {
			return false;
		}

		@Override
		public boolean isBuiltin() {
			return false;
		}

		@Override
		public Sprite getSprite() {
			return MinecraftClient.getInstance().getSpriteAtlas().getSprite(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
		}

		@Override
		public ModelTransformations getTransformations() {
			return ModelTransformations.ORIGIN;
		}

		@Override
		public ModelItemPropertyOverrideList getItemPropertyOverrides() {
			return ModelItemPropertyOverrideList.ORIGIN;
		}
	}

}
