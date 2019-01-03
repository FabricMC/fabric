/*
 * Copyright (c) 2016, 2017, 2018 FabricMC
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

package net.fabricmc.fabric.api.client.render;

import it.unimi.dsi.fastutil.longs.Long2IntLinkedOpenHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.ExtendedBlockView;

/**
 * Adaptation of inner, non-static class in BlockModelRenderer that serves same purpose.
 */
@Environment(EnvType.CLIENT)
public class AoCalculator {
	
    /**
     * Duplicated (with better names) from BlockModelRenderer due to current lack of Access Transformers.
     */
    @Environment(EnvType.CLIENT)
    static enum NeighborData
    {
        DOWN(new Direction[]{Direction.WEST, Direction.EAST, Direction.NORTH, Direction.SOUTH},
                0.5F, true, 
                new NeighborOrientation[]{NeighborOrientation.FLIP_WEST, NeighborOrientation.SOUTH, NeighborOrientation.FLIP_WEST, NeighborOrientation.FLIP_SOUTH, NeighborOrientation.WEST, NeighborOrientation.FLIP_SOUTH, NeighborOrientation.WEST, NeighborOrientation.SOUTH}, 
                new NeighborOrientation[]{NeighborOrientation.FLIP_WEST, NeighborOrientation.NORTH, NeighborOrientation.FLIP_WEST, NeighborOrientation.FLIP_NORTH, NeighborOrientation.WEST, NeighborOrientation.FLIP_NORTH, NeighborOrientation.WEST, NeighborOrientation.NORTH},
                new NeighborOrientation[]{NeighborOrientation.FLIP_EAST, NeighborOrientation.NORTH, NeighborOrientation.FLIP_EAST, NeighborOrientation.FLIP_NORTH, NeighborOrientation.EAST, NeighborOrientation.FLIP_NORTH, NeighborOrientation.EAST, NeighborOrientation.NORTH},
                new NeighborOrientation[]{NeighborOrientation.FLIP_EAST, NeighborOrientation.SOUTH, NeighborOrientation.FLIP_EAST, NeighborOrientation.FLIP_SOUTH, NeighborOrientation.EAST, NeighborOrientation.FLIP_SOUTH, NeighborOrientation.EAST, NeighborOrientation.SOUTH}),
        UP(new Direction[]{Direction.EAST, Direction.WEST, Direction.NORTH, Direction.SOUTH}, 1.0F, true, new NeighborOrientation[]{NeighborOrientation.EAST, NeighborOrientation.SOUTH, NeighborOrientation.EAST, NeighborOrientation.FLIP_SOUTH, NeighborOrientation.FLIP_EAST, NeighborOrientation.FLIP_SOUTH, NeighborOrientation.FLIP_EAST, NeighborOrientation.SOUTH}, new NeighborOrientation[]{NeighborOrientation.EAST, NeighborOrientation.NORTH, NeighborOrientation.EAST, NeighborOrientation.FLIP_NORTH, NeighborOrientation.FLIP_EAST, NeighborOrientation.FLIP_NORTH, NeighborOrientation.FLIP_EAST, NeighborOrientation.NORTH}, new NeighborOrientation[]{NeighborOrientation.WEST, NeighborOrientation.NORTH, NeighborOrientation.WEST, NeighborOrientation.FLIP_NORTH, NeighborOrientation.FLIP_WEST, NeighborOrientation.FLIP_NORTH, NeighborOrientation.FLIP_WEST, NeighborOrientation.NORTH}, new NeighborOrientation[]{NeighborOrientation.WEST, NeighborOrientation.SOUTH, NeighborOrientation.WEST, NeighborOrientation.FLIP_SOUTH, NeighborOrientation.FLIP_WEST, NeighborOrientation.FLIP_SOUTH, NeighborOrientation.FLIP_WEST, NeighborOrientation.SOUTH}),
        NORTH(new Direction[]{Direction.UP, Direction.DOWN, Direction.EAST, Direction.WEST}, 0.8F, true, new NeighborOrientation[]{NeighborOrientation.UP, NeighborOrientation.FLIP_WEST, NeighborOrientation.UP, NeighborOrientation.WEST, NeighborOrientation.FLIP_UP, NeighborOrientation.WEST, NeighborOrientation.FLIP_UP, NeighborOrientation.FLIP_WEST}, new NeighborOrientation[]{NeighborOrientation.UP, NeighborOrientation.FLIP_EAST, NeighborOrientation.UP, NeighborOrientation.EAST, NeighborOrientation.FLIP_UP, NeighborOrientation.EAST, NeighborOrientation.FLIP_UP, NeighborOrientation.FLIP_EAST}, new NeighborOrientation[]{NeighborOrientation.DOWN, NeighborOrientation.FLIP_EAST, NeighborOrientation.DOWN, NeighborOrientation.EAST, NeighborOrientation.FLIP_DOWN, NeighborOrientation.EAST, NeighborOrientation.FLIP_DOWN, NeighborOrientation.FLIP_EAST}, new NeighborOrientation[]{NeighborOrientation.DOWN, NeighborOrientation.FLIP_WEST, NeighborOrientation.DOWN, NeighborOrientation.WEST, NeighborOrientation.FLIP_DOWN, NeighborOrientation.WEST, NeighborOrientation.FLIP_DOWN, NeighborOrientation.FLIP_WEST}),
        SOUTH(new Direction[]{Direction.WEST, Direction.EAST, Direction.DOWN, Direction.UP}, 0.8F, true, new NeighborOrientation[]{NeighborOrientation.UP, NeighborOrientation.FLIP_WEST, NeighborOrientation.FLIP_UP, NeighborOrientation.FLIP_WEST, NeighborOrientation.FLIP_UP, NeighborOrientation.WEST, NeighborOrientation.UP, NeighborOrientation.WEST}, new NeighborOrientation[]{NeighborOrientation.DOWN, NeighborOrientation.FLIP_WEST, NeighborOrientation.FLIP_DOWN, NeighborOrientation.FLIP_WEST, NeighborOrientation.FLIP_DOWN, NeighborOrientation.WEST, NeighborOrientation.DOWN, NeighborOrientation.WEST}, new NeighborOrientation[]{NeighborOrientation.DOWN, NeighborOrientation.FLIP_EAST, NeighborOrientation.FLIP_DOWN, NeighborOrientation.FLIP_EAST, NeighborOrientation.FLIP_DOWN, NeighborOrientation.EAST, NeighborOrientation.DOWN, NeighborOrientation.EAST}, new NeighborOrientation[]{NeighborOrientation.UP, NeighborOrientation.FLIP_EAST, NeighborOrientation.FLIP_UP, NeighborOrientation.FLIP_EAST, NeighborOrientation.FLIP_UP, NeighborOrientation.EAST, NeighborOrientation.UP, NeighborOrientation.EAST}),
        WEST(new Direction[]{Direction.UP, Direction.DOWN, Direction.NORTH, Direction.SOUTH}, 0.6F, true, new NeighborOrientation[]{NeighborOrientation.UP, NeighborOrientation.SOUTH, NeighborOrientation.UP, NeighborOrientation.FLIP_SOUTH, NeighborOrientation.FLIP_UP, NeighborOrientation.FLIP_SOUTH, NeighborOrientation.FLIP_UP, NeighborOrientation.SOUTH}, new NeighborOrientation[]{NeighborOrientation.UP, NeighborOrientation.NORTH, NeighborOrientation.UP, NeighborOrientation.FLIP_NORTH, NeighborOrientation.FLIP_UP, NeighborOrientation.FLIP_NORTH, NeighborOrientation.FLIP_UP, NeighborOrientation.NORTH}, new NeighborOrientation[]{NeighborOrientation.DOWN, NeighborOrientation.NORTH, NeighborOrientation.DOWN, NeighborOrientation.FLIP_NORTH, NeighborOrientation.FLIP_DOWN, NeighborOrientation.FLIP_NORTH, NeighborOrientation.FLIP_DOWN, NeighborOrientation.NORTH}, new NeighborOrientation[]{NeighborOrientation.DOWN, NeighborOrientation.SOUTH, NeighborOrientation.DOWN, NeighborOrientation.FLIP_SOUTH, NeighborOrientation.FLIP_DOWN, NeighborOrientation.FLIP_SOUTH, NeighborOrientation.FLIP_DOWN, NeighborOrientation.SOUTH}),
        EAST(new Direction[]{Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH}, 0.6F, true, new NeighborOrientation[]{NeighborOrientation.FLIP_DOWN, NeighborOrientation.SOUTH, NeighborOrientation.FLIP_DOWN, NeighborOrientation.FLIP_SOUTH, NeighborOrientation.DOWN, NeighborOrientation.FLIP_SOUTH, NeighborOrientation.DOWN, NeighborOrientation.SOUTH}, new NeighborOrientation[]{NeighborOrientation.FLIP_DOWN, NeighborOrientation.NORTH, NeighborOrientation.FLIP_DOWN, NeighborOrientation.FLIP_NORTH, NeighborOrientation.DOWN, NeighborOrientation.FLIP_NORTH, NeighborOrientation.DOWN, NeighborOrientation.NORTH}, new NeighborOrientation[]{NeighborOrientation.FLIP_UP, NeighborOrientation.NORTH, NeighborOrientation.FLIP_UP, NeighborOrientation.FLIP_NORTH, NeighborOrientation.UP, NeighborOrientation.FLIP_NORTH, NeighborOrientation.UP, NeighborOrientation.NORTH}, new NeighborOrientation[]{NeighborOrientation.FLIP_UP, NeighborOrientation.SOUTH, NeighborOrientation.FLIP_UP, NeighborOrientation.FLIP_SOUTH, NeighborOrientation.UP, NeighborOrientation.FLIP_SOUTH, NeighborOrientation.UP, NeighborOrientation.SOUTH});

