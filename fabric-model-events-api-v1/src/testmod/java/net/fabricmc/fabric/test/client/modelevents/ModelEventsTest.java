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

package net.fabricmc.fabric.test.client.modelevents;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.StreamSupport;

import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.modelevents.v1.ModelPartCallbacks;
import net.fabricmc.fabric.api.client.modelevents.v1.PartTreePath;
import net.fabricmc.fabric.api.client.modelevents.v1.traversal.ModelVisitor;
import net.fabricmc.fabric.impl.client.modelevents.ModelPartCallbacksImpl;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.entity.EntityType;

public final class ModelEventsTest implements ClientModInitializer {
	private static final Logger LOGGER = LoggerFactory.getLogger(ModelEventsTest.class);

	private static final ModelVisitor DEBUG_VISITOR = ModelVisitor.builder()
            .visitCubes((matrices, cube) -> {
                VertexConsumer debugConsumer = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers().getBuffer(RenderLayer.getLines());
                WorldRenderer.drawBox(matrices, debugConsumer, 0, 0, 0, cube.sizeX() / 16F, cube.sizeY() / 16F, cube.sizeZ() / 16F, 1, 1, 0, 1);
                return true;
            })
            .visitFaces((matrices, face) -> {
                VertexConsumer debugConsumer = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers().getBuffer(RenderLayer.getLines());
                matrices.push();

                Vector3fc center = face.center();
                float cubeL = 0.01F;
                matrices.translate(center.x() / 16F, center.y() / 16F, center.z() / 16F);
                Vector3f unitVector = face.direction().getUnitVector();
                WorldRenderer.drawBox(matrices, debugConsumer, -cubeL, -cubeL, -cubeL, cubeL, cubeL, cubeL,
                        0.5F + unitVector.x * 0.5F,
                        0.5F + unitVector.y * 0.5F,
                        0.5F + unitVector.z * 0.5F,
                        1);
                matrices.translate(unitVector.x() / 16F, unitVector.y() / 16F, unitVector.z() / 16F);
                WorldRenderer.drawBox(matrices, debugConsumer, -cubeL, -cubeL, -cubeL, cubeL, cubeL, cubeL,
                        0.5F + unitVector.x * 0.5F,
                        0.5F + unitVector.y * 0.5F,
                        0.5F + unitVector.z * 0.5F,
                        1);
                matrices.pop();
                return true;
            }).build();

	@Override
	public void onInitializeClient() {
	    List<AssertionError> errors = new ArrayList<>();

	    test(errors, () -> {
	        assert PartTreePath.of().isRoot();
	    });
	    test(errors, ModelEventsTest::checkEmptyPartPathCreation);
	    test(errors, ModelEventsTest::checkPartPathParsing);
	    test(errors, ModelEventsTest::checkPathIndexOfForShortPath);
	    test(errors, ModelEventsTest::checkPathIndexOfForLongPath);
	    test(errors, ModelEventsTest::checkPathComparisons);
        test(errors, ModelEventsTest::registerEntityModelPartListener);
        test(errors, ModelEventsTest::registerBlockEntityModelPartListener);
        test(errors, ModelEventsTest::testModelRenderNesting);

        if (errors.isEmpty()) {
            LOGGER.info("Model Events tests passed!");
        } else {
            AssertionError error = new AssertionError("Model Events tests failed!");
            errors.forEach(error::addSuppressed);
            throw error;
        }
	}


	private static void test(List<AssertionError> errors, Runnable runnable) {
		try {
			runnable.run();
		} catch (AssertionError e) {
			errors.add(e);
		}
	}

	static void checkEmptyPartPathCreation() {
	    // all of these should return the same thing (don't change this to .equals()!)
	    assert PartTreePath.of() == PartTreePath.of("");
	    assert PartTreePath.of() == PartTreePath.of(" ");
	    assert PartTreePath.of() == PartTreePath.of(null);
	    assert PartTreePath.of() == PartTreePath.of("/");
	    // check stripping of the leading '/'
	    assert "a/b/c".contentEquals(PartTreePath.of("/a/b/c").toString());
	}

	static void checkPartPathParsing() {
	    assert !PartTreePath.of("a/b/c").isRoot();
	    // should be the same
	    assert Arrays.equals(new String[] {"a", "b", "c"}, StreamSupport.stream(PartTreePath.of("a/b/c").spliterator(), false).toArray(String[]::new));
	}

