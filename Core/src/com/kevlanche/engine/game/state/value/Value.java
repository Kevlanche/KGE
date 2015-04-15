package com.kevlanche.engine.game.state.value;

import com.kevlanche.engine.game.assets.Drawable;
import com.kevlanche.engine.game.state.value.variable.TypeException;
import com.kevlanche.engine.game.state.value.variable.Variable;

public interface Value {

	int asInt() throws TypeException;

	float asFloat() throws TypeException;

	String asString();

	boolean asBool() throws TypeException;

	Drawable asDrawable() throws TypeException;

	Variable[] asArray() throws TypeException;

	Function asFunction() throws TypeException;

	ValueMap asMap() throws TypeException;

	ValueType getType();
}