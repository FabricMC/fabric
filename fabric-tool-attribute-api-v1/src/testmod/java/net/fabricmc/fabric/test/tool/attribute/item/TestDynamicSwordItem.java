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
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterials;

import net.fabricmc.fabric.api.tool.attribute.v1.DynamicAttributeTool;

public class TestDynamicSwordItem extends SwordItem implements DynamicAttributeTool {
	public static final UUID TEST_UUID = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");

	public TestDynamicSwordItem(Settings settings) {
		super(ToolMaterials.DIAMOND, 6, -2.4f, settings);
	}

	@Override
	public Multimap<EntityAttribute, EntityAttributeModifier> getDynamicModifiers(EquipmentSlot slot, ItemStack stack, @Nullable LivingEntity user) {
		if (slot.equals(EquipmentSlot.MAINHAND)) {
			ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> builder = ImmutableMultimap.builder();
			builder.put(EntityAttributes.GENERIC_ATTACK_DAMAGE, new EntityAttributeModifier(TEST_UUID, "TEST", 2.0, EntityAttributeModifier.Operation.MULTIPLY_TOTAL));
			builder.put(EntityAttributes.GENERIC_ATTACK_DAMAGE, new EntityAttributeModifier(TEST_UUID, "TEST2", 2.0, EntityAttributeModifier.Operation.MULTIPLY_TOTAL));
			return builder.build();
		} else {
			return EMPTY;
		}
	}
}
