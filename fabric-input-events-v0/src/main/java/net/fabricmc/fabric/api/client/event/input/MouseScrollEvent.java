package net.fabricmc.fabric.api.client.event.input;

import net.fabricmc.fabric.api.client.FabricMouse;

public class MouseScrollEvent extends GenericMouseEvent {
	public MouseScrollEvent(double scrollX, double scrollY) {
		super(FabricMouse.getX(), FabricMouse.getY(), 0.0, 0.0, FabricMouse.getPressedButtons(), FabricMouse.getMods(), scrollX, scrollY);
	}
}