	static void checkPathIndexOfForShortPath() {
	    PartTreePath path = PartTreePath.of("a/b/c");
        assert path.indexOf(PartTreePath.of("a")) == 0;
        assert path.indexOf(PartTreePath.of("b")) == 1;
        assert path.indexOf(PartTreePath.of("c")) == 2;
        assert path.indexOf(PartTreePath.of("d")) == -1;
    }

	static void checkPathIndexOfForLongPath() {
	    PartTreePath path = PartTreePath.of("aaaaaaaaaa/bbbbbbbbbb/cccccccccccc/dddddddddd");
        assert path.indexOf(PartTreePath.of("aaaaaaaaaa/bbbbbbbbbb/cccccccccccc")) == 0;
        assert path.indexOf(PartTreePath.of("bbbbbbbbbb/cccccccccccc")) == 1;
        assert path.indexOf(PartTreePath.of("cccccccccccc/dddddddddd")) == 2;
        assert path.indexOf(PartTreePath.of("dddddddddd/eeeeeeeeeeee")) == -1;
    }

	static void checkPathComparisons() {
	    PartTreePath path = PartTreePath.of("beginning/middle/end");
	    assert path.beginsWith(PartTreePath.of("beginning"));
	    assert path.endsWith(PartTreePath.of("end"));
	    assert path.includes(PartTreePath.of("middle"));
	    assert !path.beginsWith(PartTreePath.of("middle"));
	    assert !path.endsWith(PartTreePath.of("middle"));
	}

	static void registerEntityModelPartListener() {
	    assertListenerCount(PartTreePath.of(EntityModelPartNames.HAT), path -> {
	        ModelPartCallbacks.get(path).register(EntityType.PLAYER, (player, partView, matrices, vertexConsumer, tickDelta, light, overlay, r, g, b, a) -> {
	            if (MinecraftClient.getInstance().player == player) {
	                ModelPart part = partView.part();
	                LOGGER.info("Head rotation: p=" + part.pitch + ",y=" + part.yaw + ",r=" + part.roll);
	                LOGGER.info("Head Cube Count: " + partView.cubes().size());
	            }
	        });
	    }, 1);
	}

	static void registerBlockEntityModelPartListener() {
	    assertListenerCount(PartTreePath.of("lid"), path -> {
	        ModelPartCallbacks.get(path).register(BlockEntityType.CHEST, (chest, partView, matrices, vertexConsumer, tickDelta, light, overlay, r, g, b, a) -> {
                ModelPart part = partView.part();
                LOGGER.info("Chest Lid rotation: p=" + part.pitch + ",y=" + part.yaw + ",r=" + part.roll);
                LOGGER.info("Chest Lid Cube Count: " + partView.cubes().size());
                LOGGER.info("Chest Lid Has Latch: " + partView.getChild("latch").isPresent());
                partView.root().traverse(matrices, DEBUG_VISITOR);
            });
	    }, 1);
	}

	/**
	 * Test for event listeners that render a model matching an existing event. This is to demonstrate (and test) that code
	 * which does this does not result in any infinite recursion scenarios.
	 * <p>
	 * Simply-put, if an event is already active, it will not fire until after it returns.
	 * <p>
	 * Other events can still be fired in response to models you render here, however.
	 */
	static void testModelRenderNesting() {
	    assertListenerCount(PartTreePath.of(EntityModelPartNames.HEAD), path -> {
	        // if the player has two heads we know it's working
            ModelPartCallbacks.get(path).register(EntityType.PLAYER, (player, partView, matrices, vertexConsumer, tickDelta, light, overlay, r, g, b, a) -> {
                partView.part().pivotX = -5;
                partView.part().render(matrices, vertexConsumer, light, overlay, r, g, b, a);
                partView.part().pivotX = 4;
            });
        }, 1);
	}

	static void assertListenerCount(PartTreePath path, Consumer<PartTreePath> registerAction, int expectedCountAfter) {
	    registerAction.accept(path);
        List<ModelPartCallbacksImpl> listeners = new ArrayList<>();
        ModelPartCallbacksImpl.INSTANCES.findMatchingLeafNodes(path, listeners::add);
	    assert listeners.size() == expectedCountAfter;
	}
}
