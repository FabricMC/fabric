package net.fabricmc.fabric.mixin.dimension;

import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.TaggedChoice;
import com.mojang.datafixers.util.Pair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.fabricmc.fabric.impl.dimension.TaggedChoiceExtension;
import net.fabricmc.fabric.impl.dimension.TaggedChoiceTypeExtension;

@Mixin(value = TaggedChoice.class, remap = false)
public class MixinTaggedChoice implements TaggedChoiceExtension {
	@Unique
	boolean failSoft = false;

	@Override
	public void fabric$setFailSoft(boolean cond) {
		failSoft = cond;
	}

	/**
	 * Pass the failSoft information into TaggedChoice.TaggedChoiceType.
	 */
	@SuppressWarnings("rawtypes")
	@Inject(
			method = "lambda$apply$0", at = @At("RETURN"), remap = false
	)
	private void onApply(Pair key, CallbackInfoReturnable<Type> cir) {
		if (failSoft) {
			Type returnValue = cir.getReturnValue();

			if (returnValue instanceof TaggedChoice.TaggedChoiceType<?> taggedChoiceType) {
				((TaggedChoiceTypeExtension) (Object) taggedChoiceType).fabric$setFailSoft(true);
			}
		}
	}
}
