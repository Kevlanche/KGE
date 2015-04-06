package com.kevlanche.engine.game.state.var;

public class BoolVariable extends AbstractVariable {

	private boolean mValue;

	public BoolVariable(String name, boolean defaultValue) {
		super(name);
		mValue = defaultValue;
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
	public void set(int value) throws TypeException {
		set(value != 0);
	}

	@Override
	public void set(float value) throws TypeException {
		set(value != 0);
	}

	@Override
	public void set(boolean value) throws TypeException {
		mValue = value;
	}

	@Override
	public void set(String value) throws TypeException {
		set(Boolean.parseBoolean(value));
	}

	@Override
	public boolean asBool() throws TypeException {
		return mValue;
	}
}
