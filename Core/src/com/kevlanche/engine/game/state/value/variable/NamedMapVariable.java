package com.kevlanche.engine.game.state.value.variable;

import com.kevlanche.engine.game.GameState;
import com.kevlanche.engine.game.actor.Entity;
import com.kevlanche.engine.game.script.CompileException;
import com.kevlanche.engine.game.state.value.ValueMap;

public class NamedMapVariable extends MapVariable implements NamedVariable {

	private final String mName;

	public NamedMapVariable(String name, ValueMap defaultValue) {
		super(defaultValue);
		mName = name;
	}

	@Override
	public NamedVariable compile(GameState game, Entity owner)
			throws CompileException {
		return this;
	}

	@Override
	public String getName() {
		return mName;
	}
}
