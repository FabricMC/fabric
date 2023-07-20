package net.fabricmc.fabric.test.renderer.client;

import java.util.Set;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.client.model.loading.v1.ModelResolver;
import net.fabricmc.fabric.test.renderer.RendererTest;

public class ModelResolverImpl implements ModelResolver {
	private static final Set<Identifier> FRAME_MODEL_LOCATIONS = Set.of(
			RendererTest.id("block/frame"),
			RendererTest.id("item/frame"),
			RendererTest.id("item/frame_multipart"),
			RendererTest.id("item/frame_variant")
	);

	private static final Set<Identifier> PILLAR_MODEL_LOCATIONS = Set.of(
			RendererTest.id("block/pillar"),
			RendererTest.id("item/pillar")
	);

	private static final Set<Identifier> OCTAGONAL_COLUMN_MODEL_LOCATIONS = Set.of(
			RendererTest.id("block/octagonal_column"),
			RendererTest.id("item/octagonal_column")
	);

	@Override
	@Nullable
	public UnbakedModel resolveModel(Context context) {
		Identifier id = context.id();

		if (FRAME_MODEL_LOCATIONS.contains(id)) {
			return new FrameUnbakedModel();
		}

		if (PILLAR_MODEL_LOCATIONS.contains(id)) {
			return new PillarUnbakedModel();
		}

		if (OCTAGONAL_COLUMN_MODEL_LOCATIONS.contains(id)) {
			return new OctagonalColumnUnbakedModel();
		}

		return null;
	}
}
