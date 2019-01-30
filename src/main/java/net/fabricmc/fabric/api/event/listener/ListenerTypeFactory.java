package net.fabricmc.fabric.api.event.listener;

import net.fabricmc.fabric.impl.listener.ListenerTypeFactoryImpl;

public interface ListenerTypeFactory {
	final ListenerTypeFactory INSTANCE = ListenerTypeFactoryImpl.INSTANCE;

	<T> ListenerType<T> create(Class<T> listenerType);
	<T> void registerListenerClass(Class<T> listenerType, T emptyListener, ListenerChainer<T> chainer);
}
