package com.kevlanche.engine.game;

import com.kevlanche.engine.game.actor.Entity;
import com.kevlanche.engine.game.script.CompileException;


public interface Compilable<Compiled> {
	Compiled compile(GameState game, Entity owner) throws CompileException;
}
