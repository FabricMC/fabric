package java.net.fabricmc.fabric.events;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.PlayerTickCallback;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import java.awt.*;


public class PlayerTickEventMod implements ModInitializer {
	@Override
	public void onInitialize() {
		PlayerTickCallback.EVENT.register(player -> {
			ItemStack stack = player.getEquippedStack(EquipmentSlot.HEAD);
			if (stack.getItem() == Items.PLAYER_HEAD) {
				//if someone is wearing asie's head, make them glow
				TextComponent name = stack.getItem().getTranslatedNameTrimmed(stack);
				if (name.getText().equals("asiekierka")) {
					player.addPotionEffect(new StatusEffectInstance(StatusEffects.GLOWING));
				}
			}
		});
	}
}
