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

import net.minecraft.client.render.WorldRenderer;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/**
 * Mods should use these events to introduce custom rendering during {@link WorldRenderer#render(net.minecraft.client.util.math.MatrixStack, float, long, boolean, net.minecraft.client.render.Camera, net.minecraft.client.render.GameRenderer, net.minecraft.client.render.LightmapTextureManager, net.minecraft.util.math.Matrix4f)}
 * without adding complicated and conflict-prone injections there.  Using these events also enables 3rd-party renderers
 * that make large-scale changes to rendering maintain compatibility by calling any broken even invokers directly.
 *
 * <p>These events are not dependent on the Fabric rendering API or Indigo but work when those are present.
 */
public final class WorldRenderEvents {
	private WorldRenderEvents() { }

	/**
	 * Called before world rendering executes. Input parameters are available but frustum is not.
	 * Use this event instead of injecting to the HEAD of {@link WorldRenderer#render} to avoid
	 * compatibility problems with 3rd-party renderer implementations.
	 *
	 * <p>Use for setup of state that is needed during the world render call that
	 * does not depend on the view frustum.
	 */
	public static final Event<Start> START = EventFactory.createArrayBacked(Start.class, callbacks -> context -> {
		for (final Start callback : callbacks) {
			callback.onStart(context);
		}
	});

	/**
	 * Called after view Frustum is computed and all render chunks to be rendered are
	 * identified and rebuilt but before chunks are uploaded to GPU.
	 *
	 * <p>Use for setup of state that depends on view frustum.
	 */
	public static final Event<AfterSetup> AFTER_SETUP = EventFactory.createArrayBacked(AfterSetup.class, callbacks -> context -> {
		for (final AfterSetup callback : callbacks) {
			callback.afterSetup(context);
		}
	});

	/**
	 * Called after the Solid, Cutout and Cutout Mipped terrain layers have been output to the framebuffer.
	 *
	 * <p>Use to render non-translucent terrain to the framebuffer.
	 *
	 * <p>Note that 3rd-party renderers may combine these passes or otherwise alter the
	 * rendering pipeline for sake of performance or features. This can break direct writes to the
	 * framebuffer.  Use this event for cases that cannot be satisfied by FabricBakedModel,
	 * BlockEntityRenderer or other existing abstraction. If at all possible, use an existing terrain
	 * RenderLayer instead of outputting to the framebuffer directly with GL calls.
	 *
	 * <p>The consumer is responsible for setup and tear down of GL state appropriate for the intended output.
	 *
	 * <p>Because solid and cutout quads are depth-tested, order of output does not matter except to improve
	 * culling performance, which should not be significant after primary terrain rendering. This means
	 * mods that currently hook calls to individual render layers can simply execute them all at once when
	 * the event is called.
	 *
	 * <p>This event fires before entities and block entities are rendered and may be useful to prepare them.
	 */
	public static final Event<BeforeEntities> BEFORE_ENTITIES = EventFactory.createArrayBacked(BeforeEntities.class, callbacks -> context -> {
		for (final BeforeEntities callback : callbacks) {
			callback.beforeEntities(context);
		}
	});

	/**
	 * Called after entities are rendered and solid entity layers
	 * have been drawn to the main frame buffer target, before
	 * block entity rendering begins.
	 *
	 * <p>Use for global block entity render setup, or
	 * to append block-related quads to the entity consumers using the
	 * {@VertexConsumerProvider} from the provided context. This
	 * will generally give better (if not perfect) results
	 * for non-terrain translucency vs. drawing directly later on.
	 */
	public static final Event<AfterEntities> AFTER_ENTITIES = EventFactory.createArrayBacked(AfterEntities.class, callbacks -> context -> {
		for (final AfterEntities callback : callbacks) {
			callback.afterEntities(context);
		}
	});

	/**
	 * Called before default block outline rendering and before checks are
	 * done to determine if it should happen. Can optionally cancel the default
	 * rendering but all event handlers will always be called.
	 *
	 * <p>Use this to decorate or replace the default block outline rendering
	 * for specific modded blocks or when the need for a block outline render
	 * would not be detected.  Normally, outline rendering will not happen for
	 * entities or other game objects that do not register a block-type hit.
	 *
	 * <p>Canceling the default block outline render has no effect on other
	 * event subscribers - all subscribers will always be called. Generally, modded
	 * block outline renders are specific to that mod's content or additive
	 * and thus should not interfere with each other.
	 *
	 * <p>This event should NOT be used for general-purpose replacement of
	 * the default block outline rendering because it will interfere with mod-specific
	 * renders.  Mods that replace the default block outline should instead
	 * subscribe to {@link #BLOCK_OUTLINE}.
	 */
	public static final Event<BeforeBlockOutline> BEFORE_BLOCK_OUTLINE = EventFactory.createArrayBacked(BeforeBlockOutline.class, callbacks -> context -> {
		for (final BeforeBlockOutline callback : callbacks) {
			callback.beforeBlockOutline(context);
		}
	});

