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

package net.fabricmc.fabric.impl.transfer.fluid;

import java.util.Objects;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

class WorldLocation {
	final World world;
	final BlockPos pos;

	WorldLocation(World world, BlockPos pos) {
		this.world = world;
		this.pos = pos;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		WorldLocation that = (WorldLocation) o;
		return world.equals(that.world) && pos.equals(that.pos);
	}

	@Override
	public int hashCode() {
		return Objects.hash(world, pos);
	}

	@Override
	public String toString() {
		return "WorldLocation{" + "world=" + world + ", pos=" + pos + '}';
	}
}
