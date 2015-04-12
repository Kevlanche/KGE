package com.kevlanche.engine.game.state.value.variable;

import com.kevlanche.engine.game.GameState;
import com.kevlanche.engine.game.actor.Entity;
import com.kevlanche.engine.game.script.CompileException;
import com.kevlanche.engine.game.state.value.variable.nameless.AbstractVariable;

public abstract class AbstractNamedVariable extends AbstractVariable implements
		NamedVariable {

	private final String mName;

	public AbstractNamedVariable(String name, Object defaultValue) {
		super(defaultValue);
		mName = name;
	}

	@Override
	public String getName() {
		return mName;
	}

	@Override
	public String toString() {
		return getName() + "=" + asString();
	}

	@Override
	public NamedVariable compile(GameState game, Entity owner)
			throws CompileException {
		return this;
	}
}