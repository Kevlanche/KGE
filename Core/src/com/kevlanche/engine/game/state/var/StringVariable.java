package com.kevlanche.engine.game.state.var;

public class StringVariable extends AbstractVariable {

	private String mValue;

	public StringVariable(String name, String defaultValue) {
		super(name);
		mValue = defaultValue;
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
	public void set(boolean value) throws TypeException {
		set(Boolean.toString(value));
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
	public boolean asBool() throws TypeException {
		return Boolean.parseBoolean(asString());
	}

	@Override
	public void set(String value) throws TypeException {
		mValue = value;
	}
}
