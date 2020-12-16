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

package net.fabricmc.fabric.impl.client.rendering;

import net.minecraft.block.BlockState;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.profiler.Profiler;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;

@Environment(EnvType.CLIENT)
public final class WorldRenderContextImpl implements WorldRenderContext.BlockOutlineContext, WorldRenderContext {
	private WorldRenderer worldRenderer;
	private MatrixStack matrixStack;
	private float tickDelta;
	private long limitTime;
	private boolean blockOutlines;
	private Camera camera;
	private Frustum frustum;
	private GameRenderer gameRenderer;
	private LightmapTextureManager lightmapTextureManager;
	private Matrix4f projectionMatrix;
	private VertexConsumerProvider consumers;
	private Profiler profiler;
	private boolean advancedTranslucency;
	private ClientWorld world;

	private VertexConsumer vertexConsumer;
	private Entity entity;
	private double cameraX;
	private double cameraY;
	private double cameraZ;
	private BlockPos blockPos;
	private BlockState blockState;

	public boolean renderBlockOutline = true;

	public void prepare(
			WorldRenderer worldRenderer,
			MatrixStack matrixStack,
			float tickDelta,
			long limitTime,
			boolean blockOutlines,
			Camera camera,
			GameRenderer gameRenderer,
			LightmapTextureManager lightmapTextureManager,
			Matrix4f projectionMatrix,
			VertexConsumerProvider consumers,
			Profiler profiler,
			boolean advancedTranslucency,
			ClientWorld world
	) {
		this.worldRenderer = worldRenderer;
		this.matrixStack = matrixStack;
		this.tickDelta = tickDelta;
		this.limitTime = limitTime;
		this.blockOutlines = blockOutlines;
		this.camera = camera;
		this.gameRenderer = gameRenderer;
		this.lightmapTextureManager = lightmapTextureManager;
		this.projectionMatrix = projectionMatrix;
		this.consumers = consumers;
		this.profiler = profiler;
		this.advancedTranslucency = advancedTranslucency;
		this.world = world;
	}

	public void setFrustum(Frustum frustum) {
		this.frustum = frustum;
	}

	public void prepareBlockOutline(
			VertexConsumer vertexConsumer,
			Entity entity,
			double cameraX,
			double cameraY,
			double cameraZ,
			BlockPos blockPos,
			BlockState blockState
	) {
		this.vertexConsumer = vertexConsumer;
		this.entity = entity;
		this.cameraX = cameraX;
		this.cameraY = cameraY;
		this.cameraZ = cameraZ;
		this.blockPos = blockPos;
		this.blockState = blockState;
	}

	@Override
	public WorldRenderer worldRenderer() {
		return worldRenderer;
	}

	@Override
	public MatrixStack matrixStack() {
		return matrixStack;
	}

	@Override
	public float tickDelta() {
		return tickDelta;
	}

	@Override
	public long limitTime() {
		return limitTime;
	}

	@Override
	public boolean blockOutlines() {
		return blockOutlines;
	}

	@Override
	public Camera camera() {
		return camera;
	}

	@Override
	public Matrix4f projectionMatrix() {
		return projectionMatrix;
	}

	@Override
	public ClientWorld world() {
		return world;
	}

	@Override
	public Frustum frustum() {
		return frustum;
	}

	@Override
	public VertexConsumerProvider consumers() {
		return consumers;
	}

	@Override
	public GameRenderer gameRenderer() {
		return gameRenderer;
	}

	@Override
	public LightmapTextureManager lightmapTextureManager() {
		return lightmapTextureManager;
	}

	@Override
	public Profiler profiler() {
		return profiler;
	}

	@Override
	public boolean advancedTranslucency() {
		return advancedTranslucency;
	}

	@Override
	public VertexConsumer vertexConsumer() {
		return vertexConsumer;
	}

	@Override
	public Entity entity() {
		return entity;
	}

	@Override
	public double cameraX() {
		return cameraX;
	}

	@Override
	public double cameraY() {
		return cameraY;
	}

	@Override
	public double cameraZ() {
		return cameraZ;
	}

	@Override
	public BlockPos blockPos() {
		return blockPos;
	}

	@Override
	public BlockState blockState() {
		return blockState;
	}
}
