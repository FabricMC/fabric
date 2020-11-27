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

package net.fabricmc.fabric.api.gamerule.v1.rule;

/**
 * A type of game rule which can validate an input.
 * This can be used to enforce syntax or clamp values.
 */
public interface ValidateableRule {
	/**
	 * Validates if a rule can accept the input.
	 *
	 * @param value the value to validate
	 * @return true if the value can be accepted.
	 */
	boolean validate(String value);
}
