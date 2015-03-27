package com.kevlanche.engine.game.script.var;

import java.util.Arrays;
import java.util.List;

import com.kevlanche.engine.game.script.ValueType;

public class IntVariable implements ScriptVariable {

	private final String mName;

	private final int mDefaultValue;

	public IntVariable(String name, int defaultValue) {
		mName = name;
		mDefaultValue = defaultValue;
	}

	@Override
	public Integer getDefaultValue() {
		return mDefaultValue;
	}

	@Override
	public String getName() {
		return mName;
	}

	@Override
	public ValueType getType() {
		return ValueType.INTEGER;
	}
}
