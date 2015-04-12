package com.kevlanche.engine.game.state.value;

import com.kevlanche.engine.game.state.value.variable.TypeException;

public class BoolValue extends AbstractValue {

	private boolean mValue;

	public BoolValue(boolean value) {
		mValue = value;
	}

	@Override
	public ValueType getType() {
		return ValueType.BOOL;
	}

	@Override
	public int asInt() throws TypeException {
		return asBool() ? 1 : 0;
	}

	@Override
	public float asFloat() throws TypeException {
		return asInt();
	}

	@Override
	public String asString() throws TypeException {
		return Boolean.toString(asBool());
	}

	@Override
	public boolean asBool() throws TypeException {
		return mValue;
	}
}
