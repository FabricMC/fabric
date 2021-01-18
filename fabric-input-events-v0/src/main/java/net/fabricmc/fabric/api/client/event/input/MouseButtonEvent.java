package net.fabricmc.fabric.api.client.event.input;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.fabricmc.fabric.api.client.FabricMouse;
import net.fabricmc.fabric.mixin.event.input.client.InputUtilTypeMixin;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.InputUtil.Key;

public class MouseButtonEvent extends GenericMouseEvent {
	private static final Int2ObjectMap<Key> map = ((InputUtilTypeMixin)(Object)InputUtil.Type.MOUSE).fabric_getMap();

	public final int button;
	public final int action;
	public final int mods;

	public MouseButtonEvent(int button, int action, int mods) {
		super(FabricMouse.getX(), FabricMouse.getY(), 0.0, 0.0, FabricMouse.getPressedButtons(), FabricMouse.getMods());
		this.button = button;
		this.action = action;
		this.mods = mods;
	}

	public Key getKey() {
		return map.get(this.button);
	}
}
