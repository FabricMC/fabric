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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.UnaryOperator;

import com.mojang.serialization.Codec;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import net.minecraft.Bootstrap;
import net.minecraft.SharedConstants;
import net.minecraft.block.entity.BellBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MarkerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.chunk.WorldChunk;

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.impl.attachment.AttachmentPersistentState;
import net.fabricmc.fabric.impl.attachment.AttachmentSerializingImpl;
import net.fabricmc.fabric.impl.attachment.AttachmentTargetImpl;

public class CommonAttachmentTests {
	private static final String MOD_ID = "example";
	private static final AttachmentType<Integer> PERSISTENT = AttachmentRegistry.createPersistent(
			new Identifier(MOD_ID, "persistent"),
			Codec.INT
	);

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
		ProtoChunk protoChunk = mock(ProtoChunk.class, CALLS_REAL_METHODS);

		for (AttachmentTarget target : new AttachmentTarget[]{serverWorld, entity, blockEntity, worldChunk, protoChunk}) {
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

	@Test
	void testStaticReadWrite() {
		AttachmentType<Double> dummy = AttachmentRegistry.createPersistent(
				new Identifier(MOD_ID, "dummy"),
				Codec.DOUBLE
		);
		var map = new IdentityHashMap<AttachmentType<?>, Object>();
		map.put(dummy, 0.5d);
		var fakeSave = new NbtCompound();

		AttachmentSerializingImpl.serializeAttachmentData(fakeSave, map);
		assertTrue(fakeSave.contains(AttachmentTarget.NBT_ATTACHMENT_KEY, NbtElement.COMPOUND_TYPE));
		assertTrue(fakeSave.getCompound(AttachmentTarget.NBT_ATTACHMENT_KEY).contains(dummy.identifier().toString()));

		map = AttachmentSerializingImpl.deserializeAttachmentData(fakeSave);
		assertEquals(1, map.size());
		Map.Entry<AttachmentType<?>, Object> entry = map.entrySet().stream().findFirst().orElseThrow();
		// in this case the key should be the exact same object
		// but in practice this is meaningless because on a dedicated server the JVM restarted
		assertEquals(dummy.identifier(), entry.getKey().identifier());
		assertEquals(0.5d, entry.getValue());
	}

	@Test
	void deserializeNull() {
		var nbt = new NbtCompound();
		assertNull(AttachmentSerializingImpl.deserializeAttachmentData(nbt));

		nbt.put(new Identifier("test").toString(), new NbtCompound());
		assertNull(AttachmentSerializingImpl.deserializeAttachmentData(nbt));
	}

	@Test
	void serializeNullOrEmpty() {
		var nbt = new NbtCompound();
		AttachmentSerializingImpl.serializeAttachmentData(nbt, null);
		assertFalse(nbt.contains(AttachmentTarget.NBT_ATTACHMENT_KEY));

		AttachmentSerializingImpl.serializeAttachmentData(nbt, new IdentityHashMap<>());
		assertFalse(nbt.contains(AttachmentTarget.NBT_ATTACHMENT_KEY));
	}

	@Test
	void testEntityCopy() {
		AttachmentType<Boolean> notCopiedOnRespawn = AttachmentRegistry.create(
				new Identifier(MOD_ID, "not_copied_on_respawn")
		);
		AttachmentType<Boolean> copiedOnRespawn = AttachmentRegistry.<Boolean>builder()
				.copyOnDeath()
				.buildAndRegister(new Identifier(MOD_ID, "copied_on_respawn"));

		Entity original = mock(Entity.class, CALLS_REAL_METHODS);
		original.setAttached(notCopiedOnRespawn, true);
		original.setAttached(copiedOnRespawn, true);

		Entity respawnTarget = mock(Entity.class, CALLS_REAL_METHODS);
		Entity nonRespawnTarget = mock(Entity.class, CALLS_REAL_METHODS);

		AttachmentTargetImpl.transfer(original, respawnTarget, true);
		AttachmentTargetImpl.transfer(original, nonRespawnTarget, false);
		assertTrue(respawnTarget.hasAttached(copiedOnRespawn));
		assertFalse(respawnTarget.hasAttached(notCopiedOnRespawn));
		assertTrue(nonRespawnTarget.hasAttached(copiedOnRespawn));
		assertTrue(nonRespawnTarget.hasAttached(notCopiedOnRespawn));
	}

	@Test
	void testEntityPersistence() {
		Entity entity = new MarkerEntity(EntityType.MARKER, mock());
		assertFalse(entity.hasAttached(PERSISTENT));

		int expected = 1;
		entity.setAttached(PERSISTENT, expected);
		NbtCompound fakeSave = new NbtCompound();
		entity.writeNbt(fakeSave);

		entity = spy(new MarkerEntity(EntityType.MARKER, mock())); // fresh object, like on restart
		entity.setChangeListener(mock());
		doNothing().when(entity).calculateDimensions();
		entity.readNbt(fakeSave);
		assertTrue(entity.hasAttached(PERSISTENT));
		assertEquals(expected, entity.getAttached(PERSISTENT));
	}

	@Test
	void testBlockEntityPersistence() {
		BlockEntity blockEntity = new BellBlockEntity(BlockPos.ORIGIN, mock());
		assertFalse(blockEntity.hasAttached(PERSISTENT));

		int expected = 1;
		blockEntity.setAttached(PERSISTENT, expected);
		NbtCompound fakeSave = blockEntity.createNbtWithId();

		blockEntity = BlockEntity.createFromNbt(BlockPos.ORIGIN, mock(), fakeSave);
		assertNotNull(blockEntity);
		assertTrue(blockEntity.hasAttached(PERSISTENT));
		assertEquals(expected, blockEntity.getAttached(PERSISTENT));
	}

	@Test
	void testWorldPersistentState() {
		// Trying to simulate actual saving and loading for the world is too hard
		ServerWorld world = mock(ServerWorld.class, CALLS_REAL_METHODS);
		AttachmentPersistentState state = new AttachmentPersistentState(world);
		assertFalse(world.hasAttached(PERSISTENT));

		int expected = 1;
		world.setAttached(PERSISTENT, expected);
		NbtCompound fakeSave = state.writeNbt(new NbtCompound());

		world = mock(ServerWorld.class, CALLS_REAL_METHODS);
		AttachmentPersistentState.read(world, fakeSave);
		assertTrue(world.hasAttached(PERSISTENT));
		assertEquals(expected, world.getAttached(PERSISTENT));
	}

	/*
	 * Chunk serializing is coupled with world saving in ChunkSerializer which is too much of a pain to mock,
	 * so testing is handled by the testmod instead.
	 */
}
