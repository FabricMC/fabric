package net.fabricmc.fabric.api.client.event.input;

import net.fabricmc.fabric.api.client.FabricMouse;

public class MouseMoveEvent extends GenericMouseEvent {
	public final double x;
	public final double y;
	public final double dx;
	public final double dy;

	public MouseMoveEvent(double x, double y, double dx, double dy) {
		super(x, y, dx, dy, FabricMouse.getPressedButtons(), FabricMouse.getMods());
		this.x = x;
		this.y = y;
		this.dx = dx;
		this.dy = dy;
	}
}
