package net.fabricmc.fabric.test.registry.sync;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record TestDynamicObject(String name) {
	public static final Codec<TestDynamicObject> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.STRING.fieldOf("name").forGetter(TestDynamicObject::name)
	).apply(instance, TestDynamicObject::new));
}
