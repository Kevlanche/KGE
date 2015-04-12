package com.kevlanche.engine.game.script.impl;

import com.kevlanche.engine.game.GameState;
import com.kevlanche.engine.game.actor.Entity;
import com.kevlanche.engine.game.script.BaseScript;
import com.kevlanche.engine.game.script.CompileException;
import com.kevlanche.engine.game.script.CompiledScript;

public class JavaScript extends BaseScript implements CompiledScript {

	public JavaScript(String name) {
		super(name);
	}

	@Override
	public CompiledScript compile(GameState game, Entity owner) throws CompileException {
		return this;
	}

	@Override
	public void tick() {

	}
}
