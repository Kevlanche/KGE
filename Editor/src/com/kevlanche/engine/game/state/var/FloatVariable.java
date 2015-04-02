package com.kevlanche.engine.game.state.var;


public class FloatVariable extends AbstractVariable implements ClonableVariable {

	private final String mName;

	private float mValue;

	public FloatVariable(String name, float defaultValue) {
		mName = name;
		mValue = defaultValue;
	}

	@Override
	public String getName() {
		return mName;
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
		return (int)mValue;
	}
	
	@Override
	public String asString() throws TypeException {
		return Float.toString(mValue);
	}
	
	@Override
	public void set(float value) throws TypeException {
		mValue = value;
	}
	
	@Override
	public void set(int value) throws TypeException {
		set((float)value);
	}
	
	@Override
	public void set(String value) throws TypeException {
		try {
			set(Float.parseFloat(value));
		} catch (NumberFormatException e) {
			throw new TypeException(e);
		}
	}

	@Override
	public ClonableVariable createCopy() {
		return new FloatVariable(mName, mValue);
	}
}
