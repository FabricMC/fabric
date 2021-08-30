package net.fabricmc.fabric.impl.client.rendering.fluid;

import net.fabricmc.fabric.api.client.render.fluid.v1.CustomFluidRenderer;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidOverlayBlock;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.TransparentBlock;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.texture.Sprite;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.BlockView;

public class DefaultLikeFluidRenderer implements CustomFluidRenderer {
	private static final float HALF_PI = MathHelper.field_29845;

	@Override
	public boolean renderFluid(BlockPos pos, BlockRenderView world, VertexConsumer vertexConsumer, FluidState state, DefaultBehavior defaultBehavior) {
		return render(world, pos, vertexConsumer, state);
	}

	private static final float DEFAULT_FLUID_HEIGHT = 8 / 9f;

	private static boolean isSameFluid(BlockView world, BlockPos pos, Direction side, FluidState state) {
		BlockPos blockPos = pos.offset(side);
		FluidState fluidState = world.getFluidState(blockPos);
		return fluidState.getFluid().matchesType(state.getFluid());
	}

	private static boolean isSideCovered(BlockView world, Direction direction, float height, BlockPos pos, BlockState state) {
		if (state.isOpaque()) {
			VoxelShape fluidShape = VoxelShapes.cuboid(0, 0, 0, 1, height, 1);
			VoxelShape neighborShape = state.getCullingShape(world, pos);
			return VoxelShapes.isSideCovered(fluidShape, neighborShape, direction);
		} else {
			return false;
		}
	}

	private static boolean isSideCovered(BlockView world, BlockPos pos, Direction direction, float maxDeviation) {
		BlockPos near = pos.offset(direction);
		BlockState nearState = world.getBlockState(near);
		return isSideCovered(world, direction, maxDeviation, near, nearState);
	}

	private static boolean isOppositeSideCovered(BlockView world, BlockPos pos, BlockState state, Direction direction) {
		return isSideCovered(world, direction.getOpposite(), 1.0F, pos, state);
	}

	public static boolean shouldRenderSide(BlockRenderView world, BlockPos pos, FluidState fluid, BlockState block, Direction direction) {
		return !isOppositeSideCovered(world, pos, block, direction) && !isSameFluid(world, pos, direction, fluid);
	}

