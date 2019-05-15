package java.net.fabricmc.fabric.events;

import net.fabricmc.api.ModInitializer;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.chat.Component;


public class EntityTickEventMod implements ModInitializer {
	@Override
	public void onInitialize() {
		EntityTickCallback.event(EntityType.PLAYER).register(entity -> {
			PlayerEntity player = (PlayerEntity)entity;
			ItemStack stack = player.getEquippedStack(EquipmentSlot.HEAD);
			if (stack.getItem() == Items.PLAYER_HEAD) {
				//if someone is wearing asie's head, make them glow
				Component name = stack.getItem().getTranslatedNameTrimmed(stack);
				if (name.getText().equals("asiekierka")) {
					player.addPotionEffect(new StatusEffectInstance(StatusEffects.GLOWING));
				}
			}
		});
	}
}
