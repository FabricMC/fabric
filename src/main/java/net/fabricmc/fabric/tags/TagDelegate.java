package net.fabricmc.fabric.tags;

import net.minecraft.item.Item;
import net.minecraft.tag.ItemTags;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

import java.util.Collection;

public class TagDelegate<T> extends Tag<T> {
	protected Tag<T> delegate;

	public TagDelegate(Identifier id, Tag<T> delegate) {
		super(id);
		this.delegate = delegate;
	}

	protected void onAccess() {

	}

	@Override
	public boolean contains(T var1) {
		onAccess();
		return delegate.contains(var1);
	}

	@Override
	public Collection<T> values() {
		onAccess();
		return delegate.values();
	}

	@Override
	public Collection<Tag.Entry<T>> entries() {
		onAccess();
		return delegate.entries();
	}
}
