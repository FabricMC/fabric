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

package net.fabricmc.indigo.renderer.aocalc;

import java.util.BitSet;
import java.util.function.ToIntFunction;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.ExtendedBlockView;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadView;
import net.fabricmc.indigo.renderer.aocalc.AoCalculator.AoFunc;
import net.fabricmc.indigo.renderer.render.BlockRenderInfo;

/**
 * Copy of vanilla AoCalculator modified to output to use parameterized
 * outputs and brightness function.
 */
@Environment(EnvType.CLIENT)
public class VanillaAoCalc {
	private int[] vertexData = new int[28];
	private float[] aoBounds = new float[12];
	private final ToIntFunction<BlockPos> brightnessFunc;
	private final AoFunc aoFunc;

	public VanillaAoCalc(ToIntFunction<BlockPos> brightnessFunc, AoFunc aoFunc) {
		this.brightnessFunc = brightnessFunc;
		this.aoFunc = aoFunc;
	}

	public void compute(BlockRenderInfo blockInfo, QuadView quad, float[] ao, int[] brightness) {
		BitSet bits = new BitSet(3);
		quad.toVanilla(0, vertexData, 0, false);
		updateShape(blockInfo.blockView, blockInfo.blockState, blockInfo.blockPos, vertexData, quad.lightFace(), aoBounds, bits);
		apply(blockInfo.blockView, blockInfo.blockState, blockInfo.blockPos, quad.lightFace(), aoBounds, bits, ao, brightness);
	}

