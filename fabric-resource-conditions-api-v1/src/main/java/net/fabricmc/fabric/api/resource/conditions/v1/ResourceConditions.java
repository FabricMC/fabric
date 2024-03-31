package net.fabricmc.fabric.api.resource.conditions.v1;

import net.fabricmc.fabric.impl.resource.conditions.conditions.AllModsLoadedResourceCondition;
import net.fabricmc.fabric.impl.resource.conditions.conditions.AndResourceCondition;
import net.fabricmc.fabric.impl.resource.conditions.conditions.AnyModsLoadedResourceCondition;
import net.fabricmc.fabric.impl.resource.conditions.conditions.FeaturesEnabledResourceCondition;
import net.fabricmc.fabric.impl.resource.conditions.conditions.NotResourceCondition;
import net.fabricmc.fabric.impl.resource.conditions.conditions.OrResourceCondition;
import net.fabricmc.fabric.impl.resource.conditions.conditions.RegistryContainsResourceCondition;
import net.fabricmc.fabric.impl.resource.conditions.conditions.TagsPopulatedResourceCondition;
import net.fabricmc.fabric.impl.resource.conditions.conditions.TrueResourceCondition;

import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class ResourceConditions {
	private static final Map<Identifier, ResourceConditionType<?>> REGISTERED_CONDITIONS = new ConcurrentHashMap<>();

	public static final String CONDITIONS_KEY = "fabric:load_conditions";


	public static void register(ResourceConditionType<?> condition) {
		Objects.requireNonNull(condition, "Condition may not be null.");

		if (REGISTERED_CONDITIONS.put(condition.id(), condition) != null) {
			throw new IllegalArgumentException("Duplicate resource condition registered with id " + condition.id());
		}
	}

	public static ResourceConditionType<?> getConditionType(Identifier id) {
		return REGISTERED_CONDITIONS.get(id);
	}

	public static ResourceCondition alwaysTrue() {
		return new TrueResourceCondition();
	}

	public static ResourceCondition not(ResourceCondition condition) {
		return new NotResourceCondition(condition);
	}

	public static ResourceCondition and(ResourceCondition... conditions) {
		return new AndResourceCondition(List.of(conditions));
	}

	public static ResourceCondition or(ResourceCondition... conditions) {
		return new OrResourceCondition(List.of(conditions));
	}

	public static ResourceCondition allModsLoaded(String... modIds) {
		return new AllModsLoadedResourceCondition(List.of(modIds));
	}

	public static ResourceCondition anyModsLoaded(String... modIds) {
		return new AnyModsLoadedResourceCondition(List.of(modIds));
	}

	@SafeVarargs
	public static <T> ResourceCondition tagsPopulated(Identifier registry, TagKey<T>... tags) {
		return new TagsPopulatedResourceCondition(registry, tags);
	}

	public static ResourceCondition featuresEnabled(Identifier... features) {
		return new FeaturesEnabledResourceCondition(features);
	}

	@SafeVarargs
	public static <T> ResourceCondition registryContains(RegistryKey<T>... entries) {
		return new RegistryContainsResourceCondition(entries);
	}

	public static <T> ResourceCondition registryContains(RegistryKey<Registry<T>> registry, Identifier... entries) {
		return new RegistryContainsResourceCondition(registry.getValue(), entries);
	}
}
