package com.kevlanche.engine.game.state.var;

import com.kevlanche.engine.game.Entity;

public interface ClonableVariable extends Variable {
	ClonableVariable createCopy();
}