	private void apply(ExtendedBlockView blockView, BlockState blockState, BlockPos blockPos, Direction side,
			float[] aoBounds, BitSet bits, float[] ao, int[] brightness) {
		BlockPos lightPos = bits.get(0) ? blockPos.offset(side) : blockPos;
		NeighborData neighborData = NeighborData.getData(side);
		BlockPos.Mutable mpos = new BlockPos.Mutable();
		mpos.set(lightPos).setOffset(neighborData.faces[0]);
		int int_1 = brightnessFunc.applyAsInt(mpos);
		float float_1 = aoFunc.apply(mpos);
		mpos.set(lightPos).setOffset(neighborData.faces[1]);
		int int_2 = brightnessFunc.applyAsInt(mpos);
		float float_2 = aoFunc.apply(mpos);
		mpos.set(lightPos).setOffset(neighborData.faces[2]);
		int int_3 = brightnessFunc.applyAsInt(mpos);
		float float_3 = aoFunc.apply(mpos);
		mpos.set(lightPos).setOffset(neighborData.faces[3]);
		int int_4 = brightnessFunc.applyAsInt(mpos);
		float float_4 = aoFunc.apply(mpos);
		mpos.set(lightPos).setOffset(neighborData.faces[0]).setOffset(side);
		boolean boolean_1 = blockView.getBlockState(mpos).getLightSubtracted(blockView, mpos) == 0;
		mpos.set(lightPos).setOffset(neighborData.faces[1]).setOffset(side);
		boolean boolean_2 = blockView.getBlockState(mpos).getLightSubtracted(blockView, mpos) == 0;
		mpos.set(lightPos).setOffset(neighborData.faces[2]).setOffset(side);
		boolean boolean_3 = blockView.getBlockState(mpos).getLightSubtracted(blockView, mpos) == 0;
		mpos.set(lightPos).setOffset(neighborData.faces[3]).setOffset(side);
		boolean boolean_4 = blockView.getBlockState(mpos).getLightSubtracted(blockView, mpos) == 0;
		float float_6;
		int int_6;

		if (!boolean_3 && !boolean_1) {
			float_6 = float_1;
			int_6 = int_1;
		} else {
			mpos.set(lightPos).setOffset(neighborData.faces[0]).setOffset(neighborData.faces[2]);
			float_6 = aoFunc.apply(mpos);
			int_6 = brightnessFunc.applyAsInt(mpos);
		}

		float float_8;
		int int_8;

		if (!boolean_4 && !boolean_1) {
			float_8 = float_1;
			int_8 = int_1;
		} else {
			mpos.set(lightPos).setOffset(neighborData.faces[0]).setOffset(neighborData.faces[3]);
			float_8 = aoFunc.apply(mpos);
			int_8 = brightnessFunc.applyAsInt(mpos);
		}

		float float_10;
		int int_10;

		if (!boolean_3 && !boolean_2) {
			float_10 = float_2;
			int_10 = int_2;
		} else {
			mpos.set(lightPos).setOffset(neighborData.faces[1]).setOffset(neighborData.faces[2]);
			float_10 = aoFunc.apply(mpos);
			int_10 = brightnessFunc.applyAsInt(mpos);
		}

		float float_12;
		int int_12;

		if (!boolean_4 && !boolean_2) {
			float_12 = float_2;
			int_12 = int_2;
		} else {
			mpos.set(lightPos).setOffset(neighborData.faces[1]).setOffset(neighborData.faces[3]);
			float_12 = aoFunc.apply(mpos);
			int_12 = brightnessFunc.applyAsInt(mpos);
		}

		int int_13 = brightnessFunc.applyAsInt(blockPos);
		mpos.set(blockPos).setOffset(side);

		if (bits.get(0) || !blockView.getBlockState(mpos).isFullOpaque(blockView, mpos)) {
			int_13 = brightnessFunc.applyAsInt(mpos);
		}

		float float_13 = bits.get(0) ? blockView.getBlockState(lightPos).getAmbientOcclusionLightLevel(blockView, lightPos) : blockView.getBlockState(blockPos).getAmbientOcclusionLightLevel(blockView, blockPos);
		Translation blockModelRenderer$Translation_1 = Translation.getTranslations(side);
		float float_14;
		float float_15;
		float float_16;
		float float_17;

		if (bits.get(1) && neighborData.nonCubicWeight) {
			float_14 = (float_4 + float_1 + float_8 + float_13) * 0.25F;
			float_15 = (float_3 + float_1 + float_6 + float_13) * 0.25F;
			float_16 = (float_3 + float_2 + float_10 + float_13) * 0.25F;
			float_17 = (float_4 + float_2 + float_12 + float_13) * 0.25F;
			float float_22 = aoBounds[neighborData.field_4192[0].shape] * aoBounds[neighborData.field_4192[1].shape];
			float float_23 = aoBounds[neighborData.field_4192[2].shape] * aoBounds[neighborData.field_4192[3].shape];
			float float_24 = aoBounds[neighborData.field_4192[4].shape] * aoBounds[neighborData.field_4192[5].shape];
			float float_25 = aoBounds[neighborData.field_4192[6].shape] * aoBounds[neighborData.field_4192[7].shape];
			float float_26 = aoBounds[neighborData.field_4185[0].shape] * aoBounds[neighborData.field_4185[1].shape];
			float float_27 = aoBounds[neighborData.field_4185[2].shape] * aoBounds[neighborData.field_4185[3].shape];
			float float_28 = aoBounds[neighborData.field_4185[4].shape] * aoBounds[neighborData.field_4185[5].shape];
			float float_29 = aoBounds[neighborData.field_4185[6].shape] * aoBounds[neighborData.field_4185[7].shape];
			float float_30 = aoBounds[neighborData.field_4180[0].shape] * aoBounds[neighborData.field_4180[1].shape];
			float float_31 = aoBounds[neighborData.field_4180[2].shape] * aoBounds[neighborData.field_4180[3].shape];
			float float_32 = aoBounds[neighborData.field_4180[4].shape] * aoBounds[neighborData.field_4180[5].shape];
			float float_33 = aoBounds[neighborData.field_4180[6].shape] * aoBounds[neighborData.field_4180[7].shape];
			float float_34 = aoBounds[neighborData.field_4188[0].shape] * aoBounds[neighborData.field_4188[1].shape];
			float float_35 = aoBounds[neighborData.field_4188[2].shape] * aoBounds[neighborData.field_4188[3].shape];
			float float_36 = aoBounds[neighborData.field_4188[4].shape] * aoBounds[neighborData.field_4188[5].shape];
			float float_37 = aoBounds[neighborData.field_4188[6].shape] * aoBounds[neighborData.field_4188[7].shape];

			ao[blockModelRenderer$Translation_1.firstCorner] = float_14 * float_22 + float_15 * float_23 + float_16 * float_24 + float_17 * float_25;
			ao[blockModelRenderer$Translation_1.secondCorner] = float_14 * float_26 + float_15 * float_27 + float_16 * float_28 + float_17 * float_29;
			ao[blockModelRenderer$Translation_1.thirdCorner] = float_14 * float_30 + float_15 * float_31 + float_16 * float_32 + float_17 * float_33;
			ao[blockModelRenderer$Translation_1.fourthCorner] = float_14 * float_34 + float_15 * float_35 + float_16 * float_36 + float_17 * float_37;
			int int_14 = this.getAmbientOcclusionBrightness(int_4, int_1, int_8, int_13);
			int int_15 = this.getAmbientOcclusionBrightness(int_3, int_1, int_6, int_13);
			int int_16 = this.getAmbientOcclusionBrightness(int_3, int_2, int_10, int_13);
			int int_17 = this.getAmbientOcclusionBrightness(int_4, int_2, int_12, int_13);
			brightness[blockModelRenderer$Translation_1.firstCorner] = this.getBrightness(int_14, int_15, int_16, int_17, float_22, float_23, float_24, float_25);
			brightness[blockModelRenderer$Translation_1.secondCorner] = this.getBrightness(int_14, int_15, int_16, int_17, float_26, float_27, float_28, float_29);
			brightness[blockModelRenderer$Translation_1.thirdCorner] = this.getBrightness(int_14, int_15, int_16, int_17, float_30, float_31, float_32, float_33);
			brightness[blockModelRenderer$Translation_1.fourthCorner] = this.getBrightness(int_14, int_15, int_16, int_17, float_34, float_35, float_36, float_37);
		} else {
			float_14 = (float_4 + float_1 + float_8 + float_13) * 0.25F;
			float_15 = (float_3 + float_1 + float_6 + float_13) * 0.25F;
			float_16 = (float_3 + float_2 + float_10 + float_13) * 0.25F;
			float_17 = (float_4 + float_2 + float_12 + float_13) * 0.25F;
			brightness[blockModelRenderer$Translation_1.firstCorner] = this.getAmbientOcclusionBrightness(int_4, int_1, int_8, int_13);
			brightness[blockModelRenderer$Translation_1.secondCorner] = this.getAmbientOcclusionBrightness(int_3, int_1, int_6, int_13);
			brightness[blockModelRenderer$Translation_1.thirdCorner] = this.getAmbientOcclusionBrightness(int_3, int_2, int_10, int_13);
			brightness[blockModelRenderer$Translation_1.fourthCorner] = this.getAmbientOcclusionBrightness(int_4, int_2, int_12, int_13);

			ao[blockModelRenderer$Translation_1.firstCorner] = float_14;
			ao[blockModelRenderer$Translation_1.secondCorner] = float_15;
			ao[blockModelRenderer$Translation_1.thirdCorner] = float_16;
			ao[blockModelRenderer$Translation_1.fourthCorner] = float_17;
		}
	}