        private final Direction[] faces;
        private final boolean nonCubicWeight;
        private final NeighborOrientation[] a;
        private final NeighborOrientation[] b;
        private final NeighborOrientation[] c;
        private final NeighborOrientation[] d;
        private static final NeighborData[] values = (NeighborData[])SystemUtil.consume(new NeighborData[6], (neighborData) ->
        {
            neighborData[Direction.DOWN.getId()] = DOWN;
            neighborData[Direction.UP.getId()] = UP;
            neighborData[Direction.NORTH.getId()] = NORTH;
            neighborData[Direction.SOUTH.getId()] = SOUTH;
            neighborData[Direction.WEST.getId()] = WEST;
            neighborData[Direction.EAST.getId()] = EAST;
        });

        private NeighborData(Direction[] faces, float float_1, boolean nonCubicWeight, NeighborOrientation[] a, NeighborOrientation[] b, NeighborOrientation[] c, NeighborOrientation[] d)
        {
            this.faces = faces;
            this.nonCubicWeight = nonCubicWeight;
            this.a = a;
            this.b = b;
            this.c = c;
            this.d = d;
        }

        public static NeighborData getData(Direction direction)
        {
            return values[direction.getId()];
        }
    }

    /**
     * Duplicated from BlockModelRenderer due to current lack of Access Transformers.
     */
    @Environment(EnvType.CLIENT)
    static enum NeighborOrientation
    {
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

