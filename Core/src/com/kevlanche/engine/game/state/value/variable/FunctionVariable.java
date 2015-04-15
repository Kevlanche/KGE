package com.kevlanche.engine.game.state.value.variable;

import com.kevlanche.engine.game.state.value.Function;
import com.kevlanche.engine.game.state.value.ValueType;
import com.kevlanche.engine.game.state.value.variable.nameless.AbstractVariable;

public class FunctionVariable extends AbstractVariable {

	private Function mValue;

	public FunctionVariable(Function defaultValue) {
		super(defaultValue);
		mValue = defaultValue;
	}

	@Override
	public ValueType getType() {
		return ValueType.FUNCTION;
	}

	@Override
	public String asString() throws TypeException {
		return mValue.toString();
	}

	@Override
	public void set(Function value) throws TypeException {
		mValue = value;
	}

	@Override
	public Function asFunction() throws TypeException {
		return mValue;
	}
	
	@Override
	public boolean hasDefaultValue() {
		// Don't want to save function values
		return true;
	}
}
