package com.kevlanche.engine.game.state.value;

import com.kevlanche.engine.game.state.value.variable.TypeException;

public class StringValue extends AbstractValue {

	private String mValue;

	public StringValue(String value) {
		mValue = value;
	}

	@Override
	public ValueType getType() {
		return ValueType.STRING;
	}

	@Override
	public int asInt() throws TypeException {
		try {
			return Integer.parseInt(asString());
		} catch (NumberFormatException e) {
			throw new TypeException(e);
		}
	}

	@Override
	public float asFloat() throws TypeException {
		try {
			return Float.parseFloat(asString());
		} catch (NumberFormatException e) {
			throw new TypeException(e);
		}
	}

	@Override
	public String asString() throws TypeException {
		return mValue;
	}

	@Override
	public boolean asBool() throws TypeException {
		if ("true".equalsIgnoreCase(mValue)) {
			return true;
		} else if ("false".equalsIgnoreCase(mValue)) {
			return false;
		} else {
			throw new TypeException(mValue + " is not a boolean");
		}
	}
}
