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

package net.fabricmc.fabric.mixin.registry.sync;

import java.util.Locale;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.datafixer.fix.StructuresToConfiguredStructuresFix;

@Mixin(StructuresToConfiguredStructuresFix.class)
public class MixinStructuresToConfiguredStructuresFix {
	private static final Logger LOGGER = LoggerFactory.getLogger("MixinStructuresToConfiguredStructuresFix");

	/**
	 * Vanilla throws a IllegalStateException when there is no mapping for a structure to upgrade, this causes the chunk to reset and regenerate.
	 * Now we just return the previous value, this can still cause a missing structure log warning, but it is a lot better than resetting the chunk.
	 */
	@Inject(method = "method_41022", at = @At(value = "INVOKE", target = "Ljava/lang/IllegalStateException;<init>(Ljava/lang/String;)V"), cancellable = true)
	private void method_41022(Pair<Dynamic<?>, Dynamic<?>> pair, Dynamic<?> dynamic, CallbackInfoReturnable<Dynamic<?>> cir) {
		String id = pair.getFirst().asString("UNKNOWN").toLowerCase(Locale.ROOT);
		LOGGER.debug("Found unknown structure: {}", id);
		cir.setReturnValue(pair.getSecond().createString(id));
	}
}
