package net.fabricmc.fabric.mixin.item;

import com.google.common.collect.ImmutableMultimap;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributeModifier;

import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ArmorMaterials;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.item.ArmorItem;

import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.UUID;

@Mixin(ArmorItem.class)
public class ArmorItemMixin
{
	@Shadow private static @Final UUID[] MODIFIERS;
	@Shadow protected @Final float knockbackResistance;

	@ModifyVariable(method = "<init>", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/ImmutableMultimap$Builder;build()Lcom/google/common/collect/ImmutableMultimap;"))
	private ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> fabric_knockbackResistance(ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> builder, ArmorMaterial material, EquipmentSlot slot) {
		// Vanilla handles netherite
		if (material != ArmorMaterials.NETHERITE && knockbackResistance > 0.0F) {
			builder.put(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, new EntityAttributeModifier(
					MODIFIERS[slot.getEntitySlotId()], "Armor knockback resistance",
					knockbackResistance, EntityAttributeModifier.Operation.ADDITION));
		}
		return builder;
	}
}
