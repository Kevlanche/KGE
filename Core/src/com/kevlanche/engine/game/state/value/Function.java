package com.kevlanche.engine.game.state.value;

import com.kevlanche.engine.game.state.value.variable.Variable;

public interface Function {
	Object call(Variable...args);
}