        private NeighborOrientation(Direction direction, boolean flipped)
        {
            this.shape = direction.getId() + (flipped ? 6 : 0);
        }
    }

    /**
     * Duplicated from BlockModelRenderer due to current lack of Access Transformers.
     */
    static enum Translation
    {
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
        private static final Translation[] VALUES = (Translation[])SystemUtil.consume(new Translation[6], (translations) ->
        {
            translations[Direction.DOWN.getId()] = DOWN;
            translations[Direction.UP.getId()] = UP;
            translations[Direction.NORTH.getId()] = NORTH;
            translations[Direction.SOUTH.getId()] = SOUTH;
            translations[Direction.WEST.getId()] = WEST;
            translations[Direction.EAST.getId()] = EAST;
        });

        private Translation(int firstCorner, int secondCorner, int thirdCorner, int fourthCorner)
        {
            this.firstCorner = firstCorner;
            this.secondCorner = secondCorner;
            this.thirdCorner = thirdCorner;
            this.fourthCorner = fourthCorner;
        }

        public static Translation getTranslations(Direction direction_1) {
            return VALUES[direction_1.getId()];
        }
    }
    
    final float[] colorMultiplier = new float[4];
    final int[] brightness = new int[4];

    AoCalculator() {
    }

    /**
     * TODO: Call when have a new world view, at start of chunk rebuild
     */
    void clear()
    {
        map.clear();
    }
    
