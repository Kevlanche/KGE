package com.kevlanche.engine.game.state.value.variable;

import com.kevlanche.engine.game.Compilable;

public interface NamedVariable extends ObservableVariable,
		Compilable<NamedVariable> {
	String getName();
}
