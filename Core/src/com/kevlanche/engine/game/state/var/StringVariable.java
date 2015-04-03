package com.kevlanche.engine.game.state.var;

public class StringVariable extends AbstractVariable implements
		ClonableVariable {

	private final String mName;

	private String mValue;

	public StringVariable(String name, String defaultValue) {
		mName = name;
		mValue = defaultValue;
	}

	@Override
	public String getName() {
		return mName;
	}

	@Override
	public ValueType getType() {
		return ValueType.STRING;
	}

	@Override
	public String asString() throws TypeException {
		return mValue;
	}

	@Override
	public void set(int value) throws TypeException {
		set(Integer.toString(value));
	}

	@Override
	public void set(float value) throws TypeException {
		set(Float.toString(value));
	}

	@Override
	public int asInt() throws TypeException {
		try {
			return Integer.parseInt(mValue);
		} catch (NumberFormatException e) {
			throw new TypeException(e);
		}
	}

	@Override
	public float asFloat() throws TypeException {
		try {
			return Float.parseFloat(mValue);
		} catch (NumberFormatException e) {
			throw new TypeException(e);
		}
	}

	@Override
	public void set(String value) throws TypeException {
		mValue = value;
	}

	@Override
	public ClonableVariable createCopy() {
		return new StringVariable(mName, mValue);
	}
}
