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

package net.fabricmc.indigo;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.indigo.renderer.IndigoRenderer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Indigo implements ClientModInitializer {
	private static final Logger LOGGER = LogManager.getLogger();

    @Override
    public void onInitializeClient() {
    	if (IndigoMixinConfigPlugin.shouldApplyIndigo()) {
		    LOGGER.info("Loading Indigo renderer!");
		    RendererAccess.INSTANCE.registerRenderer(IndigoRenderer.INSTANCE);
	    } else {
    		LOGGER.info("Different rendering plugin detected; not applying Indigo.");
	    }
    }
}
