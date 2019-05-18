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

package net.fabricmc.indigo.renderer.helper;

import static net.minecraft.util.math.MathHelper.equalsApproximate;

import net.fabricmc.fabric.api.renderer.v1.mesh.QuadView;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.math.Direction.AxisDirection;

/**
 * Static routines of general utility for renderer implementations.
 * Renderers are not required to use these helpers, but they were
 * designed to be usable without the default renderer.
 */
public abstract class GeometryHelper {
    /** set when a quad touches all four corners of a unit cube */
    public static final int CUBIC_FLAG = 1;
    
    /** set when a quad is parallel to (but not necessarily on) a its light face */
    public static final int AXIS_ALIGNED_FLAG = CUBIC_FLAG << 1;
    
    /** set when a quad is coplanar with its light face. Implies {@link #AXIS_ALIGNED_FLAG} */
    public static final int LIGHT_FACE_FLAG = AXIS_ALIGNED_FLAG << 1;
    
    private GeometryHelper() {}

    /**
     * Analyzes the quad and returns a value with some combination 
     * of {@link #AXIS_ALIGNED_FLAG}, {@link #LIGHT_FACE_FLAG} and {@link #CUBIC_FLAG}.
     * Intended use is to optimize lighting when the geometry is regular.
     * Expects convex quads with all points co-planar.
     */
    public static int computeShapeFlags(QuadView quad) {
        Direction lightFace = quad.lightFace();
        int bits = 0;
        if(isQuadParallelToFace(lightFace, quad)) {
            bits |= AXIS_ALIGNED_FLAG;
            if(isParallelQuadOnFace(lightFace, quad)) {
                bits |= LIGHT_FACE_FLAG;
            }
            if(isQuadCubic(lightFace, quad)) {
                bits |= CUBIC_FLAG;
            }
        }
        return bits;
    }
    
    /**
     * Returns true if quad is parallel to the given face.
     * Does not validate quad winding order.
     * Expects convex quads with all points co-planar.
     */
    public static boolean isQuadParallelToFace(Direction face, QuadView quad) {
        if(face == null) {
            return false;
        }
        int i = face.getAxis().ordinal();
        final float val = quad.posByIndex(0, i);
        return equalsApproximate(val, quad.posByIndex(1, i))
                && equalsApproximate(val, quad.posByIndex(2, i))
                && equalsApproximate(val, quad.posByIndex(3, i));
    }
    
    /**
     * True if quad - already known to be parallel to a face - is actually coplanar with it.<p>
     * 
     * Test will be unreliable if not already parallel, use {@link #isQuadParallel(Direction, QuadView)}
     * for that purpose. Expects convex quads with all points co-planar.<p>
     */
    public static boolean isParallelQuadOnFace(Direction lightFace, QuadView quad) {
        if(lightFace == null)
            return false;
        final int coordinateIndex = lightFace.getAxis().ordinal();
        final float expectedValue = lightFace.getDirection() == AxisDirection.POSITIVE ? 1 : 0;
        return equalsApproximate(quad.posByIndex(0, coordinateIndex), expectedValue);
    }
    
    /**
     * Returns true if quad is truly a quad (not a triangle) and fills a full block cross-section.
     * If known to be true, allows use of a simpler/faster AO lighting algorithm.<p>
     * 
     * Does not check if quad is actually coplanar with the light face, nor does it check that all
     * quad vertices are coplanar with each other. <p>
     * 
     * Expects convex quads with all points co-planar.<p>
     * 
     * @param lightFace MUST be non-null.
     */
    public static boolean isQuadCubic(Direction lightFace, QuadView quad) {
        if(lightFace == null) {
            return false;
        }
        
        int a, b;

        switch(lightFace) {
        case EAST: 
        case WEST:
            a = 1;
            b = 2;
            break;
        case UP:
        case DOWN:
            a = 0;
            b = 2;
            break;
        case SOUTH:
        case NORTH:
            a = 1;
            b = 0;
            break;
        default:
            // handle WTF case
            return false;
        }
       
        return confirmSquareCorners(a, b, quad);
    }
    
    /**
     * Used by {@link #isQuadCubic(Direction, int[], int, QuadSerializer)}.
     * True if quad touches all four corners of unit square.
     */
    private static boolean confirmSquareCorners(int aCoordinate, int bCoordinate, QuadView quad) {
        int flags = 0;
        
        for(int i = 0; i < 4; i++) {
            final float a = quad.posByIndex(i, aCoordinate);
            final float b = quad.posByIndex(i, bCoordinate);
            
            if(equalsApproximate(a, 0)) {
                if(equalsApproximate(b, 0)) {
                    flags |= 1;
                } else if(equalsApproximate(b, 1)) {
                    flags |= 2;
                } else {
                    return false;
                }
            } else if(equalsApproximate(a, 1)) {
                if(equalsApproximate(b, 0)) {
                    flags |= 4;
                } else if(equalsApproximate(b, 1)) {
                    flags |= 8;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
        return flags == 15;
    }
    
    /**
     * Identifies the face to which the quad is most closely aligned.
     * This mimics the value that {@link BakedQuad#getFace()} returns, and is
     * used in the vanilla renderer for all diffuse lighting.<p>
     * 
     * Derived from the quad face normal and expects convex quads with all points co-planar.
     */
    public static Direction lightFace(QuadView quad) {
        final Vector3f normal = quad.faceNormal();
        switch(GeometryHelper.longestAxis(normal)) {
            case X:
                return normal.x() > 0 ? Direction.EAST : Direction.WEST;
                
            case Y:
                return normal.y() > 0 ? Direction.UP : Direction.DOWN;
                
            case Z:
                return normal.z() > 0 ? Direction.SOUTH : Direction.NORTH;
            
            default:
                // handle WTF case
                return Direction.UP;
        }
    }

    /**
     * Simple 4-way compare, doesn't handle NaN values.
     */
    public static float min(float a, float b, float c, float d) {
        final float x = a < b ? a : b;
        final float y = c < d ? c : d;
        return x < y ? x : y;
    }

    /**
     * Simple 4-way compare, doesn't handle NaN values.
     */
    public static float max(float a, float b, float c, float d) {
        final float x = a > b ? a : b;
        final float y = c > d ? c : d;
        return x > y ? x : y;
    }
    
    /**
     * See {@link #longestAxis(float, float, float)}
     */
    public static Axis longestAxis(Vector3f vec) {
        return longestAxis(vec.x(), vec.y(), vec.z());
    }
    
    /**
     * Identifies the largest (max absolute magnitude) component (X, Y, Z) in the given vector.
     */
    public static Axis longestAxis(float normalX, float normalY, float normalZ) {
        Axis result = Axis.Y;
        float longest = Math.abs(normalY);
    
        float a = Math.abs(normalX);
        if(a > longest)
        {
            result = Axis.X;
            longest = a;
        }
    
        return Math.abs(normalZ) > longest
                ? Axis.Z : result;
    }
}
