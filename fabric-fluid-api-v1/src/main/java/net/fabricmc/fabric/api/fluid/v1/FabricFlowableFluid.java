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

package net.fabricmc.fabric.api.fluid.v1;

import java.util.Optional;

import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.LavaFluid;
import net.minecraft.fluid.WaterFluid;
import net.minecraft.sound.SoundEvent;

/**
 * Implements extra features and configs for {@link FlowableFluid}.
 */
public interface FabricFlowableFluid {
	/**
	 * <p>Gets the sound played when the bucket containing this fluid is emptied.</p>
	 * <p>If the return value is <code>Optional.empty()</code>, the following sounds are played:</p>
	 * <p>- If the fluid extends {@link FlowableFluid} or {@link WaterFluid} is played the <code>ITEM_BUCKET_EMPTY</code> sound.</p>
	 * <p>- If the fluid extends {@link LavaFluid} is played the <code>ITEM_BUCKET_EMPTY_LAVA</code> sound.</p>
	 *
	 * @return SoundEvent played when the bucket containing this fluid is emptied.
	 */
	default Optional<SoundEvent> getFabricBucketEmptySound() {
		return Optional.empty();
	}
}
