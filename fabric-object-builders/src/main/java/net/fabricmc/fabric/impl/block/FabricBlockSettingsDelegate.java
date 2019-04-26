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

package net.fabricmc.fabric.impl.block;

import net.minecraft.block.MaterialColor;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

public interface FabricBlockSettingsDelegate {
	void fabric_setMaterialColor(MaterialColor color);

	void fabric_setCollidable(boolean value);

	void fabric_setSoundGroup(BlockSoundGroup group);

	void fabric_setLightLevel(int value);

	void fabric_setHardness(float value);

	void fabric_setResistance(float value);

	void fabric_setRandomTicks(boolean value);

	void fabric_setFriction(float value);

	void fabric_setDropTable(Identifier id);

	void fabric_setDynamicBounds(boolean value);
}
