package net.fabricmc.fabric.test.item.gametest;

import java.util.List;
import java.util.Optional;

import net.fabricmc.fabric.test.item.CustomEnchantmentEffectsTest;

import net.minecraft.component.EnchantmentEffectComponentTypes;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.enchantment.effect.EnchantmentEffectEntry;
import net.minecraft.enchantment.effect.EnchantmentValueEffect;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.test.GameTest;
import net.minecraft.test.GameTestException;
import net.minecraft.test.TestContext;

import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;

import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameMode;

public class CustomEnchantmentEffectGameTest implements FabricGameTest {
	@GameTest(templateName = "fabric-item-api-v1-testmod:bedrock_platform")
	public void weirdImpalingSetsFireToTargets(TestContext context) {
		BlockPos pos = new BlockPos(3, 3, 3);
		CreeperEntity creeper = context.spawnEntity(EntityType.CREEPER, pos);
		PlayerEntity player = context.createMockPlayer(GameMode.CREATIVE);

		ItemStack trident = Items.TRIDENT.getDefaultStack();
		Optional<RegistryEntry.Reference<Enchantment>> impaling = getEnchantmentRegistry(context)
				.getEntry(CustomEnchantmentEffectsTest.WEIRD_IMPALING);
		if (impaling.isEmpty()) {
			throw new GameTestException("Weird Impaling enchantment is not present");
		}

		trident.addEnchantment(impaling.get(), 1);

		player.setStackInHand(Hand.MAIN_HAND, trident);

		context.expectEntityWithData(pos, EntityType.CREEPER, Entity::isOnFire, false);
		player.attack(creeper);
		context.expectEntityWithDataEnd(pos, EntityType.CREEPER, Entity::isOnFire, true);
	}

	@GameTest(templateName = EMPTY_STRUCTURE)
	public void weirdImpalingHasTwoDamageEffects(TestContext context) {
		Enchantment impaling = getEnchantmentRegistry(context).get(CustomEnchantmentEffectsTest.WEIRD_IMPALING);
		if (impaling == null) {
			throw new GameTestException("Weird Impaling enchantment is not present");
		}

		List<EnchantmentEffectEntry<EnchantmentValueEffect>> damageEffects = impaling
				.getEffect(EnchantmentEffectComponentTypes.DAMAGE);

		context.assertTrue(damageEffects.size() == 2, "Weird Impaling does not have two damage effects");
		context.complete();
	}

	private static Registry<Enchantment> getEnchantmentRegistry(TestContext context) {
		DynamicRegistryManager registryManager = context.getWorld().getRegistryManager();
		return registryManager.get(RegistryKeys.ENCHANTMENT);
	}
}
