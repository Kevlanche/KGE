package com.kevlanche.engine.game.state.value.variable;

import com.kevlanche.engine.game.state.value.ValueMap;
import com.kevlanche.engine.game.state.value.ValueType;
import com.kevlanche.engine.game.state.value.variable.nameless.AbstractVariable;

public class MapVariable extends AbstractVariable {

	private ValueMap mValue;

	public MapVariable(ValueMap defaultValue) {
		super(defaultValue);
		mValue = defaultValue;
	}

	@Override
	public ValueType getType() {
		return ValueType.MAP;
	}

	@Override
	public String asString() throws TypeException {
		return mValue.toString();
	}

	@Override
	public ValueMap asMap() throws TypeException {
		return mValue;
	}

	@Override
	public void set(ValueMap value) throws TypeException {
		mValue = value;
	}

	@Override
	public boolean hasDefaultValue() {
		// Don't want to save maps...?
		return true;
	}
}
