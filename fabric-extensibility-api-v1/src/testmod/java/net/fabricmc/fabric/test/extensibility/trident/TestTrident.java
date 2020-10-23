/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.fabricmc.fabric.test.extensibility.trident;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import net.fabricmc.fabric.api.extensibility.item.v1.trident.SimpleTridentItem;
import net.fabricmc.fabric.api.extensibility.item.v1.trident.SimpleTridentItemEntity;

/**
 * This trident allows the user to riptide from lava and burns entities when thrown at them.
 */
public class TestTrident extends SimpleTridentItem {
	public TestTrident(Item.Settings settings) {
		super(settings, new Identifier("fabric-extensibility-api-v1-testmod", "textures/entity/test_trident.png"));
	}

	@Override
	public TridentEntity getTridentEntity(TridentEntity trident) {
		return new SimpleTridentItemEntity(trident) {
			@Override
			protected void onHit(LivingEntity target) {
				super.onHit(target);
				target.setOnFireFor(5);
			}
		};
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		ItemStack itemStack = user.getStackInHand(hand);

		if (itemStack.getDamage() >= itemStack.getMaxDamage() - 1) {
			return TypedActionResult.fail(itemStack);
		} else if (EnchantmentHelper.getRiptide(itemStack) > 0 && !(user.isTouchingWaterOrRain() || user.isInLava())) {
			return TypedActionResult.fail(itemStack);
		} else {
			user.setCurrentHand(hand);
			return TypedActionResult.consume(itemStack);
		}
	}

	@Override
	public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
		if (user instanceof PlayerEntity) {
			PlayerEntity playerEntity = (PlayerEntity) user;
			int i = getMaxUseTime(stack) - remainingUseTicks;

			if (i >= 10) {
				int riptideLevel = EnchantmentHelper.getRiptide(stack);

				if (riptideLevel <= 0 || playerEntity.isTouchingWaterOrRain() || playerEntity.isInLava()) {
					if (!world.isClient) {
						stack.damage(1, playerEntity, (p) -> p.sendToolBreakStatus(user.getActiveHand()));

						if (riptideLevel == 0) {
							TridentEntity tridentEntity = new TridentEntity(world, playerEntity, stack);
							tridentEntity.setProperties(playerEntity, playerEntity.pitch, playerEntity.yaw, 0.0F, 2.5F + riptideLevel * 0.5F, 1.0F);

							if (playerEntity.abilities.creativeMode) {
								tridentEntity.pickupType = PersistentProjectileEntity.PickupPermission.CREATIVE_ONLY;
							}

							tridentEntity = this.getTridentEntity(tridentEntity);
							world.spawnEntity(tridentEntity);
							world.playSoundFromEntity(null, tridentEntity, SoundEvents.ITEM_TRIDENT_THROW, SoundCategory.PLAYERS, 1.0F, 1.0F);

							if (!playerEntity.abilities.creativeMode) {
								playerEntity.inventory.removeOne(stack);
							}
						}
					}

					playerEntity.incrementStat(Stats.USED.getOrCreateStat(this));

					if (riptideLevel > 0) {
						float f = playerEntity.yaw;
						float g = playerEntity.pitch;
						float h = -MathHelper.sin(f * 0.017453292F) * MathHelper.cos(g * 0.017453292F);
						float k = -MathHelper.sin(g * 0.017453292F);
						float l = MathHelper.cos(f * 0.017453292F) * MathHelper.cos(g * 0.017453292F);
						float m = MathHelper.sqrt(h * h + k * k + l * l);
						float n = 3.0F * ((1.0F + riptideLevel) / 4.0F);
						h *= n / m;
						k *= n / m;
						l *= n / m;
						playerEntity.addVelocity(h, k, l);
						playerEntity.setRiptideTicks(20);

						if (playerEntity.isOnGround()) {
							float o = 1.1999999F;
							playerEntity.move(MovementType.SELF, new Vec3d(0.0D, o, 0.0D));
						}

						SoundEvent soundEvent3;

						if (riptideLevel >= 3) {
							soundEvent3 = SoundEvents.ITEM_TRIDENT_RIPTIDE_3;
						} else if (riptideLevel == 2) {
							soundEvent3 = SoundEvents.ITEM_TRIDENT_RIPTIDE_2;
						} else {
							soundEvent3 = SoundEvents.ITEM_TRIDENT_RIPTIDE_1;
						}

						world.playSoundFromEntity(null, playerEntity, soundEvent3, SoundCategory.PLAYERS, 1.0F, 1.0F);
					}
				}
			}
		}
	}
}
