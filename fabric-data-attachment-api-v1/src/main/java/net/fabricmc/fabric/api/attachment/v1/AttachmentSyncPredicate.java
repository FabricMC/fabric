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

package net.fabricmc.fabric.api.attachment.v1;

import java.util.function.BiPredicate;

import org.jetbrains.annotations.ApiStatus;

import net.minecraft.server.network.ServerPlayerEntity;

import net.fabricmc.fabric.impl.attachment.sync.AttachmentSyncPredicateImpl;

@ApiStatus.NonExtendable
public interface AttachmentSyncPredicate {
	/**
	 * @return a predicate that syncs an attachment with all clients
	 */
	static AttachmentSyncPredicate all() {
		return AttachmentSyncPredicateImpl.all();
	}

	/**
	 * @return a predicate that syncs an attachment only with the target it is attached to, when that is a player
	 */
	static AttachmentSyncPredicate targetOnly() {
		return AttachmentSyncPredicateImpl.targetOnly();
	}

	/**
	 * @return a predicate that syncs an attachment with every client except the target it is attached to, when that is a player
	 */
	static AttachmentSyncPredicate allButTarget() {
		return AttachmentSyncPredicateImpl.allButTarget();
	}

	/**
	 * @param syncTargetTest a custom predicate, taking in the target of the attachment and a {@link ServerPlayerEntity player}
	 * @return a custom predicate to determine if the attachment should be synced with a given player
	 */
	static AttachmentSyncPredicate custom(BiPredicate<AttachmentTarget, ServerPlayerEntity> syncTargetTest) {
		return AttachmentSyncPredicateImpl.custom(syncTargetTest);
	}
}
