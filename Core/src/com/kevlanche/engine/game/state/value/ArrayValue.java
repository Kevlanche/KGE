package com.kevlanche.engine.game.state.value;

import com.kevlanche.engine.game.state.value.variable.TypeException;
import com.kevlanche.engine.game.state.value.variable.Variable;

public class ArrayValue extends AbstractValue {

	private Variable[] mValue;

	public ArrayValue(Variable[] value) {
		mValue = value;
	}

	@Override
	public ValueType getType() {
		return ValueType.ARRAY;
	}

	@Override
	public Variable[] asArray() throws TypeException {
		return mValue;
	}

	@Override
	public String asString() {
		final StringBuilder ret = new StringBuilder('[');
		for (Value value : mValue) {
			ret.append(value.asString());
		}
		ret.append(']');
		return ret.toString();
	}
}
