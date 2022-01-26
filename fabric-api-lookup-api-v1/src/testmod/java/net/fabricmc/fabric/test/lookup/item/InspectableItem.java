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

package net.fabricmc.fabric.test.lookup.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

public class InspectableItem extends Item implements Inspectable {
	private final String inspectionResult;

	public InspectableItem(String inspectionResult) {
		super(new Settings().group(ItemGroup.MISC));
		this.inspectionResult = inspectionResult;
	}

	@Override
	public Text inspect() {
		return new LiteralText(inspectionResult);
	}
}
