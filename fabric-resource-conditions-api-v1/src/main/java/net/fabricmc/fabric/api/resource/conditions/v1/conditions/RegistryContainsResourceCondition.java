package net.fabricmc.fabric.api.resource.conditions.v1.conditions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.fabricmc.fabric.api.resource.conditions.v1.ResourceCondition;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditionType;
import net.fabricmc.fabric.impl.resource.conditions.ResourceConditionsImpl;

import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.Arrays;
import java.util.List;

public record RegistryContainsResourceCondition(Identifier registry, List<Identifier> entries) implements ResourceCondition {
	public static final Codec<RegistryContainsResourceCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Identifier.CODEC.fieldOf("registry").orElse(RegistryKeys.ITEM.getValue()).forGetter(RegistryContainsResourceCondition::registry),
		Identifier.CODEC.listOf().fieldOf("values").forGetter(RegistryContainsResourceCondition::entries)
	).apply(instance, RegistryContainsResourceCondition::new));

	public RegistryContainsResourceCondition(Identifier registry, Identifier... entries) {
		this(registry, List.of(entries));
	}

	public RegistryContainsResourceCondition(Identifier registry, RegistryKey<?>... entries) {
		this(registry, Arrays.stream(entries).map(RegistryKey::getValue).toList());
	}

	@SafeVarargs
	public <T> RegistryContainsResourceCondition(RegistryKey<T>... entries) {
		this(entries[0].getRegistry(), Arrays.stream(entries).map(RegistryKey::getValue).toList());
	}

	@Override
	public ResourceConditionType<?> getType() {
		return ResourceConditionType.REGISTRY_CONTAINS;
	}

	@Override
	public boolean test() {
		return ResourceConditionsImpl.registryContains(this.registry(), this.entries());
	}
}
