package com.kevlanche.engine.game.state.var;

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
}