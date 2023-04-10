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

package net.fabricmc.fabric.api.entity;

import java.util.Map;
import java.util.Objects;

import com.google.common.collect.MapMaker;
import com.mojang.authlib.GameProfile;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.network.packet.c2s.play.ClientSettingsC2SPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stat;
import net.minecraft.text.Text;

import net.fabricmc.fabric.impl.event.interaction.FakePlayerNetworkHandler;

/**
 * A "fake player", i.e. a {@link ServerPlayerEntity} that is not a human player.
 * They are typically used to automatically perform player actions such as placing blocks.
 *
 * <p>Fake players can be obtained with {@link FakePlayer#get(ServerWorld, GameProfile)}.
 * For good inter-mod compatibility, fake players should have the UUID of their owning (human) player.
 *
 * <p>Fake players try to behave like regular {@link ServerPlayerEntity} objects to a reasonable extent.
 * In some edge cases, or for gameplay considerations, it might be necessary to check whether a {@link ServerPlayerEntity} is a fake player.
 * This can be done with an {@code instanceof} check: {@code player instanceof FakePlayer}.
 */
// TODO: to finalize or not to finalize?
public final class FakePlayer extends ServerPlayerEntity {
	/**
	 * Retrieve the fake player for the specified world and game profile, or create it if it doesn't exist.
	 *
	 * <p>Caution should be exerted when storing the returned value, as strong references to the fake player will keep the world loaded.
	 */
	public static FakePlayer get(ServerWorld world, GameProfile profile) {
		Objects.requireNonNull(world, "World may not be null.");
		Objects.requireNonNull(profile, "Game profile may not be null.");

		return FAKE_PLAYER_MAP.computeIfAbsent(new FakePlayerKey(world, profile), FakePlayer::new);
	}

	private record FakePlayerKey(ServerWorld world, GameProfile profile) { }
	private static final Map<FakePlayerKey, FakePlayer> FAKE_PLAYER_MAP = new MapMaker().weakValues().makeMap();

	private FakePlayer(FakePlayerKey key) {
		super(key.world().getServer(), key.world(), key.profile());

		this.networkHandler = new FakePlayerNetworkHandler(this);
	}

	@Override
	public void tick() { }

	@Override
	public void sendMessageToClient(Text message, boolean overlay) { }

	@Override
	public void setClientSettings(ClientSettingsC2SPacket packet) { }

	@Override
	public void increaseStat(Stat<?> stat, int amount) { }

	@Override
	public void resetStat(Stat<?> stat) { }

	@Override
	public boolean isInvulnerableTo(DamageSource damageSource) {
		return true;
	}
}
