package com.kevlanche.engine.game.state.value.variable;

import com.kevlanche.engine.game.GameState;
import com.kevlanche.engine.game.actor.Entity;
import com.kevlanche.engine.game.script.CompileException;
import com.kevlanche.engine.game.state.value.Value;

public class NamedBoolVariable extends BoolVariable implements NamedVariable {

	private String mName;

	public NamedBoolVariable(String name, boolean defaultValue) {
		super(defaultValue);
		mName = name;
	}

	@Override
	public String getName() {
		return mName;
	}

	@Override
	public NamedVariable compile(GameState game, Entity owner)
			throws CompileException {
		return this;
	}
}
