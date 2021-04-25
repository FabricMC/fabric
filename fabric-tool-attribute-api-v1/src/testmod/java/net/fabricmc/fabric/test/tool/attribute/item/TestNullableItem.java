package net.fabricmc.fabric.test.tool.attribute.item;

import java.util.UUID;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import org.jetbrains.annotations.Nullable;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import net.fabricmc.fabric.api.tool.attribute.v1.DynamicAttributeTool;

public class TestNullableItem extends Item implements DynamicAttributeTool {
	public static final UUID TEST_UUID = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");

	public TestNullableItem(Settings settings) {
		super(settings);
	}

	@Override
	public Multimap<EntityAttribute, EntityAttributeModifier> getDynamicModifiers(EquipmentSlot slot, ItemStack stack, @Nullable LivingEntity user) {
		if (slot.equals(EquipmentSlot.MAINHAND)) {
			ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> builder = ImmutableMultimap.builder();
			builder.put(EntityAttributes.GENERIC_MOVEMENT_SPEED, new EntityAttributeModifier(TEST_UUID, "Increasing speed", user == null ? 0 : user.age * 0.001, EntityAttributeModifier.Operation.ADDITION));
			return builder.build();
		} else {
			return EMPTY;
		}
	}
}
