package net.fabricmc.fabric.api.listener;

import net.fabricmc.fabric.impl.listener.ListenerRegistryArrays;

public interface ListenerRegistry {
	final ListenerRegistry INSTANCE = ListenerRegistryArrays.INSTANCE;

	<T> ListenerReference<T> get(Class<T> listenerType);
	<T> void register(Class<T> listenerType, T listener);
	<T> void registerType(Class<T> listenerType, ListenerChainer<T> chainer);
}
