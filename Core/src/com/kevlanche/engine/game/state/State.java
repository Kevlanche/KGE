package com.kevlanche.engine.game.state;

import java.util.List;

import com.kevlanche.engine.game.Compilable;
import com.kevlanche.engine.game.state.var.Variable;

public interface State extends Compilable<State> {
	String getName();
	List<Variable> getVariables();
}
