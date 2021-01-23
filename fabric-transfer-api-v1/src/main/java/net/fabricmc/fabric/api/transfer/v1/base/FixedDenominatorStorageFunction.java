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

package net.fabricmc.fabric.api.transfer.v1.base;

import com.google.common.math.LongMath;

import net.fabricmc.fabric.api.transfer.v1.storage.StorageFunction;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;

public interface FixedDenominatorStorageFunction<T> extends StorageFunction<T> {
	long denominator();
	long applyFixedDenominator(T resource, long numerator, Transaction tx);

	@Override
	default long apply(T resource, long amount, Transaction tx) {
		return apply(resource, amount, 1, tx);
	}

	@Override
	default long apply(T resource, long numerator, long denominator, Transaction tx) {
		// TODO: gracefully handle overflow
		long ownDenom = denominator();

		if (denominator % ownDenom == 0) {
			// if the passed denominator is a multiple of this denominator, handling is trivial
			long ratio = denominator / ownDenom;
			return applyFixedDenominator(resource, numerator / ratio, tx) * ratio;
		} else {
			// otherwise, the transfer will necessarily happen with the gcd of the denominators
			long g = LongMath.gcd(ownDenom, denominator);
			long factor = denominator / g;
			long ownFactor = ownDenom / g;
			long commonAmount = numerator / factor;

			// the first try uses commonAmount, and returns if it is successful
			// the second try uses the rounded-down amount returned by the first try
			for (int tries = 0; tries < 2 && commonAmount > 0; ++tries) {
				try (Transaction subtx = Transaction.open()) {
					// try to apply with the common amount
					long result = applyFixedDenominator(resource, commonAmount * ownFactor, subtx);

					// if the result can be converted back to the gcd, this is successful.
					if (result % ownFactor == 0) {
						subtx.commit();
						return result / ownFactor * factor;
					} else {
						// otherwise, rollback and try rounding down
						commonAmount = result / ownFactor;
					}
				}
			}

			return 0;
		}
	}
}
