package com.kevlanche.engine.game.state.value.variable;

import com.kevlanche.engine.game.state.value.ValueType;
import com.kevlanche.engine.game.state.value.variable.nameless.AbstractVariable;

public class FloatVariable extends AbstractVariable {

	private float mValue;

	public FloatVariable(float defaultValue) {
		super(defaultValue);
		mValue = defaultValue;
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

	@Override
	public void set(float value) throws TypeException {
		mValue = value;
		onChanged();
	}

	@Override
	public void set(int value) throws TypeException {
		set((float) value);
	}

	@Override
	public void set(String value) throws TypeException {
		try {
			set(Float.parseFloat(value));
		} catch (NumberFormatException e) {
			throw new TypeException(e);
		}
	}
}
