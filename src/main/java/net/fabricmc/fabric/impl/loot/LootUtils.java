
package net.fabricmc.fabric.impl.loot;


import com.google.gson.Gson;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.Lazy;
import net.minecraft.world.loot.LootManager;
import net.minecraft.world.loot.LootSupplier;

import java.lang.reflect.Field;
import java.util.stream.Stream;

// TODO: (BEFORE MERGE)
//   1) Should this go in api or impl?
//   2) Should the Gson instance be exposed?
//   3) Is LootUtils a good name for this class?
public final class LootUtils {
	/* Reading this from LootManager to access all serializers from vanilla. */
	private static final Lazy<Gson> GSON = new Lazy<>(() -> {
		try {
			Field gsonField = Stream.of(LootManager.class.getDeclaredFields())
					.filter(field -> field.getType() == Gson.class)
					.findFirst()
					.orElseThrow(() -> new RuntimeException("Gson not found in LootManager!"));
			gsonField.setAccessible(true);
			return (Gson) gsonField.get(null);
		} catch (Exception e) {
			throw new RuntimeException("Exception while getting Gson instance from LootManager", e);
		}
	});

	public static Gson gson() {
		return GSON.get();
	}

	public static LootSupplier readSupplierFromJson(String json) {
		return JsonHelper.deserialize(gson(), json, LootSupplier.class);
	}
}
