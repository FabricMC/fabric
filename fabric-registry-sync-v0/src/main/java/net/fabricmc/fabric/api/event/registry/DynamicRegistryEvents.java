package net.fabricmc.fabric.api.event.registry;

import java.util.Collection;
import java.util.List;

import com.mojang.serialization.Codec;
import org.jetbrains.annotations.ApiStatus;

import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryLoader;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.impl.registry.sync.DynamicRegistryRegistrationContextImpl;

/**
 * Events relating to dynamic registries.
 */
public final class DynamicRegistryEvents {
	// Events

	/**
	 * An event for registering new dynamic registries.
	 */
	public static final Event<RegisterRegistries> REGISTER_REGISTRIES = EventFactory.createArrayBacked(RegisterRegistries.class, callbacks -> context -> {
		for (RegisterRegistries callback : callbacks) {
			callback.registerDynamicRegistries(context);
		}
	});

	// TODO: is this good to have?
	/**
	 * An event that is invoked before dynamic registries are loaded.
	 * An alias for {@link DynamicRegistrySetupCallback#EVENT}.
	 */
	public static final Event<DynamicRegistrySetupCallback> BEFORE_LOAD = DynamicRegistrySetupCallback.EVENT;

	// Phases

	/**
	 * A phase of {@link #REGISTER_REGISTRIES} when vanilla dynamic registries are registered.
	 * This phase executes before {@linkplain Event#DEFAULT_PHASE the default phase}.
	 */
	public static final Identifier REGISTER_VANILLA_REGISTRIES_PHASE = new Identifier("fabric-api", "register_vanilla_registries");

	static {
		REGISTER_REGISTRIES.addPhaseOrdering(REGISTER_VANILLA_REGISTRIES_PHASE, Event.DEFAULT_PHASE);
		REGISTER_REGISTRIES.register(REGISTER_VANILLA_REGISTRIES_PHASE, context -> {
			// Add all vanilla dynamic registries
			context.add(RegistryLoader.DYNAMIC_REGISTRIES);
		});
	}

	private DynamicRegistryEvents() {
	}

	/**
	 * Collects all entries for dynamic registries.
	 *
	 * @return the entries
	 */
	public static List<RegistryLoader.Entry<?>> collectDynamicRegistries() {
		var context = new DynamicRegistryRegistrationContextImpl();
		REGISTER_REGISTRIES.invoker().registerDynamicRegistries(context);
		return context.getEntries();
	}

	@FunctionalInterface
	public interface RegisterRegistries {
		/**
		 * Registers new dynamic registries.
		 *
		 * @param context the registration context
		 */
		void registerDynamicRegistries(RegistrationContext context);
	}

	/**
	 * A context for {@linkplain #REGISTER_REGISTRIES registering dynamic registries}.
	 */
	@ApiStatus.NonExtendable
	public interface RegistrationContext {
		/**
		 * Registers a dynamic registry based on its key and its codec.
		 *
		 * @param registryKey the registry key
		 * @param codec       the codec
		 * @param <T> the registry element type
		 */
		<T> void add(RegistryKey<? extends Registry<T>> registryKey, Codec<T> codec);

		/**
		 * Registers a dynamic registry.
		 *
		 * @param entry the registry loader entry representing the dynamic registry
		 */
		void add(RegistryLoader.Entry<?> entry);

		/**
		 * Registers dynamic registries.
		 *
		 * <p>The array must not contain duplicates.
		 *
		 * @param entries the registry loader entries representing the dynamic registries
		 */
		void add(RegistryLoader.Entry<?>... entries);

		/**
		 * Registers dynamic registries.
		 *
		 * <p>The collection must not contain duplicates.
		 *
		 * @param entries the registry loader entries representing the dynamic registries
		 */
		void add(Collection<? extends RegistryLoader.Entry<?>> entries);
	}
}
