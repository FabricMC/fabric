package net.fabricmc.fabric.api.resource.conditions.v1;

import com.mojang.serialization.Codec;

import com.mojang.serialization.DataResult;

import net.minecraft.util.Identifier;

import java.util.Optional;

public interface ResourceConditionType<T extends ResourceCondition> {
	Codec<ResourceConditionType<?>> TYPE_CODEC = Identifier.CODEC.comapFlatMap(id ->
		Optional.ofNullable(ResourceConditions.getConditionType(id)).map(DataResult::success).orElseGet(() -> DataResult.error(() -> "Unknown resource condition key: "+ id )),
		ResourceConditionType::id
	);

	Identifier id();
	Codec<T> codec();
	static <T extends ResourceCondition> ResourceConditionType<T> create(Identifier id, Codec<T> codec) {
		return new ResourceConditionType<>() {
			@Override
			public Identifier id() {
				return id;
			}

			@Override
			public Codec<T> codec() {
				return codec;
			}
		};
	}
}
