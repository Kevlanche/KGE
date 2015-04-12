package com.kevlanche.engine.game.state.value.variable;

import com.kevlanche.engine.game.state.value.ValueType;

public class IntVariable extends AbstractVariable {

	private int mValue;

	public IntVariable(String name, int defaultValue) {
		super(name, defaultValue);
		mValue = defaultValue;
	}

	@Override
	public ValueType getType() {
		return ValueType.INTEGER;
	}

	@Override
	public int asInt() throws TypeException {
		return mValue;
	}

	@Override
	public float asFloat() throws TypeException {
		return asInt();
	}

	@Override
	public String asString() throws TypeException {
		return Integer.toString(asInt());
	}

	@Override
	public boolean asBool() throws TypeException {
		return asInt() != 0;
	}

	@Override
	public void set(int value) throws TypeException {
		mValue = value;
		onChanged();
	}

	@Override
	public void set(float value) throws TypeException {
		set((int) value);
	}

	@Override
	public void set(String value) throws TypeException {
		try {
			set(Integer.parseInt(value));
		} catch (NumberFormatException e) {
			throw new TypeException(e);
		}
	}

	@Override
	public void set(boolean value) throws TypeException {
		set(value ? 1 : 0);
	}
}
