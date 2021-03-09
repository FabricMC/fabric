package net.fabricmc.fabric.mixin.tool.attribute;

import java.util.Map;

import com.google.common.collect.Multimap;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.ItemStack;

import net.fabricmc.fabric.api.tool.attribute.v1.ItemStackContext;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
	@Nullable
	@Unique private ItemStack stackContext = null;
	@Nullable
	@Unique private EquipmentSlot slotContext = null;

	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/attribute/AttributeContainer;removeModifiers(Lcom/google/common/collect/Multimap;)V"), method = "method_30129", locals = LocalCapture.CAPTURE_FAILHARD)
	private void storeRemoveStackContext(CallbackInfoReturnable<Map> cir, Map map, EquipmentSlot[] var2, int var3, int var4, EquipmentSlot equipmentSlot, ItemStack oldStack, ItemStack newStack) {
		stackContext = oldStack;
		slotContext = equipmentSlot;
	}

	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/attribute/AttributeContainer;removeModifiers(Lcom/google/common/collect/Multimap;)V"), method = "method_30129")
	private void setupRemoveModifierContext(AttributeContainer attributeContainer, Multimap<EntityAttribute, EntityAttributeModifier> oldModifiers) {
		((ItemStackContext) (Object) stackContext).fabricToolAttributes_setContext((LivingEntity) (Object) this);
		Multimap<EntityAttribute, EntityAttributeModifier> attributeModifiers = stackContext.getAttributeModifiers(slotContext);
		((ItemStackContext) (Object) stackContext).fabricToolAttributes_setContext(null);
		attributeContainer.removeModifiers(attributeModifiers);
	}

	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/attribute/AttributeContainer;addTemporaryModifiers(Lcom/google/common/collect/Multimap;)V"), method = "method_30129", locals = LocalCapture.CAPTURE_FAILHARD)
	private void storeAddStackContext(CallbackInfoReturnable<Map> cir, Map map, EquipmentSlot[] var2, int var3, int var4, EquipmentSlot equipmentSlot, ItemStack oldStack, ItemStack newStack) {
		stackContext = newStack;
		slotContext = equipmentSlot;
	}

	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/attribute/AttributeContainer;addTemporaryModifiers(Lcom/google/common/collect/Multimap;)V"), method = "method_30129")
	private void setupAddModifierContext(AttributeContainer attributeContainer, Multimap<EntityAttribute, EntityAttributeModifier> oldModifiers) {
		((ItemStackContext) (Object) stackContext).fabricToolAttributes_setContext((LivingEntity) (Object) this);
		Multimap<EntityAttribute, EntityAttributeModifier> attributeModifiers = stackContext.getAttributeModifiers(slotContext);
		((ItemStackContext) (Object) stackContext).fabricToolAttributes_setContext(null);
		attributeContainer.addTemporaryModifiers(attributeModifiers);
	}
}
