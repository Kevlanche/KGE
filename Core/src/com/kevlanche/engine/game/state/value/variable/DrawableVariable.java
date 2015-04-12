package com.kevlanche.engine.game.state.value.variable;

import com.kevlanche.engine.game.assets.AssetProvider;
import com.kevlanche.engine.game.assets.Drawable;
import com.kevlanche.engine.game.state.value.ValueType;

public class DrawableVariable extends AbstractVariable {

	private Drawable mDrawable;
	private final AssetProvider mProvider;

	public DrawableVariable(String name, Drawable defaultValue, AssetProvider provider) {
		super(name, defaultValue);
		mDrawable = defaultValue;
		mProvider = provider;
	}

	@Override
	public Drawable asDrawable() throws TypeException {
		return mDrawable;
	}

	@Override
	public void set(Drawable value) throws TypeException {
		mDrawable = value;
		onChanged();
	}

	@Override
	public void set(String value) throws TypeException {
		for (Drawable d : mProvider.getDrawables()) {
			if (d.getName().equals(value)) {
				mDrawable = d;
				onChanged();
				return;
			}
		}
		throw new TypeException("No such drawable");
	}
	
	@Override
	public String asString() throws TypeException {
		return mDrawable.getName();
	}

	@Override
	public ValueType getType() {
		return ValueType.DRAWABLE;
	}

}
