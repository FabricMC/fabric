package net.fabricmc.fabric.test.shield;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.registry.ShieldRegistry;

public class ShieldTest implements ModInitializer {
	public static final Item SHIELD = new Item(new Item.Settings().maxDamage(200)) {
		// These are the same as ShieldItem

		@Override
		public UseAction getUseAction(ItemStack stack) {
			return UseAction.BLOCK;
		}

		@Override
		public int getMaxUseTime(ItemStack stack) {
			return 72000;
		}

		@Override
		public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
			ItemStack itemstack = player.getStackInHand(hand);
			player.setCurrentHand(hand);
			return TypedActionResult.consume(itemstack);
		}
	};

	@Override
	public void onInitialize() {
		Registry.register(Registry.ITEM, new Identifier("fabric", "shield"), SHIELD);
		ShieldRegistry.INSTANCE.add(SHIELD);
	}
}
