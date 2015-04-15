package com.kevlanche.engine.game.state.value;

import com.kevlanche.engine.game.assets.Drawable;
import com.kevlanche.engine.game.state.value.variable.TypeException;
import com.kevlanche.engine.game.state.value.variable.Variable;

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
	public boolean asBool() throws TypeException {
		throw new TypeException();
	}
	
	@Override
	public Drawable asDrawable() throws TypeException {
		throw new TypeException();
	}
	
	@Override
	public Variable[] asArray() throws TypeException {
		throw new TypeException();
	}
	
	@Override
	public Function asFunction() throws TypeException {
		throw new TypeException();
	}
	
	@Override
	public ValueMap asMap() throws TypeException {
		throw new TypeException();
	}
}