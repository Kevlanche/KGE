package com.kevlanche.engine.game.state.value;

import com.kevlanche.engine.game.state.value.variable.TypeException;

public class MapValue extends AbstractValue {

	private ValueMap mValue;

	public MapValue(ValueMap value) {
		mValue = value;
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
}
