package net.fabricmc.fabric.mixin.dimension;

import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.TaggedChoice;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.fabricmc.fabric.impl.dimension.TaggedChoiceTypeExtension;

@Mixin(value = TaggedChoice.TaggedChoiceType.class, remap = false)
public class MixinTaggedChoiceType<K> implements TaggedChoiceTypeExtension {
	@Unique
	private static final Logger LOGGER = LoggerFactory.getLogger("TaggedChoiceType_DimDataFix");

	@Shadow(remap = false)
	@Final
	protected Object2ObjectMap<K, Type<?>> types;

	@Unique
	private boolean failSoft;

	/**
	 * Make the DSL.taggedChoiceLazy to ignore mod custom generator types and not cause deserialization failure.
	 * The Codec.PASSTHROUGH will not make Dynamic to be deserialized and serialized to Dynamic.
	 * This will avoid deserialization failure from DFU when upgrading level.dat that contains mod custom generator types.
	 */
	@Inject(
			method = "getCodec", at = @At("HEAD"), cancellable = true, remap = false
	)
	private void onGetCodec(K k, CallbackInfoReturnable<DataResult<? extends Codec<?>>> cir) {
		if (failSoft) {
			if (!types.containsKey(k)) {
				LOGGER.warn("Not recognizing key {}. Using pass-through codec. {}", k, this);
				cir.setReturnValue(DataResult.success(Codec.PASSTHROUGH));
			}
		}
	}

	@Override
	public void fabric$setFailSoft(boolean cond) {
		failSoft = cond;
	}
}
