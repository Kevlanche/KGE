package com.kevlanche.engine.game.state.value;

import com.kevlanche.engine.game.script.CompileException;
import com.kevlanche.engine.game.state.value.variable.TypeException;

public class FloatValue extends AbstractValue {

	private float mValue;

	public FloatValue(float value) {
		mValue = value;
	}

	@Override
	public ValueType getType() {
		return ValueType.FLOAT;
	}

	@Override
	public float asFloat() throws TypeException {
		return mValue;
	}

	@Override
	public int asInt() throws TypeException {
		return (int) asFloat();
	}

	@Override
	public String asString() throws TypeException {
		return Float.toString(asFloat());
	}
}
