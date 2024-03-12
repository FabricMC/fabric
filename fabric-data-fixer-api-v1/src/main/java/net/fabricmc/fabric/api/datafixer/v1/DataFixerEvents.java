package net.fabricmc.fabric.api.datafixer.v1;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

import java.util.Map;
import java.util.function.Supplier;

public final class DataFixerEvents {

	/**
	 * Called when vanilla schema 1904 registers entities.
	 */
	public static final Event<RegisterBlockEntities> REGISTER_BLOCK_ENTITIES = EventFactory.createArrayBacked(RegisterBlockEntities.class, callbacks -> (registry, schema) -> {
		for (RegisterBlockEntities callback : callbacks) {
			callback.onRegisterBlockEntities(registry, schema);
		}
	});

	/**
	 * Called when vanilla schema 1906 registers block entities.
	 */
	public static final Event<RegisterEntities> REGISTER_ENTITIES = EventFactory.createArrayBacked(RegisterEntities.class, callbacks -> (registry, schema) -> {
		for (RegisterEntities callback : callbacks) {
			callback.onRegisterEntities(registry, schema);
		}
	});

	private DataFixerEvents() {
	}

	@FunctionalInterface
	public interface RegisterBlockEntities {
		void onRegisterBlockEntities(Map<String, Supplier<TypeTemplate>> registry, Schema schema);
	}

	@FunctionalInterface
	public interface RegisterEntities {
		void onRegisterEntities(Map<String, Supplier<TypeTemplate>> registry, Schema schema);
	}
}
