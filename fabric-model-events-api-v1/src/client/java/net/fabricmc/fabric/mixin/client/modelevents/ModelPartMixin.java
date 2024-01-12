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

package net.fabricmc.fabric.mixin.client.modelevents;

import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.ApiStatus;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.fabricmc.fabric.impl.client.modelevents.FabricPartHooks;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPart.Cuboid;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;

@ApiStatus.Internal
@Mixin(ModelPart.class)
abstract class ModelPartMixin implements FabricPartHooks.Container {
    @Shadow
    public boolean visible;
    @Shadow
    public boolean hidden;

    private FabricPartHooks fabric_hooks;

    @Dynamic("Compiler-generated class constructor method")
    @Inject(method = "<init>", at = @At("RETURN"), remap = false)
    private void init_ModelPart(List<Cuboid> cuboids, Map<String, ModelPart> children, CallbackInfo info) {
        children.values().forEach(child -> {
            FabricPartHooks.Container.of(child).getHooks().setParent(getHooks());
        });
    }

    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;IIFFFF)V", at = @At(
            value = "INVOKE",
            target = "net/minecraft/client/model/ModelPart.rotate(Lnet/minecraft/client/util/math/MatrixStack;)V"))
    private void on_render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha, CallbackInfo info) {
        if (hidden || !visible) {
            return;
        }

        getHooks().onPartRendered(matrices, vertices, light, overlay, red, green, blue, alpha);
    }

    @Override
    public FabricPartHooks getHooks() {
        if (fabric_hooks == null) {
            fabric_hooks = new FabricPartHooks(this);
        }
        return fabric_hooks;
    }

    @Override
    @Accessor("cuboids")
    public abstract List<Cuboid> getCuboids();

    @Override
    @Accessor("children")
    public abstract Map<String, ModelPart> getChildren();
}
