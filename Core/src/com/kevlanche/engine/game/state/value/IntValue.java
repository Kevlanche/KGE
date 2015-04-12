package com.kevlanche.engine.game.state.value;

import com.kevlanche.engine.game.state.value.variable.TypeException;


public class IntValue extends AbstractValue {

	private int mValue;

	public IntValue(int value) {
		mValue = value;
	}

	@Override
	public ValueType getType() {
		return ValueType.INTEGER;
	}

	@Override
	public float asFloat() throws TypeException {
		return asInt();
	}

	@Override
	public int asInt() throws TypeException {
		return mValue;
	}

	@Override
	public String asString() throws TypeException {
		return Integer.toString(asInt());
	}
}
