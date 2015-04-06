package com.kevlanche.engine.game.state.var;

import com.kevlanche.engine.game.script.CompileException;


public class FloatVariable extends AbstractVariable  {

	private float mValue;

	public FloatVariable(String name, float defaultValue) {
		super(name);
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
		return (int)asFloat();
	}
	
	@Override
	public String asString() throws TypeException {
		return Float.toString(asFloat());
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
}
