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

package net.fabricmc.fabric.impl.datafixer.fixes;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.util.Pair;

import net.minecraft.datafixers.fixes.EntitySimpleTransformFix;
import net.minecraft.nbt.Tag;

import net.fabricmc.fabric.api.datafixer.v1.SimpleFixes.EntityTransformation;

/**
 * <b>:thonkjang:</b> For some odd reason {@link #transform(String, Dynamic)} fails to accept Tag as the generic and now we have this class.
 */
public class EntityTransformationFixWrapper extends EntitySimpleTransformFix {
	private EntityTransformation transformation;

	public EntityTransformationFixWrapper(String name, Schema schema, boolean fixType, EntityTransformation transformation) {
		super(name, schema, fixType);
		this.transformation = transformation;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Pair<String, Dynamic<?>> transform(String entityName, Dynamic<?> dynamic) {
		Pair<String, Dynamic<Tag>> resultingEntity = transformation.transform(entityName, (Dynamic<Tag>) dynamic);
		return (Pair<String, Dynamic<?>>) (Object) resultingEntity; // Least kosher generic cast I've ever seen but trust me, it does work.
	}
}
