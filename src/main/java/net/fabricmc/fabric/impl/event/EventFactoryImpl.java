package net.fabricmc.fabric.impl.event;

import net.fabricmc.fabric.api.event.Event;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandleProxies;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.*;
import java.util.function.Function;

public final class EventFactoryImpl {
	private EventFactoryImpl() {

	}

	public static <T> Event<T> arrayBacked(Class<T> type, Function<T[], T> joiner) {
		return arrayBacked(type, buildEmptyInvoker(type, joiner), joiner);
	}

	public static <T> Event<T> arrayBacked(Class<T> type, T emptyInvoker, Function<T[], T> joiner) {
		return new ArrayBackedEvent<>(type, emptyInvoker, joiner);
	}

	// Code originally by sfPlayer1
	private static <T> T buildEmptyInvoker(Class<T> handlerClass, Function<T[], T> invokerSetup) {
		// find the functional interface method
		Method funcIfMethod = null;

		for (Method m : handlerClass.getMethods()) {
			if ((m.getModifiers() & (Modifier.STRICT | Modifier.PRIVATE)) == 0) {
				if (funcIfMethod != null) {
					throw new IllegalStateException("Multiple virtual methods in " + handlerClass + "; cannot build empty invoker!");
				}

				funcIfMethod = m;
			}
		}

		if (funcIfMethod == null) {
			throw new IllegalStateException("No virtual methods in " + handlerClass + "; cannot build empty invoker!");
		}

		Object defValue = null;

		try {
			// concert to mh, determine its type without the "this" reference
			MethodHandle target = MethodHandles.lookup().unreflect(funcIfMethod);
			MethodType type = target.type().dropParameterTypes(0, 1);

			if (type.returnType() != void.class) {
				// determine default return value by invoking invokerSetup.apply(T[0]) with all-jvm-default args (null for refs, false for boolean, etc.)
				// explicitCastArguments is being used to cast Object=null to the jvm default value for the correct type

				// construct method desc (TLjava/lang/Object;Ljava/lang/Object;...)R where T = invoker ref ("this"), R = invoker ret type and args 1+ are Object for each non-"this" invoker arg
				MethodType objTargetType = MethodType.genericMethodType(type.parameterCount()).changeReturnType(type.returnType()).insertParameterTypes(0, target.type().parameterType(0));
				// explicit cast to translate to the invoker args from Object to their real type, inferring jvm default values
				MethodHandle objTarget = MethodHandles.explicitCastArguments(target, objTargetType);

				// build invocation args with 0 = "this", 1+ = null
				Object[] args = new Object[target.type().parameterCount()];
				//noinspection unchecked
				args[0] = invokerSetup.apply((T[]) Array.newInstance(handlerClass, 0));

				// retrieve default by invoking invokerSetup.apply(T[0]).targetName(def,def,...)
				defValue = objTarget.invokeWithArguments(args);
			}
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}

		final Object returnValue = defValue;
		//noinspection unchecked
		return (T) Proxy.newProxyInstance(EventFactoryImpl.class.getClassLoader(), new Class[] { handlerClass },
			(proxy, method, args) -> returnValue);
	}
}