    /**
     * 
     * Mojang uses Object2Int.  This uses Long2Int for performance and to avoid
     * creating new immutable BlockPos references.  But will break if someone
     * wants to expand Y limit or world borders.  If we want to support that may
     * need to switch or make configurable.  TODO: profile and see how big the difference is.
     */
    @SuppressWarnings("serial")
    final Long2IntLinkedOpenHashMap map = new Long2IntLinkedOpenHashMap(50)
    {
        {
            defaultReturnValue(Integer.MAX_VALUE);
        }
        @Override
        protected void rehash(int int_1)
        {
        }
    };
    
    int cachedBrightness(BlockState blockState, ExtendedBlockView world, BlockPos pos)
    {
        int result;
        long key = pos.asLong();
        
        result = map.get(key);
        if (result != Integer.MAX_VALUE)
            return result;

        result = blockState.getBlockBrightness(world, pos);
        // Mojang using hashmap as a cache, evicting oldest at size limit
        // No idea if fastutil implementation is performant when used this way
        if (map.size() == 50)
            map.removeFirstInt();

        map.put(key, result);

        return result;
    }

    private final BlockPos.Mutable lightPos = new BlockPos.Mutable();
    private final BlockPos.Mutable searchPos = new BlockPos.Mutable();
    
    // TODO: cache ao light levels with brightness?
    
