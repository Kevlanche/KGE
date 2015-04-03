package com.kevlanche.engine.game.state.var;

import com.kevlanche.engine.game.Compilable;

public interface ClonableVariable extends Variable {
	ClonableVariable createCopy();
}
