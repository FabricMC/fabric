package net.fabricmc.fabric.mixin.tools;

import com.google.common.collect.Multimap;
import net.fabricmc.fabric.api.tools.v1.ActableAttributeHolder;
import net.fabricmc.fabric.api.tools.v1.ToolActor;
import net.fabricmc.fabric.impl.tools.AttributeManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity extends Entity {

	private ToolActor<LivingEntity> actor = ToolActor.of((LivingEntity)(Object)this);

	public MixinLivingEntity(EntityType<?> type, World world) {
		super(type, world);
	}

	/**
	 * @author B0undarybreaker
	 * @reason get entity attribute modifiers for actable tools
	 */
	@Redirect(method = "writeCustomDataToTag", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getAttributeModifiers(Lnet/minecraft/entity/EquipmentSlot;)Lcom/google/common/collect/Multimap;"))
	public Multimap<String, EntityAttributeModifier> actWriteModifiers(ItemStack stack, EquipmentSlot slot) {
		return actModifiers(stack, slot, actor);
	}

	/**
	 * @author B0undarybreaker
	 * @reason get entity attribute modifiers for actable tools
	 */
	@Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getAttributeModifiers(Lnet/minecraft/entity/EquipmentSlot;)Lcom/google/common/collect/Multimap;"))
	public Multimap<String, EntityAttributeModifier> actTickModifiers(ItemStack stack, EquipmentSlot slot) {
		return actModifiers(stack, slot, actor);
	}

	private static Multimap<String, EntityAttributeModifier> actModifiers(ItemStack stack, EquipmentSlot slot, ToolActor actor) {
		Multimap<String, EntityAttributeModifier> original = stack.getAttributeModifiers(slot);

		if (stack.getItem() instanceof ActableAttributeHolder) {
			ActableAttributeHolder holder = (ActableAttributeHolder)stack.getItem();
			return (AttributeManager.mergeAttributes(original, holder.getDynamicModifiers(slot, stack, actor)));
		}

		return original;
	}
}
