package net.fabricmc.fabric.impl.listener;

import com.google.common.collect.Multimap;
import net.fabricmc.fabric.api.listener.ListenerChainer;
import net.fabricmc.fabric.api.listener.ListenerReference;
import net.fabricmc.fabric.api.listener.ListenerRegistry;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Array;
import java.util.*;

public class ListenerRegistryArrays implements ListenerRegistry {
	public static class Reference<T> implements ListenerReference<T> {
		private T reference;

		private void set(T ref) {
			this.reference = ref;
		}

		@Override
		public T get() {
			return reference;
		}
	}

	public static final ListenerRegistryArrays INSTANCE = new ListenerRegistryArrays();
	private static final MethodHandle CHAINER_CHAIN_METHOD;
	private final Map<Class<?>, List<Object>> listenerMap = new HashMap<>();
	private final Map<Class<?>, ListenerChainer<?>> listenerChainerMap = new HashMap<>();
	private final Map<Class<?>, Reference<?>> listenerReferenceMap = new HashMap<>();

	static {
		try {
			CHAINER_CHAIN_METHOD = MethodHandles.publicLookup()
				.findVirtual(ListenerChainer.class, "chain", MethodType.methodType(Object.class, Object[].class));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public <T> ListenerReference<T> get(Class<T> listenerType) {
		//noinspection unchecked
		return (ListenerReference<T>) listenerReferenceMap.computeIfAbsent(listenerType, (t) -> new Reference<>());
	}

	private void update(Class<?> listenerType) {
		if (listenerChainerMap.containsKey(listenerType)) {
			List<Object> list = listenerMap.getOrDefault(listenerType, Collections.emptyList());
			Object array = Array.newInstance(listenerType, list.size());
			for (int i = 0; i < list.size(); i++) {
				Array.set(array, i, list.get(i));
			}

			// Please forgive me.
			try {
				Object chained = CHAINER_CHAIN_METHOD.invoke(listenerChainerMap.get(listenerType), array);
				//noinspection unchecked
				((Reference) get(listenerType)).set(chained);
			} catch (Throwable t) {
				throw new RuntimeException(t);
			}
		}
	}

	@Override
	public <T> void register(Class<T> listenerType, T listener) {
		listenerMap.computeIfAbsent(listenerType, (t) -> new ArrayList<>()).add(listener);
		update(listenerType);
	}

	@Override
	public <T> void registerType(Class<T> listenerType, ListenerChainer<T> chainer) {
		if (listenerChainerMap.containsKey(listenerType)) {
			throw new RuntimeException("Duplicate type registration attempted: " + listenerType.getName());
		}

		listenerChainerMap.put(listenerType, chainer);
		update(listenerType);
	}
}
