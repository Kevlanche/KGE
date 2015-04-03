package com.kevlanche.engine.game.script.impl;

import com.kevlanche.engine.game.actor.Entity;
import com.kevlanche.engine.game.script.BaseScript;
import com.kevlanche.engine.game.script.CompileException;
import com.kevlanche.engine.game.script.CompiledScript;
import com.kevlanche.engine.game.script.ReloadListener;
import com.kevlanche.engine.game.script.Script;

public class JavaScript extends BaseScript implements CompiledScript {

	@Override
	public CompiledScript compile(Entity owner) throws CompileException {
		return this;
	}

	@Override
	public void tick() {
		
	}
}
