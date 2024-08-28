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

package net.fabricmc.fabric.impl.attachment.sync;

import java.util.Objects;
import java.util.function.BiPredicate;

import org.jetbrains.annotations.Nullable;

import net.minecraft.server.network.ServerPlayerEntity;

import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;

public record AttachmentSyncPredicateImpl(
		SyncType type,
		@Nullable BiPredicate<AttachmentTarget, ServerPlayerEntity> customTest
) implements AttachmentSyncPredicate {
	private static final AttachmentSyncPredicateImpl ALL = new AttachmentSyncPredicateImpl(SyncType.ALL, null);
	private static final AttachmentSyncPredicateImpl TARGET_ONLY = new AttachmentSyncPredicateImpl(
			SyncType.TARGET_ONLY,
			null
	);
	private static final AttachmentSyncPredicateImpl ALL_BUT_TARGET = new AttachmentSyncPredicateImpl(
			SyncType.ALL_BUT_TARGET,
			null
	);

	public static AttachmentSyncPredicate all() {
		return ALL;
	}

	public static AttachmentSyncPredicate targetOnly() {
		return TARGET_ONLY;
	}

	public static AttachmentSyncPredicate allButTarget() {
		return ALL_BUT_TARGET;
	}

	public static AttachmentSyncPredicate custom(BiPredicate<AttachmentTarget, ServerPlayerEntity> customTest) {
		Objects.requireNonNull(customTest, "target test predicate cannot be null");

		return new AttachmentSyncPredicateImpl(SyncType.CUSTOM, customTest);
	}

	public boolean isCustom() {
		return type == SyncType.CUSTOM;
	}
}
