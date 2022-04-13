package net.fabricmc.fabric.test.item;

import net.fabricmc.api.ModInitializer;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ArmorKnockbackResistanceTest implements ModInitializer {
	private static final ArmorMaterial WOOD_ARMOR = new ArmorMaterial() {
		@Override
		public int getDurability(EquipmentSlot slot) {
			return 50;
		}

		@Override
		public int getProtectionAmount(EquipmentSlot slot) {
			return 5;
		}

		@Override
		public int getEnchantability() {
			return 1;
		}

		@Override
		public SoundEvent getEquipSound() {
			return SoundEvents.ITEM_ARMOR_EQUIP_GENERIC;
		}

		@Override
		public Ingredient getRepairIngredient() {
			return Ingredient.fromTag(ItemTags.LOGS);
		}

		@Override
		public String getName() {
			return "wood";
		}

		@Override
		public float getToughness() {
			return 0.0F;
		}

		@Override
		public float getKnockbackResistance() {
			return 0.5F;
		}
	};

	@Override
	public void onInitialize() {
		Registry.register(Registry.ITEM, new Identifier("fabric-item-api-v1-testmod",
				"wooden_boots"), new ArmorItem(WOOD_ARMOR, EquipmentSlot.FEET, new Item.Settings()));
	}
}
