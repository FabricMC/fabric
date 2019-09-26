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

package net.fabricmc.fabric.datafixer.mixin.accessor;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.Hook.HookFunction;

import net.minecraft.datafixers.schemas.Schema705;

@Mixin(Schema705.class)
public interface Schema705Access {
    
    @Accessor
    static HookFunction getField_5746() {
        throw new UnsupportedOperationException("Mixin dummy");
    }
    
    @Invoker
    static void callMethod_5311(Schema schema_1, Map map_1, String string) {
    }
    
    @Invoker
    static void callMethod_5330(Schema schema_1, Map map_1, String string) {
    }
}
