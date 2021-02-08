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

package net.fabricmc.fabric.mixin.interaction;

import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import net.minecraft.world.RaycastContext;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.entity.Entity;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.hit.EntityHitResult;

import net.fabricmc.fabric.api.interaction.v1.event.player.AirInteractionAccuracy;
import net.fabricmc.fabric.api.interaction.v1.event.player.ServerPlayerEntityInteractEvents;
import net.fabricmc.fabric.impl.interaction.InteractionRaycasting;
import net.fabricmc.fabric.impl.interaction.ServerPlayNetworkHandlerExtensions;

@Mixin(ServerPlayNetworkHandler.class)
abstract class ServerPlayNetworkHandlerMixin implements ServerPlayNetworkHandlerExtensions {
	@Shadow
	public ServerPlayerEntity player;

	@Unique
	private AirInteractionAccuracy interactionAccuracy = AirInteractionAccuracy.LIKELY;

	// Allow & Before entity interact
	@Inject(method = "onPlayerInteractEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;interactAt(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/ActionResult;"), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
	private void handleEntityInteraction(PlayerInteractEntityC2SPacket packet, CallbackInfo info, ServerWorld world, @Nullable Entity target) {
		// Target may be null if the client maliciously sends a packet to interact with a non-existent entity
		if (target != null) {
			final EntityHitResult hit = new EntityHitResult(target, packet.getHitPosition().add(target.getX(), target.getY(), target.getZ()));

			if (!ServerPlayerEntityInteractEvents.ALLOW.invoker().allowEntityInteraction(this.player, this.player.getServerWorld(), packet.getHand(), target, hit)) {
				info.cancel();
				// TODO: Cancel event needed?
				return;
			}

			ServerPlayerEntityInteractEvents.BEFORE.invoker().beforeEntityInteraction(this.player, this.player.getServerWorld(), packet.getHand(), target, hit);
		}
	}

	// After entity interact
	@Inject(method = "onPlayerInteractEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;interactAt(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/ActionResult;", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
	private void handleAfterEntityInteraction(PlayerInteractEntityC2SPacket packet, CallbackInfo info, ServerWorld world, @Nullable Entity target) {
		// Target may be null if the client maliciously sends a packet to attack a non-existent entity
		if (target != null) {
			final EntityHitResult hit = new EntityHitResult(target, packet.getHitPosition().add(target.getX(), target.getY(), target.getZ()));

			ServerPlayerEntityInteractEvents.AFTER.invoker().afterEntityInteraction(this.player, this.player.getServerWorld(), packet.getHand(), target, hit);
		}
	}

	/*
	 * Yes Minecraft sends no packet to detect attacking air, but it can be predicted smartly.
	 * So we have several routes we can take here.
	 * We install a sort of "accuracy level" with the type of action that is occurring.
	 * Clients which have fabric api installed (atleast the interaction-events-v1 module) or dispatch the required packets will have a "definite" accuracy level that fires when handling an attack air event.
	 */
	// TODO: Test me!
	@Inject(method = "onHandSwing", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;swingHand(Lnet/minecraft/util/Hand;)V"))
	private void handleAttackAirEvent(HandSwingC2SPacket packet, CallbackInfo ci) {
		// The client has sent a definitive attack packet to the server so we are okay.
		if (this.fabric_getInteractionAccuracy() == AirInteractionAccuracy.DEFINITE && this.fabric_handledDefiniteEvent()) {
			return;
		}

		// Not a Fabric API client, predict how likely it was that air was attacked
		Vec3d eyePos = InteractionRaycasting.getEyePos(this.player);
		double reachDistance = InteractionRaycasting.getReachDistance(this.player);
		Vec3d endEyePos = InteractionRaycasting.getEyeEndPos(this.player, reachDistance);

		RaycastContext context = new RaycastContext(
				eyePos,
				endEyePos,
				RaycastContext.ShapeType.OUTLINE, // Hand interactions use outline
				RaycastContext.FluidHandling.NONE,
				this.player
		);

		BlockHitResult result = this.player.getServerWorld().raycast(context);

		// If the raycast fails or the result is not a block then it is likely that air was attacked
		if (result == null || result.getType() != HitResult.Type.BLOCK) {
			// Likely that the player has tried attacking air - fire events
			// TODO fire events
		}
	}

	@Inject(method = "onPlayerInteractItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerInteractionManager;interactItem(Lnet/minecraft/server/network/ServerPlayerEntity;Lnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/ActionResult;"))
	private void handleUseAir(PlayerInteractItemC2SPacket packet, CallbackInfo ci) {
		// This is always called when a player has an item in their hand.
		// This is NOT called if the player's hand is empty
		// The only way to fire an event with empty hands is to have a fabric api client.
		// Raycast and see if we are interacting with a block or air

		// The client has sent a definitive interact packet to the server so we are okay.
		if (this.fabric_getInteractionAccuracy() == AirInteractionAccuracy.DEFINITE && this.fabric_handledDefiniteEvent()) {
			return;
		}

		Vec3d eyePos = InteractionRaycasting.getEyePos(this.player);
		double reachDistance = InteractionRaycasting.getReachDistance(this.player);
		Vec3d endEyePos = InteractionRaycasting.getEyeEndPos(this.player, reachDistance);

		RaycastContext context = new RaycastContext(
				eyePos,
				endEyePos,
				RaycastContext.ShapeType.OUTLINE, // Hand interactions use outline
				RaycastContext.FluidHandling.NONE,
				this.player
		);

		BlockHitResult result = this.player.getServerWorld().raycast(context);

		if (result == null || result.getType() != HitResult.Type.BLOCK) {
			// We ARE interacting with air and have an item in hand.
			// TODO fire event with LIKELY
		}
	}

	// Duck interface implementations

	@Override
	public AirInteractionAccuracy fabric_getInteractionAccuracy() {
		return this.interactionAccuracy;
	}

	@Override
	public void fabric_setInteractionAccuracy(AirInteractionAccuracy accuracy) {
		this.interactionAccuracy = accuracy;
	}
}
