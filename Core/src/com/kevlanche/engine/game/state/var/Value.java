package com.kevlanche.engine.game.state.var;

import com.kevlanche.engine.game.assets.Drawable;

public interface Value {

	int asInt() throws TypeException;

	float asFloat() throws TypeException;

	String asString() throws TypeException;

	boolean asBool() throws TypeException;

	Drawable asDrawable() throws TypeException;

	ValueType getType();
}