package com.kevlanche.engine.game.state.var;

import com.kevlanche.engine.game.Entity;

public interface Variable extends Value, Entity<Variable> {

	String getName();

	void set(int value) throws TypeException;
	void set(float value) throws TypeException;
	void set(String value) throws TypeException;
	
	void copy(Variable other) throws TypeException;
}
