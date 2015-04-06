package com.kevlanche.engine.game.state.var;

import com.kevlanche.engine.game.assets.Drawable;

public abstract class AbstractValue implements Value {

	@Override
	public int asInt() throws TypeException {
		throw new TypeException();
	}

	@Override
	public float asFloat() throws TypeException {
		throw new TypeException();
	}

	@Override
	public String asString() throws TypeException {
		throw new TypeException();
	}

	@Override
	public boolean asBool() throws TypeException {
		throw new TypeException();
	}
	
	@Override
	public Drawable asDrawable() throws TypeException {
		throw new TypeException();
	}

}