	/**
	 * Called after block outline render checks are made and before the
	 * default block outline render runs.  Will NOT be called if the default outline
	 * render was cancelled in {@link #BEFORE_BLOCK_OUTLINE}.
	 *
	 * <p>Use this to replace the default block outline rendering entirely.
	 * Canceling the default outline render here avoids interfering
	 * with mod-specific outline renders that cannot be well-handled by a
	 * general-purpose outline renderer.
	 *
	 * <p>Default block outline rendering does not have to happen here.  If a custom
	 * default outline effect benefits from being drawn at a later stage it can
	 * be cancelled here and then drawn in {@link #AFTER_TRANSLUCENT} or {@link #LAST}
	 * if desired. To facilitate this pattern, those events can check
	 * {@link WorldRenderContext.AfterBlockOutline#didCancelDefaultBlockOutline()}.
	 */
	public static final Event<BlockOutline> BLOCK_OUTLINE = EventFactory.createArrayBacked(BlockOutline.class, callbacks -> context -> {
		for (final BlockOutline callback : callbacks) {
			callback.onBlockOutline(context);
		}
	});

	/**
	 * Called before vanilla debug renderers are output to the framebuffer.
	 * This happens very soon after entities, block breaking and most other
	 * non-translucent renders but before translucency is drawn.
	 *
	 * <p>Unlike most other events, renders in this event are expected to be drawn
	 * directly and immediately to the framebuffer. The OpenGL render state view
	 * matrix will be transformed to match the camera view before the event is called.
	 *
	 * <p>Use to drawn lines, overlays and other content similar to vanilla
	 * debug renders.
	 */
	public static final Event<DebugRender> BEFORE_DEBUG_RENDER = EventFactory.createArrayBacked(DebugRender.class, callbacks -> context -> {
		for (final DebugRender callback : callbacks) {
			callback.beforeDebugRender(context);
		}
	});

	/**
	 * Called after entity, terrain, and particle translucent layers have been
	 * drawn to the framebuffer but before translucency combine has happened
	 * in fabulous mode.
	 *
	 * <p>Use for drawing overlays or other effects on top of those targets
	 * (or the main target when fabulous isn't active) before clouds and weather
	 * are drawn.  However, note that {@code WorldRenderPostEntityCallback} will
	 * offer better results in most use cases.
	 *
	 * <p>Vertex consumers are not available in this event because all buffered quads
	 * are drawn before this event is called.  Any rendering here must be drawn
	 * directly to the frame buffer.  The render state matrix will not include
	 * camera transformation, so {@link #LAST} may be preferable if that is wanted.
	 */
	public static final Event<AfterTranslucent> AFTER_TRANSLUCENT = EventFactory.createArrayBacked(AfterTranslucent.class, callbacks -> context -> {
		for (final AfterTranslucent callback : callbacks) {
			callback.afterTranslucent(context);
		}
	});

	/**
	 * Called after all framebuffer writes are complete but before all world
	 * rendering is torn down.
	 *
	 * <p>Unlike most other events, renders in this event are expected to be drawn
	 * directly and immediately to the framebuffer. The OpenGL render state view
	 * matrix will be transformed to match the camera view before the event is called.
	 *
	 * <p>Use to draw content that should appear on top of the world before hand and GUI rendering occur.
	 */
	public static final Event<Last> LAST = EventFactory.createArrayBacked(Last.class, callbacks -> context -> {
		for (final Last callback : callbacks) {
			callback.onLast(context);
		}
	});

	/**
	 * Called after all world rendering is complete and changes to GL state are unwound.
	 *
	 * <p>Use to draw overlays that handle GL state management independently or to tear
	 * down transient state in event handlers or as a hook that precedes hand/held item
	 * and GUI rendering.
	 */
	public static final Event<End> END = EventFactory.createArrayBacked(End.class, callbacks -> context -> {
		for (final End callback : callbacks) {
			callback.onEnd(context);
		}
	});

	@FunctionalInterface
	public interface Start {
		void onStart(WorldRenderContext context);
	}

	@FunctionalInterface
	public interface AfterSetup {
		void afterSetup(WorldRenderContext.FrustumContext context);
	}

	@FunctionalInterface
	public interface BeforeEntities {
		void beforeEntities(WorldRenderContext.MainContext context);
	}

	@FunctionalInterface
	public interface AfterEntities {
		void afterEntities(WorldRenderContext.MainContext context);
	}

	@FunctionalInterface
	public interface BeforeBlockOutline {
		void beforeBlockOutline(WorldRenderContext.PreBlockOutlineContext context);
	}

	@FunctionalInterface
	public interface BlockOutline {
		void onBlockOutline(WorldRenderContext.BlockOutlineContext context);
	}

	@FunctionalInterface
	public interface DebugRender {
		void beforeDebugRender(WorldRenderContext.PostBlockOutlineContext context);
	}

	@FunctionalInterface
	public interface AfterTranslucent {
		void afterTranslucent(WorldRenderContext.LateContext context);
	}

	@FunctionalInterface
	public interface Last {
		void onLast(WorldRenderContext.LateContext context);
	}

	@FunctionalInterface
	public interface End {
		void onEnd(WorldRenderContext.LateContext context);
	}
}
