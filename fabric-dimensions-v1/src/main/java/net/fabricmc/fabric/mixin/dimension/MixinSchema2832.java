package net.fabricmc.fabric.mixin.dimension;

import java.util.Map;
import java.util.function.Supplier;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.TaggedChoice;
import com.mojang.datafixers.types.templates.TypeTemplate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.datafixer.schema.Schema2832;

import net.fabricmc.fabric.impl.dimension.TaggedChoiceExtension;

@Mixin(value = Schema2832.class, remap = false)
public class MixinSchema2832 {
	/**
	 * Make the DSL.taggedChoiceLazy to ignore mod custom generator types and not cause deserialization failure.
	 */
	@Redirect(
			method = {
					"method_38837", "method_38838"
			},
			at = @At(
					value = "INVOKE",
					target = "Lcom/mojang/datafixers/DSL;taggedChoiceLazy(Ljava/lang/String;Lcom/mojang/datafixers/types/Type;Ljava/util/Map;)Lcom/mojang/datafixers/types/templates/TaggedChoice;",
					remap = false
			),
			remap = false
	)
	private static <K> TaggedChoice<K> redirectTaggedChoiceLazy(
			String name, Type<K> keyType, Map<K, Supplier<TypeTemplate>> templates
	) {
		TaggedChoice<K> result = DSL.taggedChoiceLazy(name, keyType, templates);
		((TaggedChoiceExtension) (Object) result).fabric$setFailSoft(true);
		return result;
	}
}
