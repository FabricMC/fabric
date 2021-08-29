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

package net.fabricmc.fabric.impl.client.rendering;

import java.util.List;
import java.util.Set;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import net.minecraft.client.item.TooltipData;

import net.fabricmc.loader.api.FabricLoader;

/**
 * Used to replace the body of {@link net.minecraft.client.gui.tooltip.TooltipComponent#of(TooltipData)}
 * by a call to {@link TooltipComponentCallbackImpl#of(TooltipData)}.
 */
public class TooltipComponentMixinPlugin implements IMixinConfigPlugin {
	private static final String MIXIN_CLASS = "net.fabricmc.fabric.mixin.client.rendering.MixinTooltipComponent";
	private static final String TARGET_METHOD_DESC = String.format(
			"(L%s;)L%s;",
			FabricLoader.getInstance().getMappingResolver().mapClassName("intermediary", "net.minecraft.class_5632"), // TooltipData
			FabricLoader.getInstance().getMappingResolver().mapClassName("intermediary", "net.minecraft.class_5684") // TooltipComponent
	).replace('.', '/');

	@Override
	public void onLoad(String mixinPackage) { }

	@Override
	public String getRefMapperConfig() {
		return null;
	}

	@Override
	public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
		return true;
	}

	@Override
	public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) { }

	@Override
	public List<String> getMixins() {
		return null;
	}

	@Override
	public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
		if (!mixinClassName.equals(MIXIN_CLASS)) return;

		MethodNode method = findOfMethod(targetClass);
		method.instructions.clear();
		method.visitIntInsn(Opcodes.ALOAD, 0);
		method.visitMethodInsn(Opcodes.INVOKESTATIC, "net/fabricmc/fabric/impl/client/rendering/TooltipComponentCallbackImpl", "of", TARGET_METHOD_DESC, false);
		method.visitInsn(Opcodes.ARETURN);
	}

	private static MethodNode findOfMethod(ClassNode node) {
		return node.methods.stream()
				.filter(methodNode -> methodNode.desc.equals(TARGET_METHOD_DESC))
				.findFirst().get();
	}

	@Override
	public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) { }
}
