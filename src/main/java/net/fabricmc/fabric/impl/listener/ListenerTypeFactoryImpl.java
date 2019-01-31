package net.fabricmc.fabric.impl.listener;

import net.fabricmc.fabric.api.event.listener.ListenerChainer;
import net.fabricmc.fabric.api.event.listener.ListenerType;
import net.fabricmc.fabric.api.event.listener.ListenerTypeFactory;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Array;
import java.util.*;

public class ListenerTypeFactoryImpl implements ListenerTypeFactory {
	static class Type<T> extends ListenerType<T> {
		private final Class<T> type;
		private final List<Object> listeners = new ArrayList<>();

		Type(Class<T> type) {
			this.type = type;
			update();
		}

		@SuppressWarnings("unchecked")
		private void update() {
			switch (listeners.size()) {
				case 0:
					reference = (T) ListenerTypeFactoryImpl.INSTANCE.emptyRefMap.get(type);
					break;
				case 1:
					reference = (T) listeners.get(0);
					break;
				default:
					ListenerChainer<?> chainer = ListenerTypeFactoryImpl.INSTANCE.listenerChainerMap.get(type);
					if (chainer == null) {
						reference = null;
					} else {

						Object array = Array.newInstance(type, listeners.size());
						for (int i = 0; i < listeners.size(); i++) {
							Array.set(array, i, listeners.get(i));
						}

						// Please forgive me.
						try {
							Object chained = CHAINER_CHAIN_METHOD.invoke(chainer, array);
							reference = (T) chained;
						} catch (Throwable t) {
							throw new RuntimeException(t);
						}
					}
					break;
			}
		}

		@Override
		public void register(T listener) {
			listeners.add(listener);
			update();
		}
	}

	public static final ListenerTypeFactoryImpl INSTANCE = new ListenerTypeFactoryImpl();
	private static final MethodHandle CHAINER_CHAIN_METHOD;
	private final Map<Class<?>, List<Type<?>>> listenerTypeRefs = new HashMap<>();
	private final Map<Class<?>, ListenerChainer<?>> listenerChainerMap = new HashMap<>();
	private final Map<Class<?>, Object> emptyRefMap = new HashMap<>();

	static {
		try {
			CHAINER_CHAIN_METHOD = MethodHandles.publicLookup()
				.findVirtual(ListenerChainer.class, "chain", MethodType.methodType(Object.class, Object[].class));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void updateAll(Class<?> listenerType) {
		List<Type<?>> list = listenerTypeRefs.get(listenerType);
		if (list != null) {
			list.forEach(Type::update);
		}
	}

	@Override
	public <T> ListenerType<T> create(Class<T> listenerType) {
		Type<T> type = new Type<>(listenerType);
		listenerTypeRefs.computeIfAbsent(listenerType, (t) -> new ArrayList<>()).add(type);
		return type;
	}

	@Override
	public <T> void registerListenerClass(Class<T> listenerType, T emptyListener, ListenerChainer<T> chainer) {
		if (listenerChainerMap.containsKey(listenerType)) {
			throw new RuntimeException("Duplicate type registration attempted: " + listenerType.getName());
		}

		emptyRefMap.put(listenerType, emptyListener);
		listenerChainerMap.put(listenerType, chainer);
		updateAll(listenerType);
	}
}