	private int getAmbientOcclusionBrightness(int int_1, int int_2, int int_3, int int_4) {
		if (int_1 == 0) {
			int_1 = int_4;
		}

		if (int_2 == 0) {
			int_2 = int_4;
		}

		if (int_3 == 0) {
			int_3 = int_4;
		}

		return int_1 + int_2 + int_3 + int_4 >> 2 & 16711935;
	}

	private int getBrightness(int int_1, int int_2, int int_3, int int_4, float float_1, float float_2, float float_3, float float_4) {
		int int_5 = (int) ((int_1 >> 16 & 255) * float_1 + (int_2 >> 16 & 255) * float_2 + (int_3 >> 16 & 255) * float_3 + (int_4 >> 16 & 255) * float_4) & 255;
		int int_6 = (int) ((int_1 & 255) * float_1 + (int_2 & 255) * float_2 + (int_3 & 255) * float_3 + (int_4 & 255) * float_4) & 255;
		return int_5 << 16 | int_6;
	}

	@Environment(EnvType.CLIENT)
	enum Translation {
		DOWN(0, 1, 2, 3),
		UP(2, 3, 0, 1),
		NORTH(3, 0, 1, 2),
		SOUTH(0, 1, 2, 3),
		WEST(3, 0, 1, 2),
		EAST(1, 2, 3, 0);

		private final int firstCorner;
		private final int secondCorner;
		private final int thirdCorner;
		private final int fourthCorner;
		private static final Translation[] VALUES = SystemUtil.consume(new Translation[6], (blockModelRenderer$Translations_1) -> {
			blockModelRenderer$Translations_1[Direction.DOWN.getId()] = DOWN;
			blockModelRenderer$Translations_1[Direction.UP.getId()] = UP;
			blockModelRenderer$Translations_1[Direction.NORTH.getId()] = NORTH;
			blockModelRenderer$Translations_1[Direction.SOUTH.getId()] = SOUTH;
			blockModelRenderer$Translations_1[Direction.WEST.getId()] = WEST;
			blockModelRenderer$Translations_1[Direction.EAST.getId()] = EAST;
		});

