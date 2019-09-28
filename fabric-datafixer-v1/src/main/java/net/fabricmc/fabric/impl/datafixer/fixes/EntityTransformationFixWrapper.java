package net.fabricmc.fabric.impl.datafixer.fixes;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.util.Pair;

import net.fabricmc.fabric.api.datafixer.v1.SimpleFixes.EntityTransformation;
import net.minecraft.datafixers.fixes.EntitySimpleTransformFix;
import net.minecraft.nbt.Tag;
/**
 * <b>:thonkjang:</b> For some odd reason {@link #transform(String, Dynamic)} fails to accept Tag as the generic and now we have this class
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
		return (Pair<String, Dynamic<?>>)(Object) resultingEntity;
	}

}
