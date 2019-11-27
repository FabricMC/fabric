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

package net.fabricmc.fabric.impl.networking.entity.v1;

import net.minecraft.entity.Entity;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.Vec3d;

import net.fabricmc.fabric.api.networking.entity.v1.SpawnDataHandler;

public final class BuiltinSpawnDataHandlers {
	public static final SpawnDataHandler<Entity> POSITION_AND_ROTATION = new SpawnDataHandler<Entity>() {
		@Override
		public void write(Entity entity, PacketByteBuf buf) {
			buf.writeDouble(entity.getX());
			buf.writeDouble(entity.getY());
			buf.writeDouble(entity.getZ());
			buf.writeFloat(entity.pitch);
			buf.writeFloat(entity.yaw);
		}

		@Override
		public void read(Entity entity, PacketByteBuf buf) {
			double x = buf.readDouble();
			double y = buf.readDouble();
			double z = buf.readDouble();
			float pitch = buf.readFloat();
			float yaw = buf.readFloat();
			entity.setPositionAndAngles(x, y, z, pitch, yaw);
		}
	};
	public static final SpawnDataHandler<Entity> VELOCITY = new SpawnDataHandler<Entity>() {
		@Override
		public void write(Entity entity, PacketByteBuf buf) {
			Vec3d velocity = entity.getVelocity();
			buf.writeDouble(velocity.x);
			buf.writeDouble(velocity.y);
			buf.writeDouble(velocity.z);
		}

		@Override
		public void read(Entity entity, PacketByteBuf buf) {
			double x = buf.readDouble();
			double y = buf.readDouble();
			double z = buf.readDouble();
			entity.setVelocity(x, y, z);
		}
	};
	public static final SpawnDataHandler<Entity> HEAD_YAW = new SpawnDataHandler<Entity>() {
		@Override
		public void write(Entity entity, PacketByteBuf buf) {
			buf.writeFloat(entity.getHeadYaw());
		}

		@Override
		public void read(Entity entity, PacketByteBuf buf) {
			float headYaw = buf.readFloat();
			entity.setHeadYaw(headYaw);
		}
	};
}
