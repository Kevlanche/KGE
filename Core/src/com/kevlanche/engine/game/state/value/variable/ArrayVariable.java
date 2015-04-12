package com.kevlanche.engine.game.state.value.variable;

import com.kevlanche.engine.game.state.value.Value;
import com.kevlanche.engine.game.state.value.ValueType;
import com.kevlanche.engine.game.state.value.variable.nameless.AbstractVariable;

public class ArrayVariable extends AbstractVariable {

	private Variable[] mValue;

	public ArrayVariable(Variable[] defaultValue) {
		super(defaultValue);
		mValue = defaultValue;
	}

	@Override
	public ValueType getType() {
		return ValueType.ARRAY;
	}

	@Override
	public String asString() {
		final StringBuilder ret = new StringBuilder('[');
		boolean first = true;
		for (Value value : mValue) {
			ret.append(value.asString());

			if (!first) {
				ret.append(',');
			}
			first = false;
		}
		ret.append(']');
		return ret.toString();
	}

	@Override
	public Variable[] asArray() throws TypeException {
		return mValue;
	}

	@Override
	public void set(Variable[] value) throws TypeException {
		mValue = value;
		onChanged();
	}
}
