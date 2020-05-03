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

package net.fabricmc.fabric.test.networking;

import static net.fabricmc.fabric.test.networking.NetworkingV1Test.id;

import java.io.IOException;
import java.io.UncheckedIOException;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.BoatEntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCategory;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.networking.v1.client.ClientNetworking;
import net.fabricmc.fabric.api.networking.v1.util.PacketByteBufs;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;

public final class NetworkingSpawningEntity {
	public static final Identifier CHANNEL = id("spawn_entity/v1");
	private static final EntityType<AngryBoatEntity> ANGRY_BOAT = FabricEntityTypeBuilder
			.create(EntityCategory.MISC, AngryBoatEntity::new)
			.dimensions(EntityDimensions.fixed(1.375F, 0.5625F))
			.trackable(80, 5)
			.build();

	public static CustomPayloadS2CPacket toCustomPayload(EntitySpawnS2CPacket packet) {
		PacketByteBuf buf = PacketByteBufs.create();

		try {
			packet.write(buf);
		} catch (IOException ex) {
			throw new UncheckedIOException(ex);
		}

		return new CustomPayloadS2CPacket(CHANNEL, buf);
	}

	@SuppressWarnings("unused") // entrypoint
	public static void init() {
		Registry.register(Registry.ENTITY_TYPE, id("angry_boat"), ANGRY_BOAT);
	}

	@Environment(EnvType.CLIENT)
	@SuppressWarnings("unused") // entrypoint
	public static void clientInit() {
		EntityRendererRegistry.INSTANCE.register(ANGRY_BOAT, (manager, context) -> new BoatEntityRenderer(manager));
		ClientNetworking.getPlayReceiver().register(CHANNEL, (context, buf) -> {
			EntitySpawnS2CPacket packet = new EntitySpawnS2CPacket();

			try {
				packet.read(buf);
			} catch (IOException ex) {
				return; // ignore this packet
			}

			MinecraftClient client = context.getEngine();

			client.send(() -> {
				double d = packet.getX();
				double e = packet.getY();
				double f = packet.getZ();
				EntityType<?> entityType = packet.getEntityTypeId();
				Entity entity = entityType.create(client.world);
				int i = packet.getId();
				entity.updateTrackedPosition(d, e, f);
				entity.pitch = (float) (packet.getPitch() * 360) / 256.0F;
				entity.yaw = (float) (packet.getYaw() * 360) / 256.0F;
				entity.setEntityId(i);
				entity.setUuid(packet.getUuid());
				client.world.addEntity(i, entity);
			});
		});
	}

	private NetworkingSpawningEntity() {
	}

	public static class AngryBoatEntity extends BoatEntity {
		public AngryBoatEntity(EntityType<? extends BoatEntity> entityType, World world) {
			super(entityType, world);
		}

		@Override
		public void tick() {
			super.tick();

			// angry particles on client
			if (world.isClient && age % 17 == 6) {
				for (int i = 0; i < 3; i++) {
					double d = this.random.nextGaussian() * 0.02D;
					double e = this.random.nextGaussian() * 0.02D;
					double f = this.random.nextGaussian() * 0.02D;
					world.addParticle(ParticleTypes.ANGRY_VILLAGER, getParticleX(1.2), getRandomBodyY() + 0.5, getParticleZ(1.2), d, e, f);
				}
			}
		}

		@Override
		public Packet<?> createSpawnPacket() {
			return NetworkingSpawningEntity.toCustomPayload(new EntitySpawnS2CPacket(this));
		}
	}
}
