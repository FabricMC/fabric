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

package net.fabricmc.fabric.test.entity.event;

import static net.minecraft.server.command.CommandManager.literal;

import java.util.ArrayList;
import java.util.List;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import net.minecraft.util.registry.Registry;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.EntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.LivingEntityEvents;

public final class EntityEventTests implements ModInitializer {
	private static final Logger LOGGER = LogManager.getLogger(EntityEventTests.class);
	private boolean testing = false;
	private List<EntityType<?>> successfulTests = new ArrayList<>();

	@Override
	public void onInitialize() {
		CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
			dispatcher.register(literal("livingentity_after_damage_tests").executes(this::executeTest));
		});

		LivingEntityEvents.AFTER_DAMAGED.register((entity, damageSource, damageAmount, originalHeath) -> {
			if (this.testing) {
				this.successfulTests.add(entity.getType());
			}

			LOGGER.info("Damaged {}", entity);
		});

		EntityEvents.AFTER_KILLED_OTHER.register((world, entity, killed) -> {
			LOGGER.info("[Killed]: {}", killed);
		});
	}

	private int executeTest(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		final ServerCommandSource source = context.getSource();
		source.getPlayer(); // Only players can do this

		// Set up testing
		this.successfulTests.clear();
		this.testing = true;

		// Summon every single entity from every type, damage it and check if the event was fired.
		for (EntityType<?> entityType : Registry.ENTITY_TYPE) {
			// Players cannot be summoned, must manually be tested
			if (entityType.equals(EntityType.PLAYER)) {
				source.sendFeedback(new LiteralText("Please damage yourself as we can't automate damaging player entities because we can't summon players").styled(style -> style.withColor(Formatting.YELLOW)), false);
				continue;
			}

			if (entityType.equals(EntityType.ENDER_DRAGON)) {
				// Ender dragon is special case, needs a manual test
				//living.damage(DamageSource.explosion(source.getPlayer()), 1.0F);
				source.sendFeedback(new LiteralText("Please test ender dragon manually as automated tests do not work on it.").styled(style -> style.withColor(Formatting.YELLOW)), false);
				continue;
			}

			final Entity entity = entityType.create(source.getWorld());

			if (entity == null) {
				source.sendFeedback(new LiteralText(String.format("Cannot summon entity %s, not summonable", Registry.ENTITY_TYPE.getId(entityType).toString())), false);
				source.sendFeedback(new LiteralText("Fishing Bobbers are not living, therefore cannot be damaged."), false);
				continue;
			}

			// Only test on living entities
			if (entity instanceof LivingEntity) {
				final LivingEntity living = (LivingEntity) entity;
				living.damage(DamageSource.GENERIC, 1.0F);

				if (!this.successfulTests.contains(living.getType())) {
					source.sendError(new LiteralText(String.format("Failed to capture event for entity of type %s", Registry.ENTITY_TYPE.getId(entityType).toString())));
				}

				// Just to cycle the entity ids.
				source.getWorld().spawnEntity(entity);
				source.getWorld().removeEntity(entity);

				// Finally remove the entity
				living.remove();
			}
		}

		this.testing = false;

		return 1;
	}
}
