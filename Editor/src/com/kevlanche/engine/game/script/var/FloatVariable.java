package com.kevlanche.engine.game.script.var;

import com.kevlanche.engine.game.script.ValueType;

public class FloatVariable implements ScriptVariable {

	private final String mName;

	private final float mDefaultValue;

	public FloatVariable(String name, float defaultValue) {
		mName = name;
		mDefaultValue = defaultValue;
	}

	@Override
	public Float getDefaultValue() {
		return mDefaultValue;
	}

	@Override
	public String getName() {
		return mName;
	}

	@Override
	public ValueType getType() {
		return ValueType.FLOAT;
	}
}
