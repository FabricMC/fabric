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

package net.fabricmc.fabric.test.attachment;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.mock;

import java.util.function.UnaryOperator;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import net.minecraft.Bootstrap;
import net.minecraft.SharedConstants;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.chunk.WorldChunk;

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;

public class CommonAttachmentTests {
	private static final String MOD_ID = "example";

	@BeforeAll
	static void beforeAll() {
		SharedConstants.createGameVersion();
		Bootstrap.initialize();
	}

	@Test
	void testTargets() {
		AttachmentType<String> basic = AttachmentRegistry.create(new Identifier(MOD_ID, "basic_attachment"));
		// Attachment targets
		/*
		 * CALLS_REAL_METHODS makes sense here because AttachmentTarget does not refer to anything in the underlying
		 * class, and it saves us a lot of pain trying to get the regular constructors for ServerWorld and WorldChunk to work.
		 */
		ServerWorld serverWorld = mock(ServerWorld.class, CALLS_REAL_METHODS);
		Entity entity = mock(Entity.class, CALLS_REAL_METHODS);
		BlockEntity blockEntity = mock(BlockEntity.class, CALLS_REAL_METHODS);
		WorldChunk worldChunk = mock(WorldChunk.class, CALLS_REAL_METHODS);

		for (AttachmentTarget target : new AttachmentTarget[]{serverWorld, entity, blockEntity, worldChunk}) {
			testForTarget(target, basic);
		}
	}

	private void testForTarget(AttachmentTarget target, AttachmentType<String> basic) {
		assertFalse(target.hasAttached(basic));
		assertEquals("", target.getAttachedOrElse(basic, ""));
		assertNull(target.getAttached(basic));

		String value = "attached";
		assertEquals(value, target.getAttachedOrSet(basic, value));
		assertTrue(target.hasAttached(basic));
		assertEquals(value, target.getAttached(basic));
		assertDoesNotThrow(() -> target.getAttachedOrThrow(basic));

		UnaryOperator<String> modifier = s -> s + '_';
		String modified = modifier.apply(value);
		target.modifyAttached(basic, modifier);
		assertEquals(modified, target.getAttached(basic));
		assertEquals(modified, target.removeAttached(basic));
		assertFalse(target.hasAttached(basic));
		assertThrows(NullPointerException.class, () -> target.getAttachedOrThrow(basic));
	}

	@Test
	void testDefaulted() {
		AttachmentType<Integer> defaulted = AttachmentRegistry.createDefaulted(
				new Identifier(MOD_ID, "defaulted_attachment"),
				() -> 0
		);
		Entity target = mock(Entity.class, CALLS_REAL_METHODS);

		assertFalse(target.hasAttached(defaulted));
		assertEquals(0, target.getAttachedOrCreate(defaulted));
		target.removeAttached(defaulted);
		assertFalse(target.hasAttached(defaulted));
	}
}