    // TODO: we compute bounds during quad bake for shape analysis - worth serializing and re-using here?
    // If not, need to recompute before calling
    public void compute(ExtendedBlockView world, BlockState blockState, BlockPos pos, Direction side, float[] shapeBounds, 
            boolean useNeighborBrightness,
            boolean nonCubic)
    {
        lightPos.set(useNeighborBrightness ? pos.offset(side) : pos);
        
        NeighborData neighborData = NeighborData.getData(side);
        
        searchPos.set(lightPos).setOffset(neighborData.faces[0]);
        final int light0 = cachedBrightness(blockState, world, searchPos);
        final float ao0 = world.getBlockState(searchPos).getAmbientOcclusionLightLevel(world, searchPos);
        searchPos.set(lightPos).setOffset(neighborData.faces[1]);
        final int light1 = cachedBrightness(blockState, world, searchPos);
        final float ao1 = world.getBlockState(searchPos).getAmbientOcclusionLightLevel(world, searchPos);
        searchPos.set(lightPos).setOffset(neighborData.faces[2]);
        final int light2 = cachedBrightness(blockState, world, searchPos);
        final float ao2 = world.getBlockState(searchPos).getAmbientOcclusionLightLevel(world, searchPos);
        searchPos.set(lightPos).setOffset(neighborData.faces[3]);
        final int light3 = cachedBrightness(blockState, world, searchPos);
        final float ao3 = world.getBlockState(searchPos).getAmbientOcclusionLightLevel(world, searchPos);
        
        searchPos.set(lightPos).setOffset(neighborData.faces[0]).setOffset(side);
        final boolean isClear0 = world.getBlockState(searchPos).method_11581(world, searchPos) == 0;
        searchPos.set(lightPos).setOffset(neighborData.faces[1]).setOffset(side);
        final boolean isClear1 = world.getBlockState(searchPos).method_11581(world, searchPos) == 0;
        searchPos.set(lightPos).setOffset(neighborData.faces[2]).setOffset(side);
        final boolean isClear2 = world.getBlockState(searchPos).method_11581(world, searchPos) == 0;
        searchPos.set(lightPos).setOffset(neighborData.faces[3]).setOffset(side);
        final boolean isClear3 = world.getBlockState(searchPos).method_11581(world, searchPos) == 0;
        
        int light4;
        float ao4;
        if (!isClear2 && !isClear0)
        {
            ao4 = ao0;
            light4 = light0;
        }
        else
        {
            searchPos.set(lightPos).setOffset(neighborData.faces[0]).setOffset(neighborData.faces[2]);
            ao4 = world.getBlockState(searchPos).getAmbientOcclusionLightLevel(world, searchPos);
            light4 = cachedBrightness(blockState, world, searchPos);
        }

        int light5;
        float ao5;
        if (!isClear3 && !isClear0)
        {
            ao5 = ao0;
            light5 = light0;
        }
        else
        {
            searchPos.set(lightPos).setOffset(neighborData.faces[0]).setOffset(neighborData.faces[3]);
            ao5 = world.getBlockState(searchPos).getAmbientOcclusionLightLevel(world, searchPos);
            light5 = cachedBrightness(blockState, world, searchPos);
        }

        int light6;
        float ao6;
        if (!isClear2 && !isClear1)
        {
            ao6 = ao1;
            light6 = light1;
        }
        else
        {
            searchPos.set(lightPos).setOffset(neighborData.faces[1]).setOffset(neighborData.faces[2]);
            ao6 = world.getBlockState(searchPos).getAmbientOcclusionLightLevel(world, searchPos);
            light6 = cachedBrightness(blockState, world, searchPos);
        }

        int light7;
        float ao7;
        if (!isClear3 && !isClear1)
        {
            ao7 = ao1;
            light7 = light1;
        } 
        else
        {
            searchPos.set(lightPos).setOffset(neighborData.faces[1]).setOffset(neighborData.faces[3]);
            ao7 = world.getBlockState(searchPos).getAmbientOcclusionLightLevel(world, searchPos);
            light7 = cachedBrightness(blockState, world, searchPos);
        }

        // TODO: why look this up 2X? Didn't change the code yet. Am I missing something?
        // Also, why not use light pos?
        int lightCenter = cachedBrightness(blockState, world, pos);
        searchPos.set((Vec3i)pos).setOffset(side);
        if (useNeighborBrightness || !world.getBlockState(searchPos).method_11598(world, searchPos))
        {
            lightCenter = cachedBrightness(blockState, world, searchPos);
        }

        // TODO: This won't work for triangles and non-square quads.
        // Problem isn't logic below, but bounds it consumes assume a square, with simple min/max on 2 axes.
        
        float aoCenter = useNeighborBrightness 
                ? world.getBlockState(lightPos).getAmbientOcclusionLightLevel(world, lightPos) 
                : world.getBlockState(pos).getAmbientOcclusionLightLevel(world, pos);
        Translation translation = Translation.getTranslations(side);

        final float aoCorner0 = (ao3 + ao0 + ao5 + aoCenter) * 0.25F;
        final float aoCorner1 = (ao2 + ao0 + ao4 + aoCenter) * 0.25F;
        final float aoCorner2 = (ao2 + ao1 + ao6 + aoCenter) * 0.25F;
        final float aoCorner3 = (ao3 + ao1 + ao7 + aoCenter) * 0.25F;

        final int lightCorner0 = meanBrightness(light3, light0, light5, lightCenter);
        final int lightCorner1 = meanBrightness(light2, light0, light4, lightCenter);
        final int lightCorner2 = meanBrightness(light2, light1, light6, lightCenter);
        final int lightCorner3 = meanBrightness(light3, light1, light7, lightCenter);
        
        // TODO: Likely slow and also ugly  nonCubicWeight check seems redundant?
        if (nonCubic && neighborData.nonCubicWeight)
        {
            final float wa0 = shapeBounds[neighborData.a[0].shape] * shapeBounds[neighborData.a[1].shape];
            final float wa1 = shapeBounds[neighborData.a[2].shape] * shapeBounds[neighborData.a[3].shape];
            final float wa2 = shapeBounds[neighborData.a[4].shape] * shapeBounds[neighborData.a[5].shape];
            final float wa3 = shapeBounds[neighborData.a[6].shape] * shapeBounds[neighborData.a[7].shape];
            final float wb0 = shapeBounds[neighborData.b[0].shape] * shapeBounds[neighborData.b[1].shape];
            final float wb1 = shapeBounds[neighborData.b[2].shape] * shapeBounds[neighborData.b[3].shape];
            final float wb2 = shapeBounds[neighborData.b[4].shape] * shapeBounds[neighborData.b[5].shape];
            final float wb3 = shapeBounds[neighborData.b[6].shape] * shapeBounds[neighborData.b[7].shape];
            final float wc0 = shapeBounds[neighborData.c[0].shape] * shapeBounds[neighborData.c[1].shape];
            final float wc1 = shapeBounds[neighborData.c[2].shape] * shapeBounds[neighborData.c[3].shape];
            final float wc2 = shapeBounds[neighborData.c[4].shape] * shapeBounds[neighborData.c[5].shape];
            final float wc3 = shapeBounds[neighborData.c[6].shape] * shapeBounds[neighborData.c[7].shape];
            final float wd0 = shapeBounds[neighborData.d[0].shape] * shapeBounds[neighborData.d[1].shape];
            final float wd1 = shapeBounds[neighborData.d[2].shape] * shapeBounds[neighborData.d[3].shape];
            final float wd2 = shapeBounds[neighborData.d[4].shape] * shapeBounds[neighborData.d[5].shape];
            final float wd3 = shapeBounds[neighborData.d[6].shape] * shapeBounds[neighborData.d[7].shape];
            
            colorMultiplier[translation.firstCorner] = aoCorner0 * wa0 + aoCorner1 * wa1 + aoCorner2 * wa2 + aoCorner3 * wa3;
            colorMultiplier[translation.secondCorner] = aoCorner0 * wb0 + aoCorner1 * wb1 + aoCorner2 * wb2 + aoCorner3 * wb3;
            colorMultiplier[translation.thirdCorner] = aoCorner0 * wc0 + aoCorner1 * wc1 + aoCorner2 * wc2 + aoCorner3 * wc3;
            colorMultiplier[translation.fourthCorner] = aoCorner0 * wd0 + aoCorner1 * wd1 + aoCorner2 * wd2 + aoCorner3 * wd3;
            
            brightness[translation.firstCorner] = weightedMeanBrightness(lightCorner0, lightCorner1, lightCorner2, lightCorner3, wa0, wa1, wa2, wa3);
            brightness[translation.secondCorner] = weightedMeanBrightness(lightCorner0, lightCorner1, lightCorner2, lightCorner3, wb0, wb1, wb2, wb3);
            brightness[translation.thirdCorner] = weightedMeanBrightness(lightCorner0, lightCorner1, lightCorner2, lightCorner3, wc0, wc1, wc2, wc3);
            brightness[translation.fourthCorner] = weightedMeanBrightness(lightCorner0, lightCorner1, lightCorner2, lightCorner3, wd0, wd1, wd2, wd3);
        }
        else
        {
            brightness[translation.firstCorner] = lightCorner0;
            brightness[translation.secondCorner] = lightCorner1;
            brightness[translation.thirdCorner] = lightCorner2;
            brightness[translation.fourthCorner] = lightCorner3;
            
            colorMultiplier[translation.firstCorner] = aoCorner0;
            colorMultiplier[translation.secondCorner] = aoCorner1;
            colorMultiplier[translation.thirdCorner] = aoCorner2;
            colorMultiplier[translation.fourthCorner] = aoCorner3;
        }

    }

    private static int meanBrightness(int a, int b, int c, int d)
    {
        if (a == 0)
            a = d;

        if (b == 0)
            b = d;

        if (c == 0)
            c = d;

        // bitwise divide by 4, clamp to expected (positive) range
        return a + b + c + d >> 2 & 16711935;
    }

    private static int weightedMeanBrightness(int a, int b, int c, int d, float wa, float wb, float wc, float wd)
    {
        int sky = (int)((float)(a >> 16 & 255) * wa + (float)(b >> 16 & 255) * wb + (float)(c >> 16 & 255) * wc + (float)(d >> 16 & 255) * wd) & 255;
        int block = (int)((float)(a & 255) * wa + (float)(b & 255) * wb + (float)(c & 255) * wc + (float)(d & 255) * wd) & 255;
        return sky << 16 | block;
    }
}
