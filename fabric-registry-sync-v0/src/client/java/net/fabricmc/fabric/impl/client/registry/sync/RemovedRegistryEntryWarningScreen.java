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

package net.fabricmc.fabric.impl.client.registry.sync;

import java.util.Collection;
import java.util.stream.Collectors;

import net.minecraft.client.gui.screen.BackupPromptScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public final class RemovedRegistryEntryWarningScreen extends BackupPromptScreen {
	private static final int DISPLAYED_NAMESPACES = 5;
	private static final Text TITLE = Text.translatable("fabric-registry-sync-v0.screen.removed-registry.title");
	private static final Text SUBTITLE = Text.translatable("fabric-registry-sync-v0.screen.removed-registry.subtitle.1")
			.append(Text.translatable("fabric-registry-sync-v0.screen.removed-registry.subtitle.2"))
			.append(Text.translatable("fabric-registry-sync-v0.screen.removed-registry.subtitle.3"))
			.append(Text.translatable("fabric-registry-sync-v0.screen.removed-registry.subtitle.4"));

	public RemovedRegistryEntryWarningScreen(Screen parent, Callback callback, Collection<String> namespaces) {
		super(parent, callback, TITLE, getSubtitle(namespaces), false);
	}

	private static Text getSubtitle(Collection<String> namespaces) {
		if (namespaces.size() <= DISPLAYED_NAMESPACES) {
			return SUBTITLE.copy().append(String.join(", ", namespaces));
		}

		return SUBTITLE
				.copy()
				.append(namespaces.stream().limit(DISPLAYED_NAMESPACES).collect(Collectors.joining(", ")))
				.append("\n")
				.append(Text.translatable("fabric-registry-sync-v0.screen.removed-registry.footer", namespaces.size() - DISPLAYED_NAMESPACES));
	}
}
