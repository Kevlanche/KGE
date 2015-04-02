package com.kevlanche.engine.game;

import com.kevlanche.engine.game.actor.Actor;
import com.kevlanche.engine.game.script.CompileException;


public interface Entity<Compiled> {
	Compiled compile(Actor owner) throws CompileException;
}
