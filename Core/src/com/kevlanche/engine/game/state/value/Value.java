package com.kevlanche.engine.game.state.value;

import com.kevlanche.engine.game.assets.Drawable;
import com.kevlanche.engine.game.state.value.variable.TypeException;

public interface Value {

	int asInt() throws TypeException;

	float asFloat() throws TypeException;

	String asString();

	boolean asBool() throws TypeException;

	Drawable asDrawable() throws TypeException;

	ValueType getType();
}