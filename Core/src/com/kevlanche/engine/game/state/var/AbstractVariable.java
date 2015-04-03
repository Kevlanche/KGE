package com.kevlanche.engine.game.state.var;

import com.kevlanche.engine.game.actor.Entity;
import com.kevlanche.engine.game.script.CompileException;

public abstract class AbstractVariable extends AbstractValue implements
		Variable {

	@Override
	public void set(int value) throws TypeException {
		throw new TypeException();
	}

	@Override
	public void set(float value) throws TypeException {
		throw new TypeException();
	}

	@Override
	public void set(String value) throws TypeException {
		throw new TypeException();
	}

	@Override
	public Variable compile(Entity owner) throws CompileException {
		return this;
	}

	@Override
	public void copy(Variable other) throws TypeException {
		switch (getType()) {
		case INTEGER:
			set(other.asInt());
			break;
		case FLOAT:
			set(other.asFloat());
			break;
		case STRING:
			set(other.asString());
			break;
		default:
			throw new TypeException("Don't know how to copy " + getType());
		}

	}
}