	private boolean render(BlockRenderView world, BlockPos pos, VertexConsumer vertexConsumer, FluidState state) {

		FluidRenderHandler handler = FluidRenderRegistryImpl.INSTANCE.getRenderHandler(state.getFluid());

		Sprite[] sprites = handler.getFluidSprites(world, pos, state);
		BlockState blockState = world.getBlockState(pos);

		boolean noOverlay = sprites.length < 3;
		Sprite overlay = !noOverlay ? sprites[2] : null;

		int tint = handler.getFluidColor(world, pos, state);
		float tintR = (float) (tint >> 16 & 255) / 255f;
		float tintG = (float) (tint >> 8 & 255) / 255f;
		float tintB = (float) (tint & 255) / 255f;

		boolean renderUp = !isSameFluid(world, pos, Direction.UP, state);
		boolean renderDown = shouldRenderSide(world, pos, state, blockState, Direction.DOWN)
								 && !isSideCovered(world, pos, Direction.DOWN, DEFAULT_FLUID_HEIGHT);

		boolean renderNorth = shouldRenderSide(world, pos, state, blockState, Direction.NORTH);
		boolean renderSouth = shouldRenderSide(world, pos, state, blockState, Direction.SOUTH);
		boolean renderWest = shouldRenderSide(world, pos, state, blockState, Direction.WEST);
		boolean renderEast = shouldRenderSide(world, pos, state, blockState, Direction.EAST);

		if (!renderUp && !renderDown && !renderEast && !renderWest && !renderNorth && !renderSouth) {
			return false;
		} else {
			boolean tessellated = false;

			// Diffuse lighting brightness
			float brightnessDown = world.getBrightness(Direction.DOWN, true);
			float brightnessUp = world.getBrightness(Direction.UP, true);
			float brightnessZ = world.getBrightness(Direction.NORTH, true);
			float brightnessX = world.getBrightness(Direction.WEST, true);

			// Corner fluid heights
			float heightNW = getCornerFluidHeight(world, pos, state.getFluid());
			float heightSW = getCornerFluidHeight(world, pos.south(), state.getFluid());
			float heightSE = getCornerFluidHeight(world, pos.east().south(), state.getFluid());
			float heightNE = getCornerFluidHeight(world, pos.east(), state.getFluid());

			double localX = pos.getX() & 15;
			double localY = pos.getY() & 15;
			double localZ = pos.getZ() & 15;

			float bottomY = renderDown ? 0.001f : 0;

			if (renderUp && !isSideCovered(world, pos, Direction.UP, Math.min(Math.min(heightNW, heightSW), Math.min(heightSE, heightNE)))) {
				float texU1;
				float texU2;
				float texU3;
				float texU4;
				float texV2;
				float texV3;
				float texV4;
				float texV1;

				tessellated = true;
				heightNW -= 0.001f;
				heightSW -= 0.001f;
				heightSE -= 0.001f;
				heightNE -= 0.001f;

				Vec3d velo = state.getVelocity(world, pos);
				Sprite sprite;
				if (velo.x == 0 && velo.z == 0) {
					// Still texture
					sprite = sprites[0];
					texU1 = sprite.getFrameU(0);
					texV1 = sprite.getFrameV(0);

					texU3 = sprite.getFrameU(16);
					texV3 = sprite.getFrameV(16);

					texU2 = texU1;
					texV2 = texV3;

					texU4 = texU3;
					texV4 = texV1;
				} else {
					sprite = sprites[1];
					float fluidAngle = (float) MathHelper.atan2(velo.z, velo.x) - HALF_PI;
					float sin = MathHelper.sin(fluidAngle) * 0.25f;
					float cos = MathHelper.cos(fluidAngle) * 0.25f;

					texU1 = sprite.getFrameU(8 + (-cos - sin) * 16);
					texV1 = sprite.getFrameV(8 + (-cos + sin) * 16);

					texU2 = sprite.getFrameU(8 + (-cos + sin) * 16);
					texV2 = sprite.getFrameV(8 + (cos + sin) * 16);

					texU3 = sprite.getFrameU(8 + (cos + sin) * 16);
					texV3 = sprite.getFrameV(8 + (cos - sin) * 16);

					texU4 = sprite.getFrameU(8 + (cos - sin) * 16);
					texV4 = sprite.getFrameV(8 + (-cos - sin) * 16);
				}

				// Avoid pixly lines of neighboring sprites in the texture atlas, along edges of fluid faces
				float centerU = (texU1 + texU2 + texU3 + texU4) / 4;
				float centerV = (texV1 + texV2 + texV3 + texV4) / 4;

				float atlasWidth = sprites[0].getWidth() / (sprites[0].getMaxU() - sprites[0].getMinU());
				float atlasHeight = sprites[0].getHeight() / (sprites[0].getMaxV() - sprites[0].getMinV());
				float uvInset = 4 / Math.max(atlasHeight, atlasWidth);

				texU1 = MathHelper.lerp(uvInset, texU1, centerU);
				texU2 = MathHelper.lerp(uvInset, texU2, centerU);
				texU3 = MathHelper.lerp(uvInset, texU3, centerU);
				texU4 = MathHelper.lerp(uvInset, texU4, centerU);

				texV1 = MathHelper.lerp(uvInset, texV1, centerV);
				texV2 = MathHelper.lerp(uvInset, texV2, centerV);
				texV3 = MathHelper.lerp(uvInset, texV3, centerV);
				texV4 = MathHelper.lerp(uvInset, texV4, centerV);

				// Tessellate up face
				int light = getLight(world, pos);
				float faceR = brightnessUp * tintR;
				float faceG = brightnessUp * tintG;
				float faceB = brightnessUp * tintB;

				// Outwards face
				vertex(vertexConsumer, localX + 0, localY + heightNW, localZ + 0, faceR, faceG, faceB, texU1, texV1, light);
				vertex(vertexConsumer, localX + 0, localY + heightSW, localZ + 1, faceR, faceG, faceB, texU2, texV2, light);
				vertex(vertexConsumer, localX + 1, localY + heightSE, localZ + 1, faceR, faceG, faceB, texU3, texV3, light);
				vertex(vertexConsumer, localX + 1, localY + heightNE, localZ + 0, faceR, faceG, faceB, texU4, texV4, light);

				if (state.method_15756(world, pos.up())) {
					// Inwards face
					vertex(vertexConsumer, localX + 0, localY + heightNW, localZ + 0, faceR, faceG, faceB, texU1, texV1, light);
					vertex(vertexConsumer, localX + 1, localY + heightNE, localZ + 0, faceR, faceG, faceB, texU4, texV4, light);
					vertex(vertexConsumer, localX + 1, localY + heightSE, localZ + 1, faceR, faceG, faceB, texU3, texV3, light);
					vertex(vertexConsumer, localX + 0, localY + heightSW, localZ + 1, faceR, faceG, faceB, texU2, texV2, light);
				}
			}

			if (renderDown) {
				float texU1 = sprites[0].getMinU();
				float texU2 = sprites[0].getMaxU();
				float texV1 = sprites[0].getMinV();
				float texV2 = sprites[0].getMaxV();

				int light = getLight(world, pos.down());
				float faceR = brightnessDown * tintR;
				float faceG = brightnessDown * tintG;
				float faceB = brightnessDown * tintB;

				vertex(vertexConsumer, localX + 0, localY + bottomY, localZ + 1, faceR, faceG, faceB, texU1, texV2, light);
				vertex(vertexConsumer, localX + 0, localY + bottomY, localZ + 0, faceR, faceG, faceB, texU1, texV1, light);
				vertex(vertexConsumer, localX + 1, localY + bottomY, localZ + 0, faceR, faceG, faceB, texU2, texV1, light);
				vertex(vertexConsumer, localX + 1, localY + bottomY, localZ + 1, faceR, faceG, faceB, texU2, texV2, light);
				tessellated = true;
			}

			int light = getLight(world, pos);

			for (int face = 0; face < 4; face++) {
				float heightL;
				float heightR;

				double xL;
				double zL;

				double xR;
				double zR;

				Direction direction;
				boolean shouldRender;

				if (face == 0) { // North
					heightL = heightNW;
					heightR = heightNE;

					xL = localX;
					xR = localX + 1;

					zL = localZ + 0.001f;
					zR = localZ + 0.001f;

					direction = Direction.NORTH;
					shouldRender = renderNorth;
				} else if (face == 1) { // South
					heightL = heightSE;
					heightR = heightSW;

					xL = localX + 1;
					xR = localX;

					zL = localZ + 1 - 0.001f;
					zR = localZ + 1 - 0.001f;

					direction = Direction.SOUTH;
					shouldRender = renderSouth;
				} else if (face == 2) { // West
					heightL = heightSW;
					heightR = heightNW;

					xL = localX + 0.001f;
					xR = localX + 0.001f;

					zL = localZ + 1;
					zR = localZ;

					direction = Direction.WEST;
					shouldRender = renderWest;
				} else { // East
					heightL = heightNE;
					heightR = heightSE;

					xL = localX + 1 - 0.001f;
					xR = localX + 1 - 0.001f;

					zL = localZ;
					zR = localZ + 1;

					direction = Direction.EAST;
					shouldRender = renderEast;
				}

				if (shouldRender && !isSideCovered(world, pos, direction, Math.max(heightL, heightR))) {
					tessellated = true;

					BlockPos near = pos.offset(direction);
					Sprite sprite = sprites[1];

					if (!noOverlay) {
						Block block = world.getBlockState(near).getBlock();
						if (block instanceof TransparentBlock || block instanceof LeavesBlock || block instanceof FluidOverlayBlock) {
							sprite = overlay;
						}
					}

					float texU1 = sprite.getFrameU(0);
					float texU2 = sprite.getFrameU(8);

					float texVL = sprite.getFrameV((1 - heightL) * 8);
					float texVR = sprite.getFrameV((1 - heightR) * 8);
					float texVB = sprite.getFrameV(8);

					float brightness = face < 2 ? brightnessZ : brightnessX;
					float faceR = brightnessUp * brightness * tintR;
					float faceG = brightnessUp * brightness * tintG;
					float faceB = brightnessUp * brightness * tintB;

					// Outwards face
					vertex(vertexConsumer, xL, localY + heightL, zL, faceR, faceG, faceB, texU1, texVL, light);
					vertex(vertexConsumer, xR, localY + heightR, zR, faceR, faceG, faceB, texU2, texVR, light);
					vertex(vertexConsumer, xR, localY + bottomY, zR, faceR, faceG, faceB, texU2, texVB, light);
					vertex(vertexConsumer, xL, localY + bottomY, zL, faceR, faceG, faceB, texU1, texVB, light);

					if (sprite != overlay) {
						// Inwards face
						vertex(vertexConsumer, xL, localY + bottomY, zL, faceR, faceG, faceB, texU1, texVB, light);
						vertex(vertexConsumer, xR, localY + bottomY, zR, faceR, faceG, faceB, texU2, texVB, light);
						vertex(vertexConsumer, xR, localY + heightR, zR, faceR, faceG, faceB, texU2, texVR, light);
						vertex(vertexConsumer, xL, localY + heightL, zL, faceR, faceG, faceB, texU1, texVL, light);
					}
				}
			}

			return tessellated;
		}
	}