		Translation(int int_1, int int_2, int int_3, int int_4) {
			this.firstCorner = int_1;
			this.secondCorner = int_2;
			this.thirdCorner = int_3;
			this.fourthCorner = int_4;
		}

		public static Translation getTranslations(Direction direction_1) {
			return VALUES[direction_1.getId()];
		}
	}

	@Environment(EnvType.CLIENT)
	public enum NeighborData {
		DOWN(new Direction[]{Direction.WEST, Direction.EAST, Direction.NORTH, Direction.SOUTH}, 0.5F, true, new NeighborOrientation[]{NeighborOrientation.FLIP_WEST, NeighborOrientation.SOUTH, NeighborOrientation.FLIP_WEST, NeighborOrientation.FLIP_SOUTH, NeighborOrientation.WEST, NeighborOrientation.FLIP_SOUTH, NeighborOrientation.WEST, NeighborOrientation.SOUTH}, new NeighborOrientation[]{NeighborOrientation.FLIP_WEST, NeighborOrientation.NORTH, NeighborOrientation.FLIP_WEST, NeighborOrientation.FLIP_NORTH, NeighborOrientation.WEST, NeighborOrientation.FLIP_NORTH, NeighborOrientation.WEST, NeighborOrientation.NORTH}, new NeighborOrientation[]{NeighborOrientation.FLIP_EAST, NeighborOrientation.NORTH, NeighborOrientation.FLIP_EAST, NeighborOrientation.FLIP_NORTH, NeighborOrientation.EAST, NeighborOrientation.FLIP_NORTH, NeighborOrientation.EAST, NeighborOrientation.NORTH}, new NeighborOrientation[]{NeighborOrientation.FLIP_EAST, NeighborOrientation.SOUTH, NeighborOrientation.FLIP_EAST, NeighborOrientation.FLIP_SOUTH, NeighborOrientation.EAST, NeighborOrientation.FLIP_SOUTH, NeighborOrientation.EAST, NeighborOrientation.SOUTH}),
		UP(new Direction[]{Direction.EAST, Direction.WEST, Direction.NORTH, Direction.SOUTH}, 1.0F, true, new NeighborOrientation[]{NeighborOrientation.EAST, NeighborOrientation.SOUTH, NeighborOrientation.EAST, NeighborOrientation.FLIP_SOUTH, NeighborOrientation.FLIP_EAST, NeighborOrientation.FLIP_SOUTH, NeighborOrientation.FLIP_EAST, NeighborOrientation.SOUTH}, new NeighborOrientation[]{NeighborOrientation.EAST, NeighborOrientation.NORTH, NeighborOrientation.EAST, NeighborOrientation.FLIP_NORTH, NeighborOrientation.FLIP_EAST, NeighborOrientation.FLIP_NORTH, NeighborOrientation.FLIP_EAST, NeighborOrientation.NORTH}, new NeighborOrientation[]{NeighborOrientation.WEST, NeighborOrientation.NORTH, NeighborOrientation.WEST, NeighborOrientation.FLIP_NORTH, NeighborOrientation.FLIP_WEST, NeighborOrientation.FLIP_NORTH, NeighborOrientation.FLIP_WEST, NeighborOrientation.NORTH}, new NeighborOrientation[]{NeighborOrientation.WEST, NeighborOrientation.SOUTH, NeighborOrientation.WEST, NeighborOrientation.FLIP_SOUTH, NeighborOrientation.FLIP_WEST, NeighborOrientation.FLIP_SOUTH, NeighborOrientation.FLIP_WEST, NeighborOrientation.SOUTH}),
		NORTH(new Direction[]{Direction.UP, Direction.DOWN, Direction.EAST, Direction.WEST}, 0.8F, true, new NeighborOrientation[]{NeighborOrientation.UP, NeighborOrientation.FLIP_WEST, NeighborOrientation.UP, NeighborOrientation.WEST, NeighborOrientation.FLIP_UP, NeighborOrientation.WEST, NeighborOrientation.FLIP_UP, NeighborOrientation.FLIP_WEST}, new NeighborOrientation[]{NeighborOrientation.UP, NeighborOrientation.FLIP_EAST, NeighborOrientation.UP, NeighborOrientation.EAST, NeighborOrientation.FLIP_UP, NeighborOrientation.EAST, NeighborOrientation.FLIP_UP, NeighborOrientation.FLIP_EAST}, new NeighborOrientation[]{NeighborOrientation.DOWN, NeighborOrientation.FLIP_EAST, NeighborOrientation.DOWN, NeighborOrientation.EAST, NeighborOrientation.FLIP_DOWN, NeighborOrientation.EAST, NeighborOrientation.FLIP_DOWN, NeighborOrientation.FLIP_EAST}, new NeighborOrientation[]{NeighborOrientation.DOWN, NeighborOrientation.FLIP_WEST, NeighborOrientation.DOWN, NeighborOrientation.WEST, NeighborOrientation.FLIP_DOWN, NeighborOrientation.WEST, NeighborOrientation.FLIP_DOWN, NeighborOrientation.FLIP_WEST}),
		SOUTH(new Direction[]{Direction.WEST, Direction.EAST, Direction.DOWN, Direction.UP}, 0.8F, true, new NeighborOrientation[]{NeighborOrientation.UP, NeighborOrientation.FLIP_WEST, NeighborOrientation.FLIP_UP, NeighborOrientation.FLIP_WEST, NeighborOrientation.FLIP_UP, NeighborOrientation.WEST, NeighborOrientation.UP, NeighborOrientation.WEST}, new NeighborOrientation[]{NeighborOrientation.DOWN, NeighborOrientation.FLIP_WEST, NeighborOrientation.FLIP_DOWN, NeighborOrientation.FLIP_WEST, NeighborOrientation.FLIP_DOWN, NeighborOrientation.WEST, NeighborOrientation.DOWN, NeighborOrientation.WEST}, new NeighborOrientation[]{NeighborOrientation.DOWN, NeighborOrientation.FLIP_EAST, NeighborOrientation.FLIP_DOWN, NeighborOrientation.FLIP_EAST, NeighborOrientation.FLIP_DOWN, NeighborOrientation.EAST, NeighborOrientation.DOWN, NeighborOrientation.EAST}, new NeighborOrientation[]{NeighborOrientation.UP, NeighborOrientation.FLIP_EAST, NeighborOrientation.FLIP_UP, NeighborOrientation.FLIP_EAST, NeighborOrientation.FLIP_UP, NeighborOrientation.EAST, NeighborOrientation.UP, NeighborOrientation.EAST}),
		WEST(new Direction[]{Direction.UP, Direction.DOWN, Direction.NORTH, Direction.SOUTH}, 0.6F, true, new NeighborOrientation[]{NeighborOrientation.UP, NeighborOrientation.SOUTH, NeighborOrientation.UP, NeighborOrientation.FLIP_SOUTH, NeighborOrientation.FLIP_UP, NeighborOrientation.FLIP_SOUTH, NeighborOrientation.FLIP_UP, NeighborOrientation.SOUTH}, new NeighborOrientation[]{NeighborOrientation.UP, NeighborOrientation.NORTH, NeighborOrientation.UP, NeighborOrientation.FLIP_NORTH, NeighborOrientation.FLIP_UP, NeighborOrientation.FLIP_NORTH, NeighborOrientation.FLIP_UP, NeighborOrientation.NORTH}, new NeighborOrientation[]{NeighborOrientation.DOWN, NeighborOrientation.NORTH, NeighborOrientation.DOWN, NeighborOrientation.FLIP_NORTH, NeighborOrientation.FLIP_DOWN, NeighborOrientation.FLIP_NORTH, NeighborOrientation.FLIP_DOWN, NeighborOrientation.NORTH}, new NeighborOrientation[]{NeighborOrientation.DOWN, NeighborOrientation.SOUTH, NeighborOrientation.DOWN, NeighborOrientation.FLIP_SOUTH, NeighborOrientation.FLIP_DOWN, NeighborOrientation.FLIP_SOUTH, NeighborOrientation.FLIP_DOWN, NeighborOrientation.SOUTH}),
		EAST(new Direction[]{Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH}, 0.6F, true, new NeighborOrientation[]{NeighborOrientation.FLIP_DOWN, NeighborOrientation.SOUTH, NeighborOrientation.FLIP_DOWN, NeighborOrientation.FLIP_SOUTH, NeighborOrientation.DOWN, NeighborOrientation.FLIP_SOUTH, NeighborOrientation.DOWN, NeighborOrientation.SOUTH}, new NeighborOrientation[]{NeighborOrientation.FLIP_DOWN, NeighborOrientation.NORTH, NeighborOrientation.FLIP_DOWN, NeighborOrientation.FLIP_NORTH, NeighborOrientation.DOWN, NeighborOrientation.FLIP_NORTH, NeighborOrientation.DOWN, NeighborOrientation.NORTH}, new NeighborOrientation[]{NeighborOrientation.FLIP_UP, NeighborOrientation.NORTH, NeighborOrientation.FLIP_UP, NeighborOrientation.FLIP_NORTH, NeighborOrientation.UP, NeighborOrientation.FLIP_NORTH, NeighborOrientation.UP, NeighborOrientation.NORTH}, new NeighborOrientation[]{NeighborOrientation.FLIP_UP, NeighborOrientation.SOUTH, NeighborOrientation.FLIP_UP, NeighborOrientation.FLIP_SOUTH, NeighborOrientation.UP, NeighborOrientation.FLIP_SOUTH, NeighborOrientation.UP, NeighborOrientation.SOUTH});

