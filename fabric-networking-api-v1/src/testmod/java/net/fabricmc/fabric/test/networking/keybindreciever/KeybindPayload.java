package net.fabricmc.fabric.test.networking.keybindreciever;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

import net.fabricmc.fabric.test.networking.NetworkingTestmods;

public record KeybindPayload() implements CustomPayload {
		public static final CustomPayload.Id<KeybindPayload> ID = new CustomPayload.Id<>(NetworkingTestmods.id("keybind_press_test"));
		public static final PacketCodec<RegistryByteBuf, KeybindPayload> CODEC = CustomPayload.codecOf((value, buf) -> {}, buf -> new KeybindPayload());

		@Override
		public Id<? extends CustomPayload> getId() {
			return ID;
		}
	}
