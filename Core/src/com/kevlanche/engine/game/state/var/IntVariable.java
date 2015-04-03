package com.kevlanche.engine.game.state.var;


public class IntVariable extends AbstractVariable implements ClonableVariable {

	private final String mName;

	private int mValue;

	public IntVariable(String name, int defaultValue) {
		mName = name;
		mValue = defaultValue;
	}

	@Override
	public String getName() {
		return mName;
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
		return mValue;
	}

	@Override
	public String asString() throws TypeException {
		return Integer.toString(mValue);
	}

	@Override
	public void set(int value) throws TypeException {
		mValue = value;
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
	public ClonableVariable createCopy() {
		return new IntVariable(mName, mValue);
	}
}
