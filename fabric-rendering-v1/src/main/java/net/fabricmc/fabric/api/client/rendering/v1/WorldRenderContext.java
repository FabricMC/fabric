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

package net.fabricmc.fabric.api.client.rendering.v1;

import org.jetbrains.annotations.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.profiler.Profiler;

/**
 * Except as noted below, the properties exposed here match the parameters passed to
 * {@link WorldRenderer#render(MatrixStack, float, long, boolean, Camera, GameRenderer, LightmapTextureManager, Matrix4f)}.
 */
public interface WorldRenderContext {
	/**
	 * The world renderer instance doing the rendering and invoking the event.
	 *
	 * @return WorldRenderer instance invoking the event
	 */
	WorldRenderer worldRenderer();

	MatrixStack matrixStack();

	float tickDelta();

	long limitTime();

	boolean blockOutlines();

	Camera camera();

	GameRenderer gameRenderer();

	LightmapTextureManager lightmapTextureManager();

	Matrix4f projectionMatrix();

	/**
	 * Convenient access to {WorldRenderer.world}.
	 *
	 * @return world renderer's client world instance
	 */
	ClientWorld world();

	/**
	 * Convenient access to game performance profiler.
	 *
	 * @return the active profiler
	 */
	Profiler profiler();

	/**
	 * Test to know if "fabulous" graphics mode is enabled.
	 *
	 * <p>Use this for renders that need to render on top of all translucency to activate or deactivate different
	 * event handlers to get optimal depth testing results. When fabulous is off, it may be better to render
	 * during {@code WorldRenderLastCallback} after clouds and weather are drawn. Conversely, when fabulous mode is on,
	 * it may be better to draw during {@code WorldRenderPostTranslucentCallback}, before the fabulous mode composite
	 * shader runs, depending on which translucent buffer is being targeted.
	 *
	 * @return {@code true} when "fabulous" graphics mode is enabled.
	 */
	boolean advancedTranslucency();

	public interface Drawable {
		/**
		 * The {@code VertexConsumerProvider} instance being used by the world renderer for most non-terrain renders.
		 * Generally this will be better for most use cases because quads for the same layer can be buffered
		 * incrementally and then drawn all at once by the world renderer.
		 *
		 * <p>IMPORTANT - all vertex coordinates sent to consumers should be relative to the camera to
		 * be consistent with other quads emitted by the world renderer and other mods.  If this isn't
		 * possible, caller should use a separate "immediate" instance.
		 *
		 * <p>This property is not exposed before {@link WorldRenderEvents#BEFORE_ENTITIES} or after
		 * {@link WorldRenderEvents#BEFORE_DEBUG_RENDER} because the consumer buffers are not available before or
		 * drawn after that in vanilla world rendering.  Renders that cannot draw in one of the supported events
		 * must be drawn directly to the frame buffer, preferably in {@link WorldRenderEvents#LAST} to avoid being
		 * overdrawn or cleared.
		 */
		VertexConsumerProvider consumers();
	}

	/**
	 * Conveys WorldRenderer state to event handlers after invocation.
	 * Includes view frustum, which is not available then.
	 */
	public interface AfterFrustum {
		Frustum frustum();
	}

	/**
	 * Used in the block breaking callback to convey information on
	 * ray trace hit and accept cancellation of default outline render.
	 */
	public interface PreBlockOutline {
		/**
		 * The game object currently under the crosshair target. Normally
		 * equivalent to {@link MinecraftClient#crosshairTarget}.
		 * Provided for convenience.
		 */
		@Nullable HitResult hitResult();

		/**
		 * Call to prevent the default block outline from rendering.
		 * Use this for event handlers that fully replace the vanilla
		 * block outline render.
		 *
		 * <p>The effect of this method depends on the event from which it is called:
		 *
		 * <p>In {@code BEFORE_BLOCK_RENDER} this prevents invocation of
		 * the {@code BLOCK_RENDER} event and {@link AfterBlockOutline#didCancelDefaultBlockOutline()}
		 * will be {@code false} in later-stage event invocations. It (obviously)
		 * also prevents the default outline render from running.
		 *
		 * <p>In {@code BLOCK_RENDER} this prevents the default outline render
		 * from running and {@link AfterBlockOutline#didCancelDefaultBlockOutline()}
		 * will be {@code true} in later-stage event invocations.
		 *
		 * <p>In both cases, this method is idempotent - calling it from more
		 * than one event subscriber has the same effect as calling it once.
		 * All event handlers within the same event invocation will always
		 * execute and the cancellation status cannot be inspected during the
		 * block outline render events.
		 */
		void cancelDefaultBlockOutline();
	}

	/**
	 * Used in {@code BLOCK_OUTLINE} to convey the parameters normally sent to
	 * {@code WorldRenderer.drawBlockOutline}.
	 */
	public interface BlockOutline {
		VertexConsumer vertexConsumer();

		Entity entity();

		double cameraX();

		double cameraY();

		double cameraZ();

		BlockPos blockPos();

		BlockState blockState();
	}

	/**
	 * Used after the block outline events to test if {@link #cancelDefaultBlockOutline()}
	 * was called during the {@code BLOCK_OUTLINE} event.
	 */
	public interface AfterBlockOutline {
		/**
		 * Tests if the default block outline render was cancelled in the {@code BLOCK_OUTLINE} event.
		 *
		 * <p>Will NOT be true if the outline was cancelled during {@code BEFORE_BLOCK_OUTLINE}.
		 *
		 * @return {@code true} if the default block outline render was cancelled in the {@code BLOCK_OUTLINE} event.
		 */
		boolean didCancelDefaultBlockOutline();
	}

	public interface FrustumContext extends WorldRenderContext, AfterFrustum { }

	public interface MainContext extends FrustumContext, Drawable { }

	public interface PreBlockOutlineContext extends MainContext, PreBlockOutline { }

	public interface BlockOutlineContext extends PreBlockOutlineContext, BlockOutline { }

	public interface PostBlockOutlineContext extends MainContext, AfterBlockOutline { }

	public interface LateContext extends FrustumContext, AfterBlockOutline { }
}
