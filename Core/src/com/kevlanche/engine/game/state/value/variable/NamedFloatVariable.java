package com.kevlanche.engine.game.state.value.variable;

import com.kevlanche.engine.game.GameState;
import com.kevlanche.engine.game.actor.Entity;
import com.kevlanche.engine.game.script.CompileException;
import com.kevlanche.engine.game.state.value.ValueType;

public class NamedFloatVariable extends FloatVariable implements NamedVariable {

	private final String mName;

	public NamedFloatVariable(String name, float defaultValue) {
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
