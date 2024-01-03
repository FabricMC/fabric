package net.fabricmc.fabric.test.attachment.gametest;

import java.util.Objects;
import java.util.function.IntSupplier;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.test.GameTest;
import net.minecraft.test.GameTestException;
import net.minecraft.test.TestContext;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.fabricmc.fabric.test.attachment.AttachmentTestMod;

public class AttachmentCopyTests implements FabricGameTest {
	// using a lambda type because serialization shouldn't play a role in this
	public static AttachmentType<IntSupplier> DUMMY = AttachmentRegistry.create(
			new Identifier(AttachmentTestMod.MOD_ID, "dummy")
	);
	public static AttachmentType<IntSupplier> COPY_ON_DEATH = AttachmentRegistry.<IntSupplier>builder()
			.copyOnDeath()
			.buildAndRegister(new Identifier(AttachmentTestMod.MOD_ID, "copy_test"));

	@GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
	public void testCrossWorldTeleport(TestContext context) {
		MinecraftServer server = context.getWorld().getServer();
		ServerWorld overworld = server.getOverworld();
		ServerWorld end = server.getWorld(World.END);
		// using overworld and end to avoid portal code related to the nether

		Entity entity = EntityType.PIG.create(overworld);
		Objects.requireNonNull(entity, "entity was null");
		entity.setAttached(DUMMY, () -> 10);
		entity.setAttached(COPY_ON_DEATH, () -> 10);

		Entity moved = entity.moveToWorld(end);
		if (moved == null) throw new GameTestException("Cross-world teleportation failed");

		IntSupplier attached1 = moved.getAttached(DUMMY);
		IntSupplier attached2 = moved.getAttached(COPY_ON_DEATH);

		if (attached1 == null || attached1.getAsInt() != 10 || attached2 == null || attached2.getAsInt() != 10) {
			throw new GameTestException("Attachment copying failed during cross-world teleportation");
		}

		moved.discard();
		context.complete();
	}

	@GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
	public void testMobConversion(TestContext context) {
		MobEntity mob = Objects.requireNonNull(EntityType.ZOMBIE.create(context.getWorld()));
		mob.setAttached(DUMMY, () -> 42);
		mob.setAttached(COPY_ON_DEATH, () -> 42);
		MobEntity converted = mob.convertTo(EntityType.DROWNED, false);
		if (converted == null) throw new GameTestException("Conversion failed");

		if (converted.hasAttached(DUMMY)) {
			throw new GameTestException("Attachment shouldn't have been copied on mob conversion");
		}

		IntSupplier attached = converted.getAttached(COPY_ON_DEATH);

		if (attached == null || attached.getAsInt() != 42) {
			throw new GameTestException("Attachment copying failed during mob conversion");
		}

		converted.discard();
		context.complete();
	}
}
