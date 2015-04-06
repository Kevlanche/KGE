package com.kevlanche.engine.game.state.var;

import com.kevlanche.engine.game.assets.Drawable;

public class DrawableVariable extends AbstractVariable {

	private Drawable mDrawable;

	public DrawableVariable(String name, Drawable defaultValue) {
		super(name);
		mDrawable = defaultValue;
	}

	@Override
	public Drawable asDrawable() throws TypeException {
		return mDrawable;
	}

	@Override
	public void set(Drawable value) throws TypeException {
		mDrawable = value;
	}
	
	// TODO set(String) -> grab from provider
	
	@Override
	public String asString() throws TypeException {
		return mDrawable.getName();
	}

	@Override
	public ValueType getType() {
		return ValueType.DRAWABLE;
	}

}
