package com.kevlanche.engine.game.state.var;


public interface Value {

	int asInt() throws TypeException;
	float asFloat() throws TypeException;
	String asString() throws TypeException;

	ValueType getType();
}