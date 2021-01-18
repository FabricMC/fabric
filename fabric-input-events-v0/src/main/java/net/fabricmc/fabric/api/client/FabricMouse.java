package net.fabricmc.fabric.api.client;

import net.fabricmc.fabric.impl.client.FabricMouseImpl;

public interface FabricMouse {
	public static FabricMouse INSTANCE = FabricMouseImpl.INSTANCE;

	public double impl_getX();
	public double impl_getY();
	public int impl_getPressedButtons();
	public boolean impl_isButtonPressed(int button);
	public int impl_getMods();

	public static double getX() {
		return FabricMouse.INSTANCE.impl_getX();
	}
	public static double getY() {
		return FabricMouse.INSTANCE.impl_getY();
	}
	public static int getPressedButtons() {
		return FabricMouse.INSTANCE.impl_getPressedButtons();
	}
	public static boolean isButtonPressed(int button) {
		return FabricMouse.INSTANCE.impl_isButtonPressed(button);
	}
	public static int getMods() {
		return FabricMouse.INSTANCE.impl_getMods();
	}
}