		private final Direction[] faces;
		private final boolean nonCubicWeight;
		private final NeighborOrientation[] field_4192;
		private final NeighborOrientation[] field_4185;
		private final NeighborOrientation[] field_4180;
		private final NeighborOrientation[] field_4188;
		private static final NeighborData[] field_4190 = SystemUtil.consume(new NeighborData[6], (blockModelRenderer$NeighborDatas_1) -> {
			blockModelRenderer$NeighborDatas_1[Direction.DOWN.getId()] = DOWN;
			blockModelRenderer$NeighborDatas_1[Direction.UP.getId()] = UP;
			blockModelRenderer$NeighborDatas_1[Direction.NORTH.getId()] = NORTH;
			blockModelRenderer$NeighborDatas_1[Direction.SOUTH.getId()] = SOUTH;
			blockModelRenderer$NeighborDatas_1[Direction.WEST.getId()] = WEST;
			blockModelRenderer$NeighborDatas_1[Direction.EAST.getId()] = EAST;
		});

		NeighborData(Direction[] directions_1, float float_1, boolean boolean_1, NeighborOrientation[] blockModelRenderer$NeighborOrientations_1, NeighborOrientation[] blockModelRenderer$NeighborOrientations_2, NeighborOrientation[] blockModelRenderer$NeighborOrientations_3, NeighborOrientation[] blockModelRenderer$NeighborOrientations_4) {
			this.faces = directions_1;
			this.nonCubicWeight = boolean_1;
			this.field_4192 = blockModelRenderer$NeighborOrientations_1;
			this.field_4185 = blockModelRenderer$NeighborOrientations_2;
			this.field_4180 = blockModelRenderer$NeighborOrientations_3;
			this.field_4188 = blockModelRenderer$NeighborOrientations_4;
		}