	private void vertex(VertexConsumer vertexConsumer, double x, double y, double z, float red, float green, float blue, float u, float v, int light) {
		vertexConsumer.vertex(x, y, z)
					  .color(red, green, blue, 1)
					  .texture(u, v)
					  .light(light)
					  .normal(0, 1, 0)
					  .next();
	}

	private int getLight(BlockRenderView world, BlockPos pos) {
		int lightHere = WorldRenderer.getLightmapCoordinates(world, pos);
		int lightUp = WorldRenderer.getLightmapCoordinates(world, pos.up());

		int blockHere = lightHere & 255;
		int blockUp = lightUp & 255;

		int skyHere = lightHere >> 16 & 255;
		int skyUp = lightUp >> 16 & 255;
		return Math.max(blockHere, blockUp) | Math.max(skyHere, skyUp) << 16;
	}

	private float getCornerFluidHeight(BlockView world, BlockPos pos, Fluid fluid) {
		int weights = 0;
		float height = 0;

		for (int itr = 0; itr < 4; itr++) {

			BlockPos near = pos.add(-(itr & 1), 0, -(itr >> 1 & 1));

			if (world.getFluidState(near.up()).getFluid().matchesType(fluid)) {
				return 1;
			}

			FluidState nearState = world.getFluidState(near);
			if (nearState.getFluid().matchesType(fluid)) {
				float hgt = nearState.getHeight(world, near);
				int wgt = getFluidSlopeWeight(nearState, world, near);
				height += hgt * wgt;
				weights += wgt;
			} else if (!world.getBlockState(near).getMaterial().isSolid()) {
				// height += 0;    but it's useless :)
				weights++;
			}
		}

		return height / (float) weights;
	}

	protected int getFluidSlopeWeight(FluidState state, BlockView world, BlockPos pos) {
		return state.isStill() ? 10 : 1;
	}
}