		public static NeighborData getData(Direction direction_1) {
			return field_4190[direction_1.getId()];
		}
	}

	@Environment(EnvType.CLIENT)
	public enum NeighborOrientation {
		DOWN(Direction.DOWN, false),
		UP(Direction.UP, false),
		NORTH(Direction.NORTH, false),
		SOUTH(Direction.SOUTH, false),
		WEST(Direction.WEST, false),
		EAST(Direction.EAST, false),
		FLIP_DOWN(Direction.DOWN, true),
		FLIP_UP(Direction.UP, true),
		FLIP_NORTH(Direction.NORTH, true),
		FLIP_SOUTH(Direction.SOUTH, true),
		FLIP_WEST(Direction.WEST, true),
		FLIP_EAST(Direction.EAST, true);

		private final int shape;

		NeighborOrientation(Direction direction_1, boolean boolean_1) {
			this.shape = direction_1.getId() + (boolean_1 ? Direction.values().length : 0);
		}
	}

	public static void updateShape(ExtendedBlockView extendedBlockView_1, BlockState blockState_1, BlockPos blockPos_1, int[] ints_1, Direction direction_1, float[] floats_1, BitSet bitSet_1) {
		float float_1 = 32.0F;
		float float_2 = 32.0F;
		float float_3 = 32.0F;
		float float_4 = -32.0F;
		float float_5 = -32.0F;
		float float_6 = -32.0F;

		int int_2;
		float float_11;

		for (int_2 = 0; int_2 < 4; ++int_2) {
			float_11 = Float.intBitsToFloat(ints_1[int_2 * 7]);
			float float_8 = Float.intBitsToFloat(ints_1[int_2 * 7 + 1]);
			float float_9 = Float.intBitsToFloat(ints_1[int_2 * 7 + 2]);
			float_1 = Math.min(float_1, float_11);
			float_2 = Math.min(float_2, float_8);
			float_3 = Math.min(float_3, float_9);
			float_4 = Math.max(float_4, float_11);
			float_5 = Math.max(float_5, float_8);
			float_6 = Math.max(float_6, float_9);
		}

		if (floats_1 != null) {
			floats_1[Direction.WEST.getId()] = float_1;
			floats_1[Direction.EAST.getId()] = float_4;
			floats_1[Direction.DOWN.getId()] = float_2;
			floats_1[Direction.UP.getId()] = float_5;
			floats_1[Direction.NORTH.getId()] = float_3;
			floats_1[Direction.SOUTH.getId()] = float_6;
			int_2 = Direction.values().length;
			floats_1[Direction.WEST.getId() + int_2] = 1.0F - float_1;
			floats_1[Direction.EAST.getId() + int_2] = 1.0F - float_4;
			floats_1[Direction.DOWN.getId() + int_2] = 1.0F - float_2;
			floats_1[Direction.UP.getId() + int_2] = 1.0F - float_5;
			floats_1[Direction.NORTH.getId() + int_2] = 1.0F - float_3;
			floats_1[Direction.SOUTH.getId() + int_2] = 1.0F - float_6;
		}

		float_11 = 0.9999F;
		switch (direction_1) {
		case DOWN:
			bitSet_1.set(1, float_1 >= 1.0E-4F || float_3 >= 1.0E-4F || float_4 <= 0.9999F || float_6 <= 0.9999F);
			bitSet_1.set(0, (float_2 < 1.0E-4F || Block.isShapeFullCube(blockState_1.getCollisionShape(extendedBlockView_1, blockPos_1))) && float_2 == float_5);
			break;
		case UP:
			bitSet_1.set(1, float_1 >= 1.0E-4F || float_3 >= 1.0E-4F || float_4 <= 0.9999F || float_6 <= 0.9999F);
			bitSet_1.set(0, (float_5 > 0.9999F || Block.isShapeFullCube(blockState_1.getCollisionShape(extendedBlockView_1, blockPos_1))) && float_2 == float_5);
			break;
		case NORTH:
			bitSet_1.set(1, float_1 >= 1.0E-4F || float_2 >= 1.0E-4F || float_4 <= 0.9999F || float_5 <= 0.9999F);
			bitSet_1.set(0, (float_3 < 1.0E-4F || Block.isShapeFullCube(blockState_1.getCollisionShape(extendedBlockView_1, blockPos_1))) && float_3 == float_6);
			break;
		case SOUTH:
			bitSet_1.set(1, float_1 >= 1.0E-4F || float_2 >= 1.0E-4F || float_4 <= 0.9999F || float_5 <= 0.9999F);
			bitSet_1.set(0, (float_6 > 0.9999F || Block.isShapeFullCube(blockState_1.getCollisionShape(extendedBlockView_1, blockPos_1))) && float_3 == float_6);
			break;
		case WEST:
			bitSet_1.set(1, float_2 >= 1.0E-4F || float_3 >= 1.0E-4F || float_5 <= 0.9999F || float_6 <= 0.9999F);
			bitSet_1.set(0, (float_1 < 1.0E-4F || Block.isShapeFullCube(blockState_1.getCollisionShape(extendedBlockView_1, blockPos_1))) && float_1 == float_4);
			break;
		case EAST:
			bitSet_1.set(1, float_2 >= 1.0E-4F || float_3 >= 1.0E-4F || float_5 <= 0.9999F || float_6 <= 0.9999F);
			bitSet_1.set(0, (float_4 > 0.9999F || Block.isShapeFullCube(blockState_1.getCollisionShape(extendedBlockView_1, blockPos_1))) && float_1 == float_4);
		}
	}
